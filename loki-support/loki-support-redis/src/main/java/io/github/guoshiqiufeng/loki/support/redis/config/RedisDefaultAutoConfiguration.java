/*
 * Copyright (c) 2023-2024, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.loki.support.redis.config;

import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.redis.RedisClient;
import io.github.guoshiqiufeng.loki.support.redis.impl.jedis.RedisClusterImpl;
import io.github.guoshiqiufeng.loki.support.redis.impl.jedis.RedisDefaultImpl;
import io.github.guoshiqiufeng.loki.support.redis.impl.jedis.RedisSentinelImpl;
import io.github.guoshiqiufeng.loki.support.redis.utils.RedisConfigUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/3/19 17:51
 */
@Configuration
@ConditionalOnMissingClass("org.springframework.data.redis.core.RedisOperations")
@ConditionalOnProperty(name = "loki.redis.support-type", havingValue = "default", matchIfMissing = true)
public class RedisDefaultAutoConfiguration {

    /**
     * 创建并返回一个 Jedis 连接池对象。
     *
     * @param redisProperties Redis 相关配置属性
     * @return Jedis 连接池对象
     */
    public JedisPool redisPoolFactory(RedisProperties redisProperties) {
        // 获取 Jedis 连接池配置
        JedisPoolConfig jedisPoolConfig = RedisConfigUtils.getJedisPoolConfig(redisProperties.getJedis().getPool());
        // 获取 Jedis 客户端配置
        JedisClientConfig clientConfig = RedisConfigUtils.getJedisClientConfig(redisProperties);
        // 使用配置创建并返回 Jedis 连接池对象
        return new JedisPool(jedisPoolConfig, new HostAndPort(redisProperties.getHost(),
                redisProperties.getPort()), clientConfig);
    }

    /**
     * 创建并返回一个 Jedis Sentinel 连接池对象。
     *
     * @param redisProperties Redis 相关配置属性
     * @return Jedis Sentinel 连接池对象
     */
    public JedisSentinelPool jedisSentinelPool(RedisProperties redisProperties) {
        // 获取 Jedis 连接池配置
        JedisPoolConfig jedisPoolConfig = RedisConfigUtils.getJedisPoolConfig(redisProperties.getJedis().getPool());
        // 截取集群节点
        String[] sentinels = redisProperties.getSentinel().getNodes().toArray(new String[0]);
        // 创建set集合
        Set<HostAndPort> nodes;
        // 循环数组把集群节点添加到set集合中
        nodes = Arrays.stream(sentinels).map(HostAndPort::from).collect(Collectors.toSet());
        // 获取 Jedis 客户端配置
        JedisClientConfig clientConfig = RedisConfigUtils.getJedisClientConfig(redisProperties);
        // 获取 Sentinel Jedis 客户端配置
        JedisClientConfig sentinelClientConfig = RedisConfigUtils.getJedisSentinelClientConfig(redisProperties);
        // 使用配置创建并返回 Jedis Sentinel 连接池对象
        return new JedisSentinelPool(redisProperties.getSentinel().getMaster(), nodes, jedisPoolConfig,
                clientConfig, sentinelClientConfig);
    }

    /**
     * 创建并返回一个 Jedis Cluster 对象。
     *
     * @param redisProperties Redis 相关配置属性
     * @return Jedis Cluster 对象
     */
    public JedisCluster getJedisCluster(RedisProperties redisProperties) {
        // 获取 Jedis 连接池配置
        ConnectionPoolConfig poolConfig = RedisConfigUtils.getJedisConnectionPoolConfig(redisProperties);
        // 截取集群节点
        String[] cluster = redisProperties.getCluster().getNodes().toArray(new String[0]);
        // 创建set集合
        Set<HostAndPort> nodes;
        // 循环数组把集群节点添加到set集合中
        nodes = Arrays.stream(cluster).map(HostAndPort::from).collect(Collectors.toSet());
        // 获取 Jedis 客户端配置
        JedisClientConfig clientConfig = RedisConfigUtils.getJedisClientConfig(redisProperties);
        // 需要密码连接的创建对象方式
        // 使用配置创建并返回 Jedis Cluster 对象
        return new JedisCluster(nodes, clientConfig,
                redisProperties.getCluster().getMaxRedirects(), poolConfig);
    }

    /**
     * 根据配置创建并返回一个 RedisClient 对象。
     *
     * @param lokiProperties  Loki 配置属性
     * @param redisProperties Redis 相关配置属性
     * @return RedisClient 对象
     */
    @Bean
    @ConditionalOnMissingBean(RedisClient.class)
    public RedisClient redisClient(LokiProperties lokiProperties, RedisProperties redisProperties) {
        RedisConfigUtils.convert(lokiProperties, redisProperties);
        if (redisProperties.getSentinel() != null && !redisProperties.getSentinel().getMaster().isEmpty()) {
            // 如果配置了 Sentinel，返回 RedisSentinelImpl 对象
            return new RedisSentinelImpl(jedisSentinelPool(redisProperties));
        } else if (redisProperties.getCluster() != null && !redisProperties.getCluster().getNodes().isEmpty()) {
            // 如果配置了 Cluster，返回 RedisClusterImpl 对象
            return new RedisClusterImpl(getJedisCluster(redisProperties));
        } else {
            // 默认情况下返回 RedisDefaultImpl 对象
            return new RedisDefaultImpl(redisPoolFactory(redisProperties));
        }
    }
}
