package io.github.guoshiqiufeng.loki.support.redis.utils;

import io.github.guoshiqiufeng.loki.support.redis.config.RedisProperties;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/5 14:26
 */
@UtilityClass
public class RedisConfigUtils {

    /**
     * 获取 JedisClientConfig 配置。
     *
     * @param redisProperties Redis 配置属性
     * @return JedisClientConfig 配置
     */
    public JedisClientConfig getJedisClientConfig(RedisProperties redisProperties) {
        DefaultJedisClientConfig.Builder builder = DefaultJedisClientConfig.builder();

        // 设置用户名
        if (redisProperties.getUsername() != null && !redisProperties.getUsername().isEmpty()) {
            builder.user(redisProperties.getUsername());
        }

        // 设置密码
        if (redisProperties.getPassword() != null && !redisProperties.getPassword().isEmpty()) {
            builder.password(redisProperties.getPassword());
        }

        // 设置超时时间和数据库
        if(redisProperties.getTimeout() != null) {
            builder.timeoutMillis((int) redisProperties.getTimeout().toMillis());
        }
        if(redisProperties.getConnectTimeout() != null) {
            builder.connectionTimeoutMillis((int) redisProperties.getConnectTimeout().toMillis());
        }
        builder.database(redisProperties.getDatabase());

        return builder.build();
    }

    /**
     * 获取 Jedis 连接池配置。
     *
     * @param redisProperties Redis 配置属性
     * @return Jedis 连接池配置
     */
    public ConnectionPoolConfig getJedisConnectionPoolConfig(RedisProperties redisProperties) {
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();

        // 设置连接池参数
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinEvictableIdleDuration(pool.getMaxWait());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());

        return poolConfig;
    }

    /**
     * 获取 Jedis 连接池配置。
     *
     * @param pool Redis 连接池配置属性
     * @return Jedis 连接池配置
     */
    public JedisPoolConfig getJedisPoolConfig(RedisProperties.Pool pool) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        // 设置连接池参数
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinEvictableIdleDuration(pool.getMaxWait());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());

        return poolConfig;
    }

    /**
     * 获取 Sentinel JedisClientConfig 配置。
     *
     * @param redisProperties Redis 配置属性
     * @return Sentinel JedisClientConfig 配置
     */
    public JedisClientConfig getJedisSentinelClientConfig(RedisProperties redisProperties) {
        DefaultJedisClientConfig.Builder builder = DefaultJedisClientConfig.builder();
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();

        // 如果没有 Sentinel 配置，则返回默认配置
        if (sentinel == null) {
            return builder.build();
        }

        // 设置超时时间
        if(redisProperties.getTimeout() != null) {
            builder.timeoutMillis((int) redisProperties.getTimeout().toMillis());
        }
        if(redisProperties.getConnectTimeout() != null) {
            builder.connectionTimeoutMillis((int) redisProperties.getConnectTimeout().toMillis());
        }

        // 设置 Sentinel 用户名
        if (sentinel.getUsername() != null && !sentinel.getUsername().isEmpty()) {
            builder.user(sentinel.getUsername());
        }

        // 设置 Sentinel 密码
        if (sentinel.getPassword() != null && !sentinel.getPassword().isEmpty()) {
            builder.password(sentinel.getPassword());
        }

        return builder.build();
    }

}
