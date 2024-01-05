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
     * 获取 JedisClientConfig
     *
     * @param redisProperties redis配置
     * @return JedisClientConfig
     */
    public JedisClientConfig getJedisClientConfig(RedisProperties redisProperties) {
        DefaultJedisClientConfig.Builder builder = DefaultJedisClientConfig.builder();
        if (redisProperties.getUsername() != null && !redisProperties.getUsername().isEmpty()) {
            builder.user(redisProperties.getUsername());
        }
        if (redisProperties.getPassword() != null && !redisProperties.getPassword().isEmpty()) {
            builder.password(redisProperties.getPassword());
        }
        builder.timeoutMillis(redisProperties.getTimeout());
        builder.database(redisProperties.getDatabase());
        return builder.build();
    }

    public ConnectionPoolConfig getJedisConnectionPoolConfig(RedisProperties redisProperties) {
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinEvictableIdleDuration(pool.getMaxWait());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());
        return poolConfig;
    }

    public JedisPoolConfig getJedisPoolConfig(RedisProperties redisProperties) {
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinEvictableIdleDuration(pool.getMaxWait());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());
        return poolConfig;
    }

    /**
     * 获取 Sentinel JedisClientConfig
     *
     * @param redisProperties redis配置
     * @return JedisClientConfig
     */
    public JedisClientConfig getJedisSentinelClientConfig(RedisProperties redisProperties) {
        DefaultJedisClientConfig.Builder builder = DefaultJedisClientConfig.builder();
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        if (sentinel == null) {
            return builder.build();
        }

        builder.timeoutMillis(redisProperties.getTimeout());

        if (sentinel.getUsername() != null && !sentinel.getUsername().isEmpty()) {
            builder.user(sentinel.getUsername());
        }

        if (sentinel.getPassword() != null && !sentinel.getPassword().isEmpty()) {
            builder.password(sentinel.getPassword());
        }

        return builder.build();
    }
}
