package io.github.guoshiqiufeng.loki.core.handler.impl;

import io.github.guoshiqiufeng.loki.MessageContent;
import io.github.guoshiqiufeng.loki.constant.Constant;
import io.github.guoshiqiufeng.loki.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.core.handler.AbstractHandler;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.toolkit.ThreadPoolUtils;
import io.github.guoshiqiufeng.loki.enums.MqType;
import io.github.guoshiqiufeng.loki.support.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.rocketmq.shaded.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * redis消息处理器
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/20 09:40
 */
@Slf4j
public class RedisHandler extends AbstractHandler {
    private final RedisClient redisClient;

    /**
     * 构造函数
     *
     * @param properties    loki配置
     * @param handlerHolder 具体事件处理持有者
     */
    public RedisHandler(LokiProperties properties, HandlerHolder handlerHolder, RedisClient redisClient) {
        super(properties, handlerHolder);
        type = MqType.REDIS.getCode();
        this.redisClient = redisClient;
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
            log.error("RedisHandler# send message error: topic is null");
            return null;
        }
        if (StringUtils.isEmpty(body)) {
            log.error("RedisHandler# send message error: body is null");
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

            long publish = redisClient.publish(topic, body);
            log.info("publish:{}", publish);
            return null;
        } catch (Exception e) {
            log.error("RedisHandler# send message error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

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
    @Override
    public CompletableFuture<String> sendAsync(String producerName, String topic, String tag, String message, Long deliveryTimestamp, String... keys) {
        if (StringUtils.isEmpty(topic)) {
            log.error("RedisHandler# send message error: topic is null");
            return null;
        }
        if (StringUtils.isEmpty(message)) {
            log.error("RedisHandler# send message error: body is null");
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

            return CompletableFuture.supplyAsync(() -> "" + redisClient.publish(topic, message));
        } catch (Exception e) {
            log.error("RedisHandler# send message error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 消息监听
     *
     * @param consumerGroup          消费分组
     * @param index                  索引
     * @param topic                  消息主题
     * @param tag                    消息标签
     * @param consumptionThreadCount 消费线数
     * @param maxCacheMessageCount   最大缓存信息数
     * @param function               消息处理函数
     */
    @Override
    public void pushMessageListener(String consumerGroup, Integer index, String topic, String tag, Integer consumptionThreadCount, Integer maxCacheMessageCount, Function<MessageContent<String>, Void> function) {
        if (StringUtils.isEmpty(topic)) {
            log.error("RocketMqHandler# pushMessageListener error: topic is null");
            return;
        }
        try {
            if (StringUtils.isEmpty(tag)) {
                tag = "*";
            }
            ExecutorService executorService = ThreadPoolUtils.getSingleThreadPool();
            CompletableFuture.runAsync(() -> {
                redisClient.subscribe(record -> function.apply(new MessageContent<String>()
                        .setTopic(record.getTopic())
                        .setBody(record.getMessage())
                        .setBodyMessage(record.getMessage())
                ), topic);

            }, executorService).exceptionally(throwable -> {
                log.error("Exception occurred in CompletableFuture: {}", throwable.getMessage());
                return null;
            });

        } catch (Exception e) {
            log.error("RocketMqHandler# pushMessageListener error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
