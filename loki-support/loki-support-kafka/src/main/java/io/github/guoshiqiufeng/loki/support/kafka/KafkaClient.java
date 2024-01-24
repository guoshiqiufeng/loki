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
package io.github.guoshiqiufeng.loki.support.kafka;

import io.github.guoshiqiufeng.loki.constant.Constant;
import io.github.guoshiqiufeng.loki.support.core.LokiClient;
import io.github.guoshiqiufeng.loki.support.core.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
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
import java.util.concurrent.Future;

/**
 * kafka客户端
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/1/6 10:35
 */
public interface KafkaClient extends LokiClient {

    /**
     * 发送消息
     *
     * @param groupName 组名称
     * @param record    发送信息
     * @return 发送消息结果
     */
    @Override
    default CompletableFuture<ProducerResult> sendAsync(String groupName, io.github.guoshiqiufeng.loki.support.core.ProducerRecord record) {
        if (record == null) {
            throw new LokiException("sendAsync fail : record is null!");
        }
        List<Header> headers = new ArrayList<>();
        String tag = record.getTag();
        if (StringUtils.isNotEmpty(tag)) {
            headers.add(new RecordHeader(Constant.KAFKA_TAG, tag.getBytes(StandardCharsets.UTF_8)));
        }
        String key = null;
        if (record.getKeys() != null && !record.getKeys().isEmpty()) {
            key = record.getKeys().get(0);
        }
        ProducerRecord<String, String> kafkaRecord = new ProducerRecord<>(record.getTopic(),
                null, record.getDeliveryTimestamp(), key, record.getMessage(), headers);
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
     * 发送消息
     *
     * @param producerName 生产者名称
     * @param record       消息
     * @return Future
     */
    Future<RecordMetadata> send(String producerName, ProducerRecord<String, String> record);

    /**
     * 获取消费者
     *
     * @param consumerGroup 消费者组
     * @param index         消费者索引
     * @return 消费者
     */
    KafkaConsumer<String, String> getConsumer(String consumerGroup, Integer index);
}
