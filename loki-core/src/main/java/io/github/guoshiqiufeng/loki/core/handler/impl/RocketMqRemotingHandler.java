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
import io.github.guoshiqiufeng.loki.core.handler.AbstractHandler;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.enums.MqType;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerConfig;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerResult;
import io.github.guoshiqiufeng.loki.support.rocketmq.remoting.RocketRemotingClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/18 11:10
 */
@Slf4j
public class RocketMqRemotingHandler extends AbstractHandler {

    private final RocketRemotingClient rocketRemotingClient;

    public RocketMqRemotingHandler(LokiProperties properties, HandlerHolder handlerHolder, RocketRemotingClient rocketRemotingClient) {
        super(properties, handlerHolder);
        type = MqType.ROCKET_MQ_REMOTING.getCode();
        this.rocketRemotingClient = rocketRemotingClient;
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
        if (!validateParameters(topic, body)) {
            return null;
        }
        // 发送消息
        try {
            ProducerRecord producerRecord = new ProducerRecord(topic, tag, body, deliveryTimestamp, Arrays.asList(keys));
            if (log.isDebugEnabled()) {
                log.debug("RocketMqRemotingHandler# send producerRecord:{}", producerRecord);
            }
            return rocketRemotingClient.send(producerName, producerRecord).getMsgId();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqRemotingHandler# send message error:{}", e.getMessage());
            }
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
        if (!validateParameters(topic, body)) {
            return null;
        }
        ProducerRecord producerRecord = new ProducerRecord(topic, tag, body, deliveryTimestamp, Arrays.asList(keys));
        if (log.isDebugEnabled()) {
            log.debug("RocketMqRemotingHandler# sendAsync producerRecord:{}", producerRecord);
        }
        // 发送消息
        try {
            return rocketRemotingClient.sendAsync(producerName, producerRecord).thenApply(ProducerResult::getMsgId);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqRemotingHandler# send message error:{}", e.getMessage());
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 消息监听
     *
     * @param consumerConfig 消费配置
     * @param function       消息处理函数
     */
    @Override
    public void pushMessageListener(ConsumerConfig consumerConfig, Function<MessageContent<String>, Void> function) {
        rocketRemotingClient.consumer(consumerConfig, function);
    }

}
