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

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.SendReceipt;

import java.util.concurrent.CompletableFuture;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/18 10:07
 */
public interface RocketClient {

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