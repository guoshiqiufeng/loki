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
package io.github.guoshiqiufeng.loki.support.rocketmq.remoting.impl;

import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineUtils;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.rocketmq.remoting.RocketRemotingClient;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/31 10:09
 */
public abstract class BaseRocketRemotingClient implements RocketRemotingClient {

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
        Message message = covertMessage(record);
        SendResult recordMetadata = send(groupName, message);
        ProducerResult result = new ProducerResult();
        result.setTopic(message.getTopic());
        result.setMsgId(recordMetadata.getMsgId());
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
        Message message = covertMessage(record);
        return CompletableFuture.supplyAsync(() -> send(groupName, message))
                .thenApplyAsync(recordMetadata -> {
                    ProducerResult result = new ProducerResult();
                    result.setTopic(message.getTopic());
                    result.setMsgId(recordMetadata.getMsgId());
                    return result;
                });
    }

    private Message covertMessage(ProducerRecord record) {
        record = PipelineUtils.processSend(record);
        if(record == null) {
            throw new LokiException("record is null!");
        }
        Message message = new Message(record.getTopic(), record.getTag(), record.getMessage().getBytes());
        Long deliveryTimestamp = record.getDeliveryTimestamp();
        if (deliveryTimestamp != null && deliveryTimestamp != 0) {
            message.setDeliverTimeMs(System.currentTimeMillis() + deliveryTimestamp);
        }
        List<String> keys = record.getKeys();
        if (keys != null && !keys.isEmpty()) {
            message.setKeys(keys);
        }
        return message;
    }

    /**
     * 发送消息
     *
     * @param producerName 生产者名称
     * @param message      消息
     * @return 结果
     */
    abstract SendResult send(String producerName, Message message);
}
