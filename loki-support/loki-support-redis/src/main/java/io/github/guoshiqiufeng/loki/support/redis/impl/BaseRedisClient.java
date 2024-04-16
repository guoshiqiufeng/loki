/*
 * Copyright (c) 2023-2024, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.loki.support.redis.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import io.github.guoshiqiufeng.loki.MessageContent;
import io.github.guoshiqiufeng.loki.constant.Constant;
import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerConfig;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineUtils;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import io.github.guoshiqiufeng.loki.support.core.util.ThreadPoolUtils;
import io.github.guoshiqiufeng.loki.support.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/31 09:30
 */
@Slf4j
public abstract class BaseRedisClient implements RedisClient {

    /**
     * 发送消息
     *
     * @param groupName      组名称
     * @param producerRecord 发送信息
     * @return 发送消息结果
     */
    @Override
    public ProducerResult send(String groupName, ProducerRecord producerRecord) {
        if (producerRecord == null) {
            throw new LokiException("sendAsync fail : record is null!");
        }
        producerRecord = PipelineUtils.processSend(producerRecord);
        if (producerRecord == null) {
            throw new LokiException("producerRecord is null!");
        }
        ProducerRecord finalRecord = producerRecord;
        String msgId = IdUtil.fastSimpleUUID();
        if (producerRecord.getDeliveryTimestamp() != null && producerRecord.getDeliveryTimestamp() != 0) {
            producerRecord.setDeliveryTimestamp(System.currentTimeMillis() + producerRecord.getDeliveryTimestamp());
        }
        if (producerRecord.getDeliveryTimestamp() != null && producerRecord.getDeliveryTimestamp() > System.currentTimeMillis()) {
            // 定时发送
            set(Constant.REDIS_KEY_PREFIX + producerRecord.getTopic() + ":" + msgId, producerRecord.getMessage(), producerRecord.getDeliveryTimestamp());
            hset(Constant.REDIS_DELIVERY_KEY, Constant.REDIS_KEY_PREFIX + producerRecord.getTopic() + ":" + msgId, JSONUtil.toJsonStr(producerRecord));
            if (log.isDebugEnabled()) {
                log.debug("publish is delivery, msgId: {}", msgId);
            }
        } else {
            long publish = publish(finalRecord.getTopic(), finalRecord.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("publish msgId:{} result: {}", msgId, publish);
            }
        }

        ProducerResult result = new ProducerResult();
        result.setTopic(finalRecord.getTopic());

        result.setMsgId(msgId);
        return result;
    }

    /**
     * 发送消息
     *
     * @param groupName      组名称
     * @param producerRecord 发送信息
     * @return 发送消息结果
     */
    @Override
    public CompletableFuture<ProducerResult> sendAsync(String groupName, ProducerRecord producerRecord) {
        return CompletableFuture.supplyAsync(() -> send(groupName, producerRecord));
    }

    /**
     * 消费消息
     *
     * @param consumerConfig 消费配置
     * @param function       消费函数
     */
    @Override
    public void consumer(ConsumerConfig consumerConfig, Function<MessageContent<String>, Void> function) {
        String topic = consumerConfig.getTopic();
        String topicPattern = consumerConfig.getTopicPattern();
        String tag = consumerConfig.getTag();

        if (StringUtils.isEmpty(topic) && StringUtils.isEmpty(topicPattern)) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# pushMessageListener error: topic and topicPattern is both null");
            }
            return;
        }
        try {
            if (StringUtils.isEmpty(tag)) {
                tag = "*";
            }
            ExecutorService executorService = ThreadPoolUtils.getSingleThreadPool();
            CompletableFuture.runAsync(() -> {
                if (!StringUtils.isEmpty(topicPattern)) {
                    this.psubscribe(record -> function.apply(new MessageContent<String>()
                            .setTopic(record.getTopic())
                            .setBody(record.getBodyMessage())
                            .setBodyMessage(record.getBodyMessage())
                    ), topicPattern);
                } else {
                    this.subscribe(record -> function.apply(new MessageContent<String>()
                            .setTopic(record.getTopic())
                            .setBody(record.getBodyMessage())
                            .setBodyMessage(record.getBodyMessage())
                    ), topic);
                }
            }, executorService).exceptionally(throwable -> {
                if (log.isErrorEnabled()) {
                    log.error("Exception occurred in CompletableFuture: {}", throwable.getMessage());
                }
                return null;
            });

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# pushMessageListener error:{}", e.getMessage());
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 消息
     * @return 发布结果
     */
    abstract public long publish(String channel, String message);

    /**
     * 判断key是否存在
     *
     * @param key 建
     * @return
     */
    abstract public boolean exists(String key);

    /**
     * set
     *
     * @param key          建
     * @param value        值
     * @param expireAtTime 过期时间戳
     */
    abstract public void set(String key, String value, Long expireAtTime);

    /**
     * hset
     *
     * @param key   建
     * @param field 字段
     * @param value 值
     */
    abstract public void hset(String key, String field, String value);

    /**
     * hget
     *
     * @param key   建
     * @param field 字段
     * @return
     */
    abstract public String hget(String key, String field);

    /**
     * 删除
     *
     * @param key   建
     * @param field 字段
     */
    abstract public void hdel(String key, String... field);

    /**
     * hkeys
     *
     * @param key 建
     * @return
     */
    abstract public Set<String> hkeys(String key);

}
