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

import io.github.guoshiqiufeng.loki.constant.Constant;
import io.github.guoshiqiufeng.loki.support.core.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import io.github.guoshiqiufeng.loki.support.kafka.KafkaClient;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/31 09:23
 */
public abstract class BaseKafkaClient implements KafkaClient {

    /**
     * 发送消息
     *
     * @param groupName 组名称
     * @param record    发送信息
     * @return 发送消息结果
     */
    @Override
    public ProducerResult send(String groupName, io.github.guoshiqiufeng.loki.support.core.ProducerRecord record) {
        try {
            return sendAsync(groupName, record).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送消息
     *
     * @param groupName 组名称
     * @param record    发送信息
     * @return 发送消息结果
     */
    @Override
    public CompletableFuture<ProducerResult> sendAsync(String groupName, io.github.guoshiqiufeng.loki.support.core.ProducerRecord record) {
        if (record == null) {
            throw new LokiException("sendAsync fail : record is null!");
        }
        ProducerRecord<String, String> kafkaRecord = covertKafkaRecord(record);
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
     * record 转换为 kafka record
     *
     * @param record record
     * @return kafka record
     */
    private ProducerRecord<String, String> covertKafkaRecord(io.github.guoshiqiufeng.loki.support.core.ProducerRecord record) {
        record = processSend(record);
        List<Header> headers = new ArrayList<>();
        String tag = record.getTag();
        Long deliveryTimestamp = record.getDeliveryTimestamp();
        Long timestamp = null;
        if (deliveryTimestamp != null && deliveryTimestamp != 0) {
            timestamp = System.currentTimeMillis() + deliveryTimestamp;
        }
        if (StringUtils.isNotEmpty(tag)) {
            headers.add(new RecordHeader(Constant.KAFKA_TAG, tag.getBytes(StandardCharsets.UTF_8)));
        }
        String key = null;
        if (record.getKeys() != null && !record.getKeys().isEmpty()) {
            key = record.getKeys().get(0);
        }
        return new ProducerRecord<>(record.getTopic(),
                null, timestamp, key, record.getMessage(), headers);
    }


    /**
     * 发送消息
     *
     * @param producerName 生产者名称
     * @param record       消息
     * @return Future
     */
    abstract Future<RecordMetadata> send(String producerName, ProducerRecord<String, String> record);


}
