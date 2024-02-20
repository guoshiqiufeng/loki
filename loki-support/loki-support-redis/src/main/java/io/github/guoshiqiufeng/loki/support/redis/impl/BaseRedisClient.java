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

import io.github.guoshiqiufeng.loki.MessageContent;
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
        long publish = publish(finalRecord.getTopic(), finalRecord.getMessage());
        ProducerResult result = new ProducerResult();
        result.setTopic(finalRecord.getTopic());
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
        if (record == null) {
            throw new LokiException("sendAsync fail : record is null!");
        }
        record = PipelineUtils.processSend(record);
        if (record == null) {
            throw new LokiException("record is null!");
        }
        ProducerRecord finalRecord = record;
        return CompletableFuture.supplyAsync(() -> publish(finalRecord.getTopic(), finalRecord.getMessage()))
                .thenApplyAsync(recordMetadata -> {
                    ProducerResult result = new ProducerResult();
                    result.setTopic(finalRecord.getTopic());
                    return result;
                });
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
}
