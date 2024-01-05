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
package io.github.guoshiqiufeng.loki.core.handler.impl;

import io.github.guoshiqiufeng.loki.MessageContent;
import io.github.guoshiqiufeng.loki.constant.Constant;
import io.github.guoshiqiufeng.loki.core.toolkit.StringUtils;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.core.handler.AbstractHandler;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.toolkit.KafkaConfigUtils;
import io.github.guoshiqiufeng.loki.core.toolkit.KafkaConsumeUtils;
import io.github.guoshiqiufeng.loki.core.toolkit.ThreadPoolUtils;
import io.github.guoshiqiufeng.loki.enums.MqType;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * kafka消息处理器
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/14 09:19
 */
@Slf4j
public class KafkaHandler extends AbstractHandler {

    /**
     * 构造函数
     *
     * @param properties    loki配置
     * @param handlerHolder 具体事件处理持有者
     */
    public KafkaHandler(LokiProperties properties, HandlerHolder handlerHolder) {
        super(properties, handlerHolder);
        type = MqType.KAFKA.getCode();
        super.init();
    }

    /**
     * 发送消息
     *
     * @param producerName      生产者名称
     * @param topic             消息主题
     * @param tag               消息标签
     * @param body              消息内容
     * @param deliveryTimestamp 延时发送时间
     * @param keys              keys
     * @return messageId 消息id
     */
    @Override
    public String send(String producerName, String topic, String tag, String body, Long deliveryTimestamp, String... keys) {
        if (StringUtils.isEmpty(topic)) {
            log.error("KafkaHandler# send message error: topic is null");
            return null;
        }
        if (StringUtils.isEmpty(body)) {
            log.error("KafkaHandler# send message error: body is null");
            return null;
        }
        // 发送消息
        try {
            List<Header> headers = new ArrayList<>();
            if (StringUtils.isNotEmpty(tag)) {
                headers.add(new RecordHeader(Constant.KAFKA_TAG, tag.getBytes(StandardCharsets.UTF_8)));
            }
            Long timestamp = null;
            if (deliveryTimestamp != null && deliveryTimestamp != 0) {
                timestamp = System.currentTimeMillis() + deliveryTimestamp;
            }
            String key = null;
            if (keys != null && keys.length > 0) {
                key = keys[0];
            }
            KafkaProducer<String, String> producer = KafkaConfigUtils.getProducer(producerName, properties);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, timestamp, key, body, headers);
            return getMessageId(producer.send(record).get());
        } catch (Exception e) {
            log.error("KafkaHandler# send message error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /**
     * 异步发送消息
     *
     * @param producerName      生产者名称
     * @param topic             消息主题
     * @param tag               消息标签
     * @param body              消息内容
     * @param deliveryTimestamp 延时发送时间
     * @param keys              keys
     * @return messageId 消息id
     */
    @Override
    public CompletableFuture<String> sendAsync(String producerName, String topic, String tag, String body, Long deliveryTimestamp, String... keys) {
        if (StringUtils.isEmpty(topic)) {
            log.error("KafkaHandler# send message error: topic is null");
            return null;
        }
        if (StringUtils.isEmpty(body)) {
            log.error("KafkaHandler# send message error: body is null");
            return null;
        }
        // 发送消息
        try {
            List<Header> headers = new ArrayList<>();
            if (StringUtils.isNotEmpty(tag)) {
                headers.add(new RecordHeader(Constant.KAFKA_TAG, tag.getBytes(StandardCharsets.UTF_8)));
            }
            Long timestamp = null;
            if (deliveryTimestamp != null && deliveryTimestamp != 0) {
                timestamp = System.currentTimeMillis() + deliveryTimestamp;
            }
            String key = null;
            if (keys != null && keys.length > 0) {
                key = keys[0];
            }
            KafkaProducer<String, String> producer = KafkaConfigUtils.getProducer(producerName, properties);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, timestamp, key, body, headers);
            return CompletableFuture.supplyAsync(() -> {
                        try {
                            return producer.send(record).get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .thenApplyAsync(this::getMessageId);
        } catch (Exception e) {
            log.error("KafkaHandler# send message error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 消息监听
     *
     * @param consumerGroup          消费分组
     * @param index                  索引
     * @param topic                  消息主题
     * @param tag                    消息标签
     * @param consumptionThreadCount 消费线数
     * @param maxCacheMessageCount   最大缓存信息数
     * @param function               消息处理函数
     */
    @Override
    public void pushMessageListener(String consumerGroup, Integer index, String topic, String tag, Integer consumptionThreadCount, Integer maxCacheMessageCount, Function<MessageContent<String>, Void> function) {
        if (StringUtils.isEmpty(topic)) {
            log.error("RocketMqHandler# pushMessageListener error: topic is null");
            return;
        }
        try {
            KafkaConsumer<String, String> consumer = KafkaConfigUtils.getPushConsumerBuilder(properties, consumerGroup, index);
            if (StringUtils.isEmpty(tag)) {
                tag = "*";
            }
            ExecutorService executorService = ThreadPoolUtils.getSingleThreadPool();
            String finalTag = tag;
            CompletableFuture.runAsync(() -> {
                KafkaConsumeUtils.consumeMessage(consumer, topic, finalTag, record -> function.apply(new MessageContent<String>()
                        .setMessageId(getMessageId(record))
                        // .setMessageGroup(messageGroup)
                        .setTopic(record.topic())
                        .setTag(record.topic())
                        .setKeys(Collections.singletonList(record.key()))
                        .setBody(record.value())
                        .setBodyMessage(record.value())));
            }, executorService).exceptionally(throwable -> {
                log.error("Exception occurred in CompletableFuture: {}", throwable.getMessage());
                return null;
            });

        } catch (Exception e) {
            log.error("RocketMqHandler# pushMessageListener error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取消息id<br>
     * 使用partition和offset拼接
     *
     * @param recordMetadata recordMetadata
     * @return 消息id
     */
    private String getMessageId(RecordMetadata recordMetadata) {
        return recordMetadata.partition() + "_" + recordMetadata.offset();
    }

    private String getMessageId(ConsumerRecord<String, String> recordMetadata) {
        return recordMetadata.partition() + "_" + recordMetadata.offset();
    }

}
