package io.github.guoshiqiufeng.loki.support.redis.impl.jedis;

import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerRecord;
import io.github.guoshiqiufeng.loki.support.redis.consumer.DefaultJedisPubSub;
import io.github.guoshiqiufeng.loki.support.redis.impl.BaseRedisClient;
import redis.clients.jedis.JedisPubSub;

import java.util.function.Function;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/3/19 17:28
 */
public abstract class BaseJedisClient extends BaseRedisClient {

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param channels    频道
     */
    abstract public void subscribe(JedisPubSub jedisPubSub, String... channels);

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param patterns    规则
     */
    abstract public void psubscribe(JedisPubSub jedisPubSub, String... patterns);

    /**
     * 订阅消息
     *
     * @param function 回调
     * @param channels 频道
     */
    @Override
    public void subscribe(Function<ConsumerRecord, Void> function, String... channels) {
        subscribe(new DefaultJedisPubSub(function), channels);
    }

    /**
     * 订阅消息
     *
     * @param function 回调
     * @param patterns 规则
     */
    @Override
    public void psubscribe(Function<ConsumerRecord, Void> function, String... patterns) {
        psubscribe(new DefaultJedisPubSub(function), patterns);
    }

}
