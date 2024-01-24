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
package io.github.guoshiqiufeng.loki.support.rocketmq.remoting;

import io.github.guoshiqiufeng.loki.constant.Constant;
import io.github.guoshiqiufeng.loki.support.core.LokiClient;
import io.github.guoshiqiufeng.loki.support.core.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * rocketmq remoting client
 * <p>
 * 支持rocketmq 4.x,5.x的客户端
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/1/18 10:13
 */
public interface RocketRemotingClient extends LokiClient {


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
        Message message = new Message(record.getTopic(), record.getTag(), record.getMessage().getBytes());
        Long deliveryTimestamp = record.getDeliveryTimestamp();
        if (deliveryTimestamp != null && deliveryTimestamp != 0) {
            message.setDeliverTimeMs(System.currentTimeMillis() + deliveryTimestamp);
        }
        List<String> keys = record.getKeys();
        if (keys != null && !keys.isEmpty()) {
            message.setKeys(keys);
        }
        return CompletableFuture.supplyAsync(() -> send(groupName, message))
                .thenApplyAsync(recordMetadata -> {
                    ProducerResult result = new ProducerResult();
                    result.setTopic(record.getTopic());
                    result.setMsgId(recordMetadata.getMsgId());
                    return result;
                });
    }

    /**
     * 发送消息
     *
     * @param producerName 生产者名称
     * @param message      消息
     */
    SendResult send(String producerName, Message message);

    /**
     * 获取消费者
     *
     * @param consumerGroup 消费者组
     * @param index         消费者索引
     * @return 消费者
     */
    DefaultMQPushConsumer getConsumer(String consumerGroup, Integer index);
}
