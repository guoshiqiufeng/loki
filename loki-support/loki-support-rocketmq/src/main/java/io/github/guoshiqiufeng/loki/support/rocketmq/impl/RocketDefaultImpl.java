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

import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.rocketmq.RocketClient;
import io.github.guoshiqiufeng.loki.support.rocketmq.util.RocketMqConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;

import java.util.concurrent.CompletableFuture;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/18 16:08
 */
@Slf4j
public class RocketDefaultImpl implements RocketClient {

    private final LokiProperties lokiProperties;

    public RocketDefaultImpl(LokiProperties lokiProperties) {
        this.lokiProperties = lokiProperties;
    }

    /**
     * 发送消息
     *
     * @param producerName
     * @param message
     */
    @Override
    public SendReceipt send(String producerName, Message message) throws ClientException {
        Producer producer = RocketMqConfigUtils.getProducer(producerName, lokiProperties);
        return producer.send(message);
    }

    /**
     * 异步发送消息
     *
     * @param producerName
     * @param message
     * @return
     */
    @Override
    public CompletableFuture<SendReceipt> sendAsync(String producerName, Message message) throws ClientException {
        Producer producer = RocketMqConfigUtils.getProducer(producerName, lokiProperties);
        return producer.sendAsync(message);
    }

    /**
     * 获取消费者
     *
     * @param consumerGroup 消费者组
     * @param index         消费者索引
     * @return 消费者
     */
    @Override
    public PushConsumerBuilder getConsumer(String consumerGroup, Integer index) {
        return RocketMqConfigUtils.getPushConsumerBuilder(lokiProperties);
    }
}
