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
package io.github.guoshiqiufeng.loki.support.rocketmq;

import io.github.guoshiqiufeng.loki.constant.Constant;
import io.github.guoshiqiufeng.loki.support.core.LokiClient;
import io.github.guoshiqiufeng.loki.support.core.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/18 10:07
 */
public interface RocketClient extends LokiClient {

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
        MessageBuilder messageBuilder = new MessageBuilderImpl()
                .setTopic(record.getTopic());
        if (StringUtils.isNotEmpty(record.getTag())) {
            messageBuilder.setTag(record.getTag());
        }
        Long deliveryTimestamp = record.getDeliveryTimestamp();
        if (deliveryTimestamp != null && deliveryTimestamp != 0) {
            messageBuilder.setDeliveryTimestamp(System.currentTimeMillis() + deliveryTimestamp);
        } else {
            messageBuilder.setMessageGroup(groupName);
        }
        List<String> keys = record.getKeys();
        if (keys != null && !keys.isEmpty()) {
            messageBuilder.setKeys(keys.toArray(new String[0]));
        }
        Message message = messageBuilder
                .setBody(record.getMessage().getBytes())
                .build();
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return send(groupName, message);
                    } catch (ClientException e) {
                        throw new LokiException(e.getMessage());
                    }
                })
                .thenApplyAsync(recordMetadata -> {
                    ProducerResult result = new ProducerResult();
                    result.setTopic(record.getTopic());
                    result.setMsgId(recordMetadata.getMessageId().toString());
                    return result;
                });
    }

    /**
     * 发送消息
     */
    SendReceipt send(String producerName, Message message) throws ClientException;

    /**
     * 异步发送消息
     *
     * @param producerName
     * @param message
     * @return
     */
    CompletableFuture<SendReceipt> sendAsync(String producerName, Message message) throws ClientException;

    /**
     * 获取消费者
     *
     * @param consumerGroup 消费者组
     * @param index         消费者索引
     * @return 消费者
     */
    PushConsumerBuilder getConsumer(String consumerGroup, Integer index);
}
