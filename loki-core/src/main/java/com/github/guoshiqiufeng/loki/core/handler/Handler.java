package com.github.guoshiqiufeng.loki.core.handler;

import com.github.guoshiqiufeng.loki.MessageContent;

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
     * @param topic   消息主题
     * @param message 消息内容
     * @return
     */
    CompletableFuture<String> sendAsync(String producerName, String topic, String tag, String message, Long deliveryTimestamp, String... keys);

    void pushMessageListener(String consumerGroup, String topic, String tag, Function<MessageContent<String>, Void> function);

}
