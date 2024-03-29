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

import com.google.common.base.Throwables;
import io.github.guoshiqiufeng.loki.MessageContent;
import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerConfig;
import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerRecord;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineUtils;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import io.github.guoshiqiufeng.loki.support.core.util.ThreadPoolUtils;
import io.github.guoshiqiufeng.loki.support.rocketmq.remoting.RocketRemotingClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/31 10:09
 */
@Slf4j
public abstract class BaseRocketRemotingClient implements RocketRemotingClient {

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
        Message message = covertMessage(producerRecord);
        SendResult recordMetadata = send(groupName, message);
        ProducerResult result = new ProducerResult();
        result.setTopic(message.getTopic());
        result.setMsgId(recordMetadata.getMsgId());
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
        Message message = covertMessage(producerRecord);
        return CompletableFuture.supplyAsync(() -> send(groupName, message))
                .thenApplyAsync(recordMetadata -> {
                    ProducerResult result = new ProducerResult();
                    result.setTopic(message.getTopic());
                    result.setMsgId(recordMetadata.getMsgId());
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
        String topicPattern = consumerConfig.getTopicPattern();
        String tag = consumerConfig.getTag();
        if (StringUtils.isEmpty(topic) && StringUtils.isEmpty(topicPattern)) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# pushMessageListener error: topic and topicPattern is both null");
            }
            return;
        }
        try {
            if (StringUtils.isEmpty(tag)) {
                tag = "*";
            }
            ExecutorService executorService = ThreadPoolUtils.getSingleThreadPool();
            String finalTag = tag;
            DefaultMQPushConsumer consumer = this.getConsumer(consumerConfig.getConsumerGroup(), consumerConfig.getIndex());
            CompletableFuture.runAsync(() -> {
                if (!StringUtils.isEmpty(topicPattern)) {

                } else {
                    try {
                        consumer.subscribe(topic, finalTag);
                        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                            try {
                                for (MessageExt msgExt : msgs) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("msgExt:{}", msgExt);
                                    }
                                    ConsumerRecord consumerRecord = covertConsumerRecord(msgExt);
                                    consumerRecord = PipelineUtils.processListener(consumerRecord);
                                    if (consumerRecord == null) {
                                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                                    }
                                    function.apply(new MessageContent<String>()
                                            .setMessageId(consumerRecord.getMessageId())
                                            //.setMessageGroup(msgExt.getProperty(MessageConst.PROPERTY_PRODUCER_GROUP))
                                            .setTopic(consumerRecord.getTopic())
                                            .setTag(consumerRecord.getTag())
                                            .setKeys(consumerRecord.getKeys())
                                            .setBody(consumerRecord.getBodyMessage())
                                            .setBodyMessage(consumerRecord.getBodyMessage()));
                                }
                            } catch (Exception e) {
                                if (log.isErrorEnabled()) {
                                    log.error("RocketMqHandler# pushMessageListener error:{}", Throwables.getStackTraceAsString(e));
                                }
                                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                            }
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        });
                        consumer.start();
                    } catch (Exception e) {
                        if (log.isErrorEnabled()) {
                            log.error("Exception consumer in registerMessageListener: {}", e.getMessage());
                        }
                    }
                }

            }, executorService).exceptionally(throwable -> {
                if (log.isErrorEnabled()) {
                    log.error("Exception occurred in CompletableFuture: {}", throwable.getMessage());
                }
                return null;
            });

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("RocketMqHandler# pushMessageListener error:{}", e.getMessage());
            }
            throw new RuntimeException(e);
        }
    }

    private ConsumerRecord covertConsumerRecord(MessageExt msgExt) {
        return new ConsumerRecord(msgExt.getTopic(), msgExt.getTags(),
                msgExt.getMsgId(), null, Arrays.asList(msgExt.getKeys().split(",")),
                new String(msgExt.getBody()));
    }

    private Message covertMessage(ProducerRecord producerRecord) {
        producerRecord = PipelineUtils.processSend(producerRecord);
        if (producerRecord == null) {
            throw new LokiException("producerRecord is null!");
        }
        Message message = new Message(producerRecord.getTopic(), producerRecord.getTag(), producerRecord.getMessage().getBytes());
        Long deliveryTimestamp = producerRecord.getDeliveryTimestamp();
        if (deliveryTimestamp != null && deliveryTimestamp != 0) {
            message.setDeliverTimeMs(System.currentTimeMillis() + deliveryTimestamp);
        }
        List<String> keys = producerRecord.getKeys();
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
