package com.github.guoshiqiufeng.loki.core.handler.impl;

import com.github.guoshiqiufeng.loki.MessageContent;
import com.github.guoshiqiufeng.loki.core.config.LokiProperties;
import com.github.guoshiqiufeng.loki.core.handler.AbstractHandler;
import com.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import com.github.guoshiqiufeng.loki.core.toolkit.ProductUtils;
import com.github.guoshiqiufeng.loki.enums.MqType;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;
import org.apache.rocketmq.shaded.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * rocketMq 5.x
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/16 14:18
 */
@Slf4j
public class RocketMqHandler extends AbstractHandler {


    public RocketMqHandler(LokiProperties properties, HandlerHolder handlerHolder) {
        super(properties, handlerHolder);
        type = MqType.ROCKET_MQ.getCode();
        super.init();
    }

    @Override
    public String send(String producerName, String topic, String tag, String body, Long deliveryTimestamp, String... keys) {
        if (StringUtils.isEmpty(topic)) {
            log.error("RocketMqHandler# send message error: topic is null");
            return null;
        }
        if (StringUtils.isEmpty(body)) {
            log.error("RocketMqHandler# send message error: body is null");
            return null;
        }
        // 发送消息
        try {
            Producer producer = ProductUtils.getProducer(producerName, properties);
            MessageBuilder messageBuilder = new MessageBuilderImpl()
                    .setTopic(topic);
            if (StringUtils.isNoneBlank(tag)) {
                messageBuilder.setTag(tag);
            }
            if (deliveryTimestamp != null && deliveryTimestamp != 0) {
                messageBuilder.setDeliveryTimestamp(System.currentTimeMillis() + deliveryTimestamp);
            }
            if (keys != null && keys.length > 0) {
                messageBuilder.setKeys(keys);
            }
            Message message = messageBuilder
                    .setBody(body.getBytes())
                    .build();
            log.debug("RocketMqHandler# send message:{}", message);
            SendReceipt send = producer.send(message);
            log.debug("RocketMqHandler# send messageId:{}", send.getMessageId());
            return send.getMessageId().toString();
        } catch (ClientException e) {
            log.error("RocketMqHandler# send message error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<String> sendAsync(String producerName, String topic, String tag, String body, Long deliveryTimestamp, String... keys) {
        if (StringUtils.isEmpty(topic)) {
            log.error("RocketMqHandler# send message error: topic is null");
            return null;
        }
        if (StringUtils.isEmpty(body)) {
            log.error("RocketMqHandler# send message error: body is null");
            return null;
        }
        // 发送消息
        try {
            Producer producer = ProductUtils.getProducer(producerName, properties);
            MessageBuilder messageBuilder = new MessageBuilderImpl()
                    .setTopic(topic);
            if (StringUtils.isNoneBlank(tag)) {
                messageBuilder.setTag(tag);
            }
            if (deliveryTimestamp != null && deliveryTimestamp != 0) {
                messageBuilder.setDeliveryTimestamp(System.currentTimeMillis() + deliveryTimestamp);
            }
            if (keys != null && keys.length > 0) {
                messageBuilder.setKeys(keys);
            }
            Message message = messageBuilder
                    .setBody(body.getBytes())
                    .build();
            log.debug("RocketMqHandler# send message:{}", message);
            return producer.sendAsync(message).thenApply(m -> m.getMessageId().toString());
        } catch (ClientException e) {
            log.error("RocketMqHandler# send message error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pushMessageListener(String consumerGroup, String topic, String tag, Function<MessageContent<String>, Void> function) {
        if (StringUtils.isEmpty(topic)) {
            log.error("RocketMqHandler# pushMessageListener error: topic is null");
            return;
        }
        try {
            PushConsumerBuilder pushConsumerBuilder = ProductUtils.getPushConsumerBuilder(properties);
            if (StringUtils.isEmpty(tag)) {
                tag = "*";
            }
            FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
            pushConsumerBuilder
                    .setConsumerGroup(consumerGroup)
                    // Set the subscription for the consumer.
                    .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                    .setMessageListener(messageView -> {
                        log.debug("Consume message={}", messageView);
                        MessageId messageId = messageView.getMessageId();
                        String messageGroup = messageView.getMessageGroup().orElse("");
                        String tagName = messageView.getTag().orElse("");
                        String topicName = messageView.getTopic();
                        Collection<String> keys = messageView.getKeys();
                        String body = StandardCharsets.UTF_8.decode(messageView.getBody()).toString();
                        // log.debug("messageId={}, messageGroup={}, tagName={}, topicName={}, body={}", messageId, messageGroup, tagName, topicName, body);
                        function.apply(new MessageContent<String>()
                                .setMessageId(messageId.toString())
                                .setMessageGroup(messageGroup)
                                .setTopic(topicName)
                                .setTag(tagName)
                                .setKeys(keys)
                                .setBody(body));
                        return ConsumeResult.SUCCESS;
                    })
                    .build();
        } catch (Exception e) {
            log.error("RocketMqHandler# pushMessageListener error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
