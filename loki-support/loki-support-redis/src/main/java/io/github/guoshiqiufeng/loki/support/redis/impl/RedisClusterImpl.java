package io.github.guoshiqiufeng.loki.support.redis.impl;

import io.github.guoshiqiufeng.loki.support.redis.RedisClient;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

/**
 * 集群版redis实现
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 17:26
 */
public class RedisClusterImpl implements RedisClient {

    private final JedisCluster jedisCluster;

    public RedisClusterImpl(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
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
        return jedisCluster.publish(channel, message);
    }

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param channels    频道
     */
    @Override
    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        jedisCluster.subscribe(jedisPubSub, channels);
    }
}
