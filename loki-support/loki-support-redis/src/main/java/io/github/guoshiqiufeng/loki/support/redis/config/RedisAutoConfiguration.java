/*
 * Copyright (c) 2023-2023, fubluesky (fubluesky@foxmail.com)
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

import io.github.guoshiqiufeng.loki.enums.MqType;
import io.github.guoshiqiufeng.loki.support.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.redis.RedisClient;
import io.github.guoshiqiufeng.loki.support.redis.impl.RedisClusterImpl;
import io.github.guoshiqiufeng.loki.support.redis.impl.RedisDefaultImpl;
import io.github.guoshiqiufeng.loki.support.redis.impl.RedisSentinelImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * redis配置
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 16:16
 */
@Configuration
public class RedisAutoConfiguration {

    /**
     * 配置文件
     *
     * @return 配置文件
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.data.redis")
    @ConditionalOnMissingBean(RedisProperties.class)
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }


    public JedisPool redisPoolFactory(RedisProperties redisProperties) {
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(pool.getMaxIdle());
        jedisPoolConfig.setMinEvictableIdleDuration(pool.getMaxWait());
        if (redisProperties.getPassword() != null && !redisProperties.getPassword().isEmpty()) {
            return new JedisPool(jedisPoolConfig, redisProperties.getHost(),
                    redisProperties.getPort(), redisProperties.getTimeout(),
                    redisProperties.getPassword(), redisProperties.getDatabase());
        } else {
            return new JedisPool(jedisPoolConfig, redisProperties.getHost(),
                    redisProperties.getPort(), redisProperties.getTimeout(),
                    null, redisProperties.getDatabase());
        }
    }

    public JedisSentinelPool jedisSentinelPool(RedisProperties redisProperties) {
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(pool.getMaxIdle());
        jedisPoolConfig.setMinEvictableIdleDuration(pool.getMaxWait());
        // 截取集群节点
        String[] sentinels = redisProperties.getSentinel().getNodes().toArray(new String[0]);
        // 创建set集合
        Set<String> nodes = new HashSet<>();
        // 循环数组把集群节点添加到set集合中
        Collections.addAll(nodes, sentinels);
        if (redisProperties.getSentinel().getPassword() != null && !redisProperties.getSentinel().getPassword().isEmpty()) {
            return new JedisSentinelPool(redisProperties.getSentinel().getMaster(), nodes, jedisPoolConfig, redisProperties.getTimeout(), redisProperties.getTimeout(),
                    redisProperties.getPassword(), redisProperties.getDatabase(), null, redisProperties.getTimeout(), redisProperties.getTimeout(),
                    redisProperties.getSentinel().getPassword(), null);
        } else {
            return new JedisSentinelPool(redisProperties.getSentinel().getMaster(), nodes, jedisPoolConfig, redisProperties.getTimeout(), redisProperties.getPassword());
        }
    }

    public JedisCluster getJedisCluster(RedisProperties redisProperties) {
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinEvictableIdleDuration(pool.getMaxWait());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());
        // 截取集群节点
        String[] cluster = redisProperties.getCluster().getNodes().toArray(new String[0]);
        // 创建set集合
        Set<HostAndPort> nodes = new HashSet<>();
        // 循环数组把集群节点添加到set集合中
        for (String node : cluster) {
            String[] host = node.split(":");
            //添加集群节点
            if (host.length > 1) {
                nodes.add(new HostAndPort(host[0], Integer.parseInt(host[1])));
            }
        }
        //需要密码连接的创建对象方式
        return new JedisCluster(nodes, redisProperties.getTimeout(), 2000,
                redisProperties.getCluster().getMaxRedirects(), redisProperties.getPassword(), poolConfig);
    }

    @Bean
    @ConditionalOnMissingBean(RedisClient.class)
    public RedisClient redisClient(LokiProperties lokiProperties, RedisProperties redisProperties) {
        convert(lokiProperties, redisProperties);
        if (redisProperties.getSentinel() != null && !redisProperties.getSentinel().getMaster().isEmpty()) {
            return new RedisSentinelImpl(jedisSentinelPool(redisProperties));
        } else if (redisProperties.getCluster() != null && !redisProperties.getCluster().getNodes().isEmpty()) {
            return new RedisClusterImpl(getJedisCluster(redisProperties));
        } else {
            return new RedisDefaultImpl(redisPoolFactory(redisProperties));
        }
    }

    /**
     * 转换
     * @param lokiProperties loki配置
     * @param redisProperties redis配置
     */
    private void convert(LokiProperties lokiProperties, RedisProperties redisProperties) {
        GlobalConfig globalConfig = lokiProperties.getGlobalConfig();
        GlobalConfig.MqConfig mqConfig = globalConfig.getMqConfig();
        if(mqConfig != null && mqConfig.getMqType().equals(MqType.REDIS)) {
            // TODO
//            String address = mqConfig.getAddress();
//            Boolean auth = mqConfig.getAuth();
//            String username = mqConfig.getUsername();
//            String password = mqConfig.getPassword();
//            if(address == null || address.isEmpty()) {
//                return;
//            }
//            address.split("");
        }
    }
}
