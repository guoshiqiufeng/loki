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
import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerRecord;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineUtils;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import io.github.guoshiqiufeng.loki.support.core.util.ThreadPoolUtils;
import io.github.guoshiqiufeng.loki.support.redis.RedisClient;
import io.github.guoshiqiufeng.loki.support.redis.consumer.DefaultJedisPubSub;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.params.SetParams;

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
     * @param groupName 组名称
     * @param record    发送信息
     * @return 发送消息结果
     */
    @Override
    public ProducerResult send(String groupName, ProducerRecord record) {
        if (record == null) {
            throw new LokiException("sendAsync fail : record is null!");
        }
        record = PipelineUtils.processSend(record);
        if (record == null) {
            throw new LokiException("record is null!");
        }
        ProducerRecord finalRecord = record;
        String msgId = IdUtil.fastSimpleUUID();
        if (record.getDeliveryTimestamp() != null && record.getDeliveryTimestamp() != 0) {
            record.setDeliveryTimestamp(System.currentTimeMillis() + record.getDeliveryTimestamp());
        }
        if(record.getDeliveryTimestamp() != null && record.getDeliveryTimestamp() > System.currentTimeMillis()) {
            // 定时发送
            set(Constant.REDIS_KEY_PREFIX + record.getTopic() + ":"+ msgId, record.getMessage(), new SetParams().pxAt(record.getDeliveryTimestamp()));
            hset(Constant.REDIS_DELIVERY_KEY, Constant.REDIS_KEY_PREFIX + record.getTopic()+ ":"+ msgId, JSONUtil.toJsonStr(record));
            if(log.isDebugEnabled()) {
                log.debug("publish is delivery, msgId: {}", msgId);
            }
        } else {
            long publish = publish(finalRecord.getTopic(), finalRecord.getMessage());
            if(log.isDebugEnabled()) {
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
     * @param groupName 组名称
     * @param record    发送信息
     * @return 发送消息结果
     */
    @Override
    public CompletableFuture<ProducerResult> sendAsync(String groupName, ProducerRecord record) {
        return CompletableFuture.supplyAsync(() -> send(groupName, record));
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
    abstract long publish(String channel, String message);

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param channels    频道
     */
    abstract public void subscribe(JedisPubSub jedisPubSub, String... channels);

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param patterns    规则
     */
    abstract public void psubscribe(JedisPubSub jedisPubSub, String... patterns);

    /**
     * 订阅消息
     *
     * @param function 回调
     * @param channels 频道
     */
    @Override
    public void subscribe(Function<ConsumerRecord, Void> function, String... channels) {
        subscribe(new DefaultJedisPubSub(function), channels);
    }

    /**
     * 订阅消息
     *
     * @param function 回调
     * @param patterns 规则
     */
    @Override
    public void psubscribe(Function<ConsumerRecord, Void> function, String... patterns) {
        psubscribe(new DefaultJedisPubSub(function), patterns);
    }

    /**
     * 判断key是否存在
     * @param key 建
     * @return
     */
    abstract public boolean exists(String key);
    /**
     * set
     * @param key 建
     * @param value 值
     * @param params 参数
     */
    abstract public void set(String key, String value, SetParams params);

    /**
     * hset
     * @param key 建
     * @param field 字段
     * @param value 值
     */
    abstract public void hset(String key, String field, String value);

    /**
     * hget
     * @param key 建
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
