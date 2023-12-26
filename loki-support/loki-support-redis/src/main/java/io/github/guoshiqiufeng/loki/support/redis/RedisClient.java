package io.github.guoshiqiufeng.loki.support.redis;

import io.github.guoshiqiufeng.loki.support.redis.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.util.function.Function;

/**
 * redis客户端
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 15:49
 */
public interface RedisClient {

    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 消息
     * @return 发布结果
     */
    long publish(String channel, String message);

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param channels    频道
     */
    public void subscribe(JedisPubSub jedisPubSub, String... channels);

    default public void subscribe(Function<ConsumerRecord, Void> function, String... channels) {
        subscribe(new JedisPubSub() {
            private final Logger log = LoggerFactory.getLogger(RedisClient.class);
            @Override
            public void onMessage(String channel, String message) {
                log.debug("{} onMessage : {}", channel, message);
                function.apply(new ConsumerRecord().setTopic(channel).setMessage(message));
            }

            @Override
            public void onPMessage(String pattern, String channel, String message) {
                log.debug("{} onPMessage : {}", channel, message);
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                log.debug("{} subscribe success", channel);
            }

            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                log.debug("{} unsubscribe success", channel);
            }

            @Override
            public void onPUnsubscribe(String pattern, int subscribedChannels) {
                log.debug("{} PUnsubscribe success", pattern);
            }

            @Override
            public void onPSubscribe(String pattern, int subscribedChannels) {
                log.debug("{} PSubscribe success", pattern);
            }
        }, channels);
    }
}
