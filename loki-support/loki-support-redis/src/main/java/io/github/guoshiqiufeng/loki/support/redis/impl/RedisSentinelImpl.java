package io.github.guoshiqiufeng.loki.support.redis.impl;

import io.github.guoshiqiufeng.loki.support.redis.RedisClient;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisSentinelPool;

/**
 * 哨兵redis实现
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 17:29
 */
public class RedisSentinelImpl implements RedisClient {

    private final JedisSentinelPool jedisSentinelPool;

    public RedisSentinelImpl(JedisSentinelPool jedisSentinelPool) {
        this.jedisSentinelPool = jedisSentinelPool;
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
        return jedisSentinelPool.getResource().publish(channel, message);
    }

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param channels    频道
     */
    @Override
    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        jedisSentinelPool.getResource().subscribe(jedisPubSub, channels);
    }
}
