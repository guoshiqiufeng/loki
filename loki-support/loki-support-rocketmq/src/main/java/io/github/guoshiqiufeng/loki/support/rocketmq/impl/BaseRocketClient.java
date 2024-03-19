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

import io.github.guoshiqiufeng.loki.MessageContent;
import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerConfig;
import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerRecord;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineUtils;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import io.github.guoshiqiufeng.loki.support.rocketmq.RocketClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;
import org.apache.rocketmq.shaded.com.google.common.base.Throwables;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/31 09:40
 */
@Slf4j
public abstract class BaseRocketClient implements RocketClient {

    /**
     * 发送消息
     *
     * @param groupName      组名称
     * @param producerRecord 发送信息
     * @return 发送消息结果
     */
    @Override
    public ProducerResult send(String groupName, ProducerRecord producerRecord) {
        if (producerRecord == null) {
            throw new LokiException("sendAsync fail : producerRecord is null!");
        }
        Message message = covertMessage(groupName, producerRecord);
        ProducerResult result = new ProducerResult();
        SendReceipt recordMetadata = send(groupName, message);
        result.setTopic(message.getTopic());
        result.setMsgId(recordMetadata.getMessageId().toString());
        return result;
    }

    /**
     * 发送消息
     *
     * @param groupName      组名称
     * @param producerRecord 发送信息
     * @return 发送消息结果
     */
    @Override
    public CompletableFuture<ProducerResult> sendAsync(String groupName, ProducerRecord producerRecord) {
        if (producerRecord == null) {
            throw new LokiException("sendAsync fail : producerRecord is null!");
        }
        Message message = covertMessage(groupName, producerRecord);
        return sendAsync(groupName, message)
                .thenApply(recordMetadata -> {
                    ProducerResult result = new ProducerResult();
                    result.setTopic(message.getTopic());
                    result.setMsgId(recordMetadata.getMessageId().toString());
                    return result;
                });
    }

    /**
     * 消费消息
     *
     * @param consumerConfig 消费配置
     * @param function       消费函数
     */
    @Override
    public void consumer(ConsumerConfig consumerConfig, Function<MessageContent<String>, Void> function) {
        String topic = consumerConfig.getTopic();
        String tag = consumerConfig.getTag();

        if (StringUtils.isEmpty(topic)) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# pushMessageListener error: topic is null");
            }
            return;
        }
        try {
            PushConsumerBuilder pushConsumerBuilder = this.getConsumer(consumerConfig.getConsumerGroup(), consumerConfig.getIndex());
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
                        ConsumerRecord consumerRecord = covertConsumerRecord(messageView);
                        consumerRecord = PipelineUtils.processListener(consumerRecord);
                        if (consumerRecord == null) {
                            return ConsumeResult.SUCCESS;
                        }
                        try {
                            function.apply(new MessageContent<String>()
                                    .setMessageId(consumerRecord.getMessageId())
                                    .setMessageGroup(consumerRecord.getMessageGroup())
                                    .setTopic(consumerRecord.getTopic())
                                    .setTag(consumerRecord.getTag())
                                    .setKeys(consumerRecord.getKeys())
                                    .setBody(consumerRecord.getBodyMessage())
                                    .setBodyMessage(consumerRecord.getBodyMessage()));
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

    private ConsumerRecord covertConsumerRecord(MessageView messageView) {
        MessageId messageId = messageView.getMessageId();
        String messageGroup = messageView.getMessageGroup().orElse("");
        String tagName = messageView.getTag().orElse("");
        String topicName = messageView.getTopic();
        String body = StandardCharsets.UTF_8.decode(messageView.getBody()).toString();
        return new ConsumerRecord(topicName, tagName, messageId.toString(), messageGroup,
                messageView.getKeys(), body);
    }

    private Message covertMessage(String groupName, ProducerRecord producerRecord) {
        producerRecord = PipelineUtils.processSend(producerRecord);
        if (producerRecord == null) {
            throw new LokiException("producerRecord is null!");
        }
        MessageBuilder messageBuilder = new MessageBuilderImpl()
                .setTopic(producerRecord.getTopic());
        if (StringUtils.isNotEmpty(producerRecord.getTag())) {
            messageBuilder.setTag(producerRecord.getTag());
        }
        Long deliveryTimestamp = producerRecord.getDeliveryTimestamp();
        if (deliveryTimestamp != null && deliveryTimestamp != 0) {
            messageBuilder.setDeliveryTimestamp(System.currentTimeMillis() + deliveryTimestamp);
        } else {
            messageBuilder.setMessageGroup(groupName);
        }
        List<String> keys = producerRecord.getKeys();
        if (keys != null && !keys.isEmpty()) {
            messageBuilder.setKeys(keys.toArray(new String[0]));
        }
        return messageBuilder
                .setBody(producerRecord.getMessage().getBytes())
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
