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
package io.github.guoshiqiufeng.loki.support.kafka.impl;

import io.github.guoshiqiufeng.loki.MessageContent;
import io.github.guoshiqiufeng.loki.constant.Constant;
import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerConfig;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineUtils;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import io.github.guoshiqiufeng.loki.support.core.util.ThreadPoolUtils;
import io.github.guoshiqiufeng.loki.support.kafka.KafkaClient;
import io.github.guoshiqiufeng.loki.support.kafka.utils.KafkaConsumeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/31 09:23
 */
@Slf4j
public abstract class BaseKafkaClient implements KafkaClient {

    /**
     * 发送消息
     *
     * @param groupName      组名称
     * @param producerRecord 发送信息
     * @return 发送消息结果
     */
    @Override
    public ProducerResult send(String groupName, io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord producerRecord) {
        try {
            return sendAsync(groupName, producerRecord).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送消息
     *
     * @param groupName      组名称
     * @param producerRecord 发送信息
     * @return 发送消息结果
     */
    @Override
    public CompletableFuture<ProducerResult> sendAsync(String groupName, io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord producerRecord) {
        if (producerRecord == null) {
            throw new LokiException("sendAsync fail : producerRecord is null!");
        }
        ProducerRecord<String, String> kafkaRecord = covertKafkaRecord(producerRecord);
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return send(groupName, kafkaRecord).get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new LokiException(e.getMessage());
                    }
                })
                .thenApplyAsync(recordMetadata -> {
                    ProducerResult result = new ProducerResult();
                    result.setTopic(recordMetadata.topic());
                    result.setMsgId(recordMetadata.partition() + "_" + recordMetadata.offset());
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
            String finalTag = tag;
            KafkaConsumer<String, String> consumer = this.getConsumer(consumerConfig.getConsumerGroup(), consumerConfig.getIndex());
            CompletableFuture.runAsync(() -> {
                if (!StringUtils.isEmpty(topicPattern)) {
                    KafkaConsumeUtils.consumeMessageForPattern(
                            consumer,
                            topicPattern, finalTag,
                            record -> function.apply(new MessageContent<String>()
                                    .setMessageId(record.getMessageId())
                                    // .setMessageGroup(messageGroup)
                                    .setTopic(record.getTopic())
                                    .setTag(record.getTag())
                                    .setKeys(record.getKeys())
                                    .setBody(record.getBodyMessage())
                                    .setBodyMessage(record.getBodyMessage())));
                } else {
                    KafkaConsumeUtils.consumeMessage(
                            consumer,
                            topic, finalTag,
                            record -> function.apply(new MessageContent<String>()
                                    .setMessageId(record.getMessageId())
                                    // .setMessageGroup(messageGroup)
                                    .setTopic(record.getTopic())
                                    .setTag(record.getTag())
                                    .setKeys(record.getKeys())
                                    .setBody(record.getBodyMessage())
                                    .setBodyMessage(record.getBodyMessage())));
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
     * record 转换为 kafka record
     *
     * @param producerRecord producerRecord
     * @return kafka producerRecord
     */
    private ProducerRecord<String, String> covertKafkaRecord(io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord producerRecord) {
        producerRecord = PipelineUtils.processSend(producerRecord);
        if (producerRecord == null) {
            throw new LokiException("producerRecord is null!");
        }
        List<Header> headers = new ArrayList<>();
        String tag = producerRecord.getTag();
        Long deliveryTimestamp = producerRecord.getDeliveryTimestamp();
        Long timestamp = null;
        if (deliveryTimestamp != null && deliveryTimestamp != 0) {
            timestamp = System.currentTimeMillis() + deliveryTimestamp;
        }
        if (StringUtils.isNotEmpty(tag)) {
            headers.add(new RecordHeader(Constant.KAFKA_TAG, tag.getBytes(StandardCharsets.UTF_8)));
        }
        String key = null;
        if (producerRecord.getKeys() != null && !producerRecord.getKeys().isEmpty()) {
            key = producerRecord.getKeys().get(0);
        }
        return new ProducerRecord<>(producerRecord.getTopic(),
                null, timestamp, key, producerRecord.getMessage(), headers);
    }


    /**
     * 发送消息
     *
     * @param producerName   生产者名称
     * @param producerRecord 消息
     * @return Future
     */
    abstract Future<RecordMetadata> send(String producerName, ProducerRecord<String, String> producerRecord);


}
