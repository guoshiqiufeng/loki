package io.github.guoshiqiufeng.loki.support.redis.impl;

import io.github.guoshiqiufeng.loki.support.redis.RedisClient;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

/**
 * 默认实现：单机版redis
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 17:34
 */
public class RedisDefaultImpl implements RedisClient {

    private final JedisPool jedisPool;

    public RedisDefaultImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 消息
     * @return 发布结果
     */
    @Override
    public long publish(String channel, String message) {
        return jedisPool.getResource().publish(channel, message);
    }

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param channels    频道
     */
    @Override
    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        jedisPool.getResource().subscribe(jedisPubSub, channels);
    }
}
