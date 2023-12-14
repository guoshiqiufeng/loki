/*
 * Copyright (c) 2023-2023, fubluesky (fubluesky@foxmail.com)
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
import io.github.guoshiqiufeng.loki.constant.Constant;
import io.github.guoshiqiufeng.loki.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.core.handler.AbstractHandler;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.enums.MqType;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.rocketmq.shaded.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 构造函数
     *
     * @param properties    loki配置
     * @param handlerHolder 具体事件处理持有者
     */
    public KafkaHandler(LokiProperties properties, HandlerHolder handlerHolder) {
        super(properties, handlerHolder);
        type = MqType.KAFKA.getCode();
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
            log.error("KafkaHandler# send message error: topic is null");
            return null;
        }
        if (StringUtils.isEmpty(body)) {
            log.error("KafkaHandler# send message error: body is null");
            return null;
        }
        // 发送消息
        try {
            List<Header> headers = new ArrayList<>();
            if (StringUtils.isNoneBlank(tag)) {
                headers.add(new RecordHeader(Constant.KAFKA_TAG, tag.getBytes(StandardCharsets.UTF_8)));
            }
            Long timestamp = null;
            if (deliveryTimestamp != null && deliveryTimestamp != 0) {
                timestamp = System.currentTimeMillis() + deliveryTimestamp;
            }
            String key = null;
            if (keys != null && keys.length > 0) {
                key = keys[0];
            }
            ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(new ProducerRecord<>(topic, null, timestamp, key, body, headers));
            SendResult<String, String> stringStringSendResult = send.get();
            return null;
        } catch (Exception e) {
            log.error("KafkaHandler# send message error:{}", e.getMessage());
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
            log.error("KafkaHandler# send message error: topic is null");
            return null;
        }
        if (StringUtils.isEmpty(body)) {
            log.error("KafkaHandler# send message error: body is null");
            return null;
        }
        // 发送消息
        try {
            List<Header> headers = new ArrayList<>();
            if (StringUtils.isNoneBlank(tag)) {
                headers.add(new RecordHeader(Constant.KAFKA_TAG, tag.getBytes(StandardCharsets.UTF_8)));
            }
            Long timestamp = null;
            if (deliveryTimestamp != null && deliveryTimestamp != 0) {
                timestamp = System.currentTimeMillis() + deliveryTimestamp;
            }
            String key = null;
            if (keys != null && keys.length > 0) {
                key = keys[0];
            }
            ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(new ProducerRecord<>(topic, null, timestamp, key, body, headers));

            return null;
        } catch (Exception e) {
            log.error("KafkaHandler# send message error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 消息监听
     *
     * @param consumerGroup          消费分组
     * @param topic                  消息主题
     * @param tag                    消息标签
     * @param consumptionThreadCount 消费线数
     * @param maxCacheMessageCount   最大缓存信息数
     * @param function               消息处理函数
     */
    @Override
    public void pushMessageListener(String consumerGroup, String topic, String tag, Integer consumptionThreadCount, Integer maxCacheMessageCount, Function<MessageContent<String>, Void> function) {

    }


}
