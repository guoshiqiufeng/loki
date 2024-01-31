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
package io.github.guoshiqiufeng.loki.support.rocketmq.impl;

import io.github.guoshiqiufeng.loki.support.core.ProducerRecord;
import io.github.guoshiqiufeng.loki.support.core.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import io.github.guoshiqiufeng.loki.support.rocketmq.RocketClient;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/31 09:40
 */
public abstract class BaseRocketClient implements RocketClient {

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
        Message message = covertMessage(groupName, record);
        ProducerResult result = new ProducerResult();
        SendReceipt recordMetadata = send(groupName, message);
        result.setTopic(message.getTopic());
        result.setMsgId(recordMetadata.getMessageId().toString());
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
        Message message = covertMessage(groupName, record);
        return sendAsync(groupName, message)
                .thenApply(recordMetadata -> {
                    ProducerResult result = new ProducerResult();
                    result.setTopic(message.getTopic());
                    result.setMsgId(recordMetadata.getMessageId().toString());
                    return result;
                });
    }

    private Message covertMessage(String groupName, ProducerRecord record) {
        record = processSend(record);
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
        return messageBuilder
                .setBody(record.getMessage().getBytes())
                .build();
    }

    /**
     * 发送消息
     *
     * @param producerName producerName
     * @param message      消息
     * @return 结果
     */
    abstract SendReceipt send(String producerName, Message message);

    /**
     * 异步发送消息
     *
     * @param producerName producerName
     * @param message      消息
     * @return 结果
     */
    abstract CompletableFuture<SendReceipt> sendAsync(String producerName, Message message);

}
