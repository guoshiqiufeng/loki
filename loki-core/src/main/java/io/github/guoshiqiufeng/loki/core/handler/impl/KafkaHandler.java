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
import io.github.guoshiqiufeng.loki.core.toolkit.ThreadPoolUtils;
import io.github.guoshiqiufeng.loki.enums.MqType;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import io.github.guoshiqiufeng.loki.support.kafka.KafkaClient;
import io.github.guoshiqiufeng.loki.support.kafka.utils.KafkaConsumeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * kafka消息处理器
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/14 09:19
 */
@Slf4j
public class KafkaHandler extends AbstractHandler {

    final KafkaClient kafkaClient;

    /**
     * 构造函数
     *
     * @param properties    loki配置
     * @param handlerHolder 具体事件处理持有者
     * @param kafkaClient   kafka客户端
     */
    public KafkaHandler(LokiProperties properties, HandlerHolder handlerHolder, KafkaClient kafkaClient) {
        super(properties, handlerHolder);
        type = MqType.KAFKA.getCode();
        this.kafkaClient = kafkaClient;
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
                log.error("KafkaHandler# send message error: topic is null");
            }
            return null;
        }
        if (StringUtils.isEmpty(body)) {
            if (log.isErrorEnabled()) {
                log.error("KafkaHandler# send message error: body is null");
            }
            return null;
        }
        // 发送消息
        try {
            ProducerRecord record = new ProducerRecord(topic, tag, body, deliveryTimestamp, Arrays.asList(keys));
            return kafkaClient.sendAsync(producerName, record).get().getMsgId();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("KafkaHandler# send message error:{}", e.getMessage());
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
                log.error("KafkaHandler# send message error: topic is null");
            }
            return null;
        }
        if (StringUtils.isEmpty(body)) {
            if (log.isErrorEnabled()) {
                log.error("KafkaHandler# send message error: body is null");
            }
            return null;
        }
        // 发送消息
        try {
            ProducerRecord record = new ProducerRecord(topic, tag, body, deliveryTimestamp, Arrays.asList(keys));
            return kafkaClient.sendAsync(producerName, record).thenApply(ProducerResult::getMsgId);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("KafkaHandler# send message error:{}", e.getMessage());
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
            KafkaConsumer<String, String> consumer = kafkaClient.getConsumer(consumerConfig.getConsumerGroup(), consumerConfig.getIndex());
            CompletableFuture.runAsync(() -> {
                if (!StringUtils.isEmpty(topicPattern)) {
                    KafkaConsumeUtils.consumeMessageForPattern(
                            consumer,
                            topicPattern, finalTag,
                            record -> function.apply(new MessageContent<String>()
                                    .setMessageId(record.getMessageId())
                                    // .setMessageGroup(messageGroup)
                                    .setTopic(record.getTopic())
                                    .setTag(record.getTag())
                                    .setKeys(record.getKeys())
                                    .setBody(record.getBodyMessage())
                                    .setBodyMessage(record.getBodyMessage())));
                } else {
                    KafkaConsumeUtils.consumeMessage(
                            consumer,
                            topic, finalTag,
                            record -> function.apply(new MessageContent<String>()
                                    .setMessageId(record.getMessageId())
                                    // .setMessageGroup(messageGroup)
                                    .setTopic(record.getTopic())
                                    .setTag(record.getTag())
                                    .setKeys(record.getKeys())
                                    .setBody(record.getBodyMessage())
                                    .setBodyMessage(record.getBodyMessage())));
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

}
