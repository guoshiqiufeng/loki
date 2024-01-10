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
package io.github.guoshiqiufeng.loki.core.handler;

import io.github.guoshiqiufeng.loki.MessageContent;
import io.github.guoshiqiufeng.loki.core.config.ConsumerConfig;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 消息处理事件接口
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/16 14:08
 */
public interface Handler {

    /**
     * 发送消息
     *
     * @param topic   消息主题
     * @param message 消息内容
     * @return messageId 消息id
     */
    default String send(String topic, String message) {
        return send(topic, null, message);
    }

    /**
     * 发送消息
     *
     * @param topic   消息主题
     * @param tag     消息标签
     * @param message 消息内容
     * @return messageId 消息id
     */
    default String send(String topic, String tag, String message) {
        return send(null, topic, tag, message);
    }


    /**
     * 发送消息
     *
     * @param producerName 生产者名称
     * @param topic        消息主题
     * @param tag          消息标签
     * @param message      消息内容
     * @return messageId 消息id
     */
    default String send(String producerName, String topic, String tag, String message) {
        return send(producerName, topic, tag, message, null);
    }

    /**
     * 发送消息
     *
     * @param producerName      生产者名称
     * @param topic             消息主题
     * @param tag               消息标签
     * @param message           消息内容
     * @param deliveryTimestamp 延时发送时间
     * @param keys              keys
     * @return messageId 消息id
     */
    String send(String producerName, String topic, String tag, String message, Long deliveryTimestamp, String... keys);

    /**
     * 异步发送消息
     *
     * @param producerName      生产者名称
     * @param topic             消息主题
     * @param tag               消息标签
     * @param message           消息内容
     * @param deliveryTimestamp 延时发送时间
     * @param keys              keys
     * @return messageId 消息id
     */
    CompletableFuture<String> sendAsync(String producerName, String topic, String tag, String message, Long deliveryTimestamp, String... keys);

    /**
     * 消息监听
     *
     * @param consumerConfig         消费配置
     * @param function               消息处理函数
     */
    void pushMessageListener(ConsumerConfig consumerConfig,
                             Function<MessageContent<String>, Void> function);

}
