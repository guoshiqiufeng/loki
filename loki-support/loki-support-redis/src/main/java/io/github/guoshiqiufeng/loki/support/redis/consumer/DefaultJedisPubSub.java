package io.github.guoshiqiufeng.loki.support.redis.consumer;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPubSub;

import java.util.function.Function;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/10 17:31
 */
@Slf4j
public class DefaultJedisPubSub extends JedisPubSub {

    final Function<ConsumerRecord, Void> function;

    public DefaultJedisPubSub(Function<ConsumerRecord, Void> function) {
        this.function = function;
    }

    @Override
    public void onMessage(String channel, String message) {
        if (log.isDebugEnabled()) {
            log.debug("{} onMessage : {}", channel, message);
        }
        function.apply(new ConsumerRecord().setTopic(channel).setMessage(message));
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        if (log.isDebugEnabled()) {
            log.debug("pattern: {}  channel: {} onPMessage : {}", pattern, channel, message);
        }
        function.apply(new ConsumerRecord().setTopic(channel).setMessage(message));
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        if (log.isDebugEnabled()) {
            log.debug("{} subscribe success", channel);
        }
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        if (log.isDebugEnabled()) {
            log.debug("{} unsubscribe success", channel);
        }
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        if (log.isDebugEnabled()) {
            log.debug("{} PUnsubscribe success", pattern);
        }
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        if (log.isDebugEnabled()) {
            log.debug("{} PSubscribe success", pattern);
        }
    }

}
