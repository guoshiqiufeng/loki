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
import io.github.guoshiqiufeng.loki.core.config.ConsumerConfig;
import io.github.guoshiqiufeng.loki.core.handler.AbstractHandler;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.toolkit.StringUtils;
import io.github.guoshiqiufeng.loki.enums.MqType;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.rocketmq.RocketClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;
import org.apache.rocketmq.shaded.com.google.common.base.Throwables;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * rocketMq 5.x消息处理器
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/16 14:18
 */
@Slf4j
public class RocketMqHandler extends AbstractHandler {

    private final RocketClient rocketClient;

    /**
     * 构造函数
     *
     * @param properties    loki配置
     * @param handlerHolder 具体事件处理持有者
     */
    public RocketMqHandler(LokiProperties properties, HandlerHolder handlerHolder, RocketClient rocketClient) {
        super(properties, handlerHolder);
        type = MqType.ROCKET_MQ.getCode();
        this.rocketClient = rocketClient;
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
        if (StringUtils.isEmpty(topic)) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# send message error: topic is null");
            }
            return null;
        }
        if (StringUtils.isEmpty(body)) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# send message error: body is null");
            }
            return null;
        }
        // 发送消息
        try {
            MessageBuilder messageBuilder = new MessageBuilderImpl()
                    .setTopic(topic);
            if (StringUtils.isNotEmpty(tag)) {
                messageBuilder.setTag(tag);
            }
            if (deliveryTimestamp != null && deliveryTimestamp != 0) {
                messageBuilder.setDeliveryTimestamp(System.currentTimeMillis() + deliveryTimestamp);
            }
            if (keys != null && keys.length > 0) {
                messageBuilder.setKeys(keys);
            }
            Message message = messageBuilder
                    .setMessageGroup(producerName)
                    .setBody(body.getBytes())
                    .build();
            if (log.isDebugEnabled()) {
                log.debug("RocketMqHandler# send message:{}", message);
            }
            SendReceipt send = rocketClient.send(producerName, message);
            if (log.isDebugEnabled()) {
                log.debug("RocketMqHandler# send messageId:{}", send.getMessageId());
            }
            return send.getMessageId().toString();
        } catch (ClientException e) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# send message error:{}", e.getMessage());
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
        if (StringUtils.isEmpty(topic)) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# send message error: topic is null");
            }
            return null;
        }
        if (StringUtils.isEmpty(body)) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# send message error: body is null");
            }
            return null;
        }
        // 发送消息
        try {
            MessageBuilder messageBuilder = new MessageBuilderImpl()
                    .setTopic(topic);
            if (StringUtils.isNotEmpty(tag)) {
                messageBuilder.setTag(tag);
            }
            if (deliveryTimestamp != null && deliveryTimestamp != 0) {
                messageBuilder.setDeliveryTimestamp(System.currentTimeMillis() + deliveryTimestamp);
            }
            if (keys != null && keys.length > 0) {
                messageBuilder.setKeys(keys);
            }
            Message message = messageBuilder
                    .setMessageGroup(producerName)
                    .setBody(body.getBytes())
                    .build();
            if (log.isDebugEnabled()) {
                log.debug("RocketMqHandler# send message:{}", message);
            }
            return rocketClient.sendAsync(producerName, message).thenApply(m -> m.getMessageId().toString());
        } catch (ClientException e) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# send message error:{}", e.getMessage());
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
        String topic = consumerConfig.getTopic();
        String tag = consumerConfig.getTag();

        if (StringUtils.isEmpty(topic)) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# pushMessageListener error: topic is null");
            }
            return;
        }
        try {
            PushConsumerBuilder pushConsumerBuilder = rocketClient.getConsumer(consumerConfig.getConsumerGroup(), consumerConfig.getIndex());
            if (StringUtils.isEmpty(tag)) {
                tag = "*";
            }
            FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
            pushConsumerBuilder
                    .setConsumerGroup(consumerConfig.getConsumerGroup())
                    // Set the subscription for the getConsumer.
                    .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                    .setConsumptionThreadCount(consumerConfig.getConsumptionThreadCount())
                    .setMaxCacheMessageCount(consumerConfig.getMaxCacheMessageCount())
                    .setMessageListener(messageView -> {
                        if (log.isDebugEnabled()) {
                            log.debug("Consume message={}", messageView);
                        }
                        MessageId messageId = messageView.getMessageId();
                        String messageGroup = messageView.getMessageGroup().orElse("");
                        String tagName = messageView.getTag().orElse("");
                        String topicName = messageView.getTopic();
                        Collection<String> keys = messageView.getKeys();
                        String body = StandardCharsets.UTF_8.decode(messageView.getBody()).toString();
                        try {
                            function.apply(new MessageContent<String>()
                                    .setMessageId(messageId.toString())
                                    .setMessageGroup(messageGroup)
                                    .setTopic(topicName)
                                    .setTag(tagName)
                                    .setKeys(keys)
                                    .setBody(body)
                                    .setBodyMessage(body));
                        } catch (Exception e) {
                            if (log.isErrorEnabled()) {
                                log.error("RocketMqHandler# pushMessageListener error:{}", Throwables.getStackTraceAsString(e));
                            }
                            return ConsumeResult.FAILURE;
                        }
                        return ConsumeResult.SUCCESS;
                    })
                    .build();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# pushMessageListener error:{}", e.getMessage());
            }
            throw new RuntimeException(e);
        }
    }
}
