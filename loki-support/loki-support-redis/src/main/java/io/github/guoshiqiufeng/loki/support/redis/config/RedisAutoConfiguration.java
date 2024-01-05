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
import io.github.guoshiqiufeng.loki.support.redis.utils.RedisConfigUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        JedisClientConfig SentinelClientConfig = RedisConfigUtils.getJedisSentinelClientConfig(redisProperties);
        // 使用配置创建并返回 Jedis Sentinel 连接池对象
        return new JedisSentinelPool(redisProperties.getSentinel().getMaster(), nodes, jedisPoolConfig,
                clientConfig, SentinelClientConfig);
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
        convert(lokiProperties, redisProperties);
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

    /**
     * 转换 Loki 配置和 Redis 配置，更新 RedisProperties 对象。
     *
     * @param lokiProperties  Loki 配置属性
     * @param redisProperties Redis 相关配置属性
     */
    private void convert(LokiProperties lokiProperties, RedisProperties redisProperties) {
        // 获取 Loki 全局配置
        GlobalConfig globalConfig = lokiProperties.getGlobalConfig();
        GlobalConfig.MqConfig mqConfig = globalConfig.getMqConfig();

        if (isRedisMqConfigValid(mqConfig)) {
            String address = mqConfig.getAddress();
            if (address != null && !address.isEmpty()) {
                // 将地址拆分为集合
                List<String> addressList = Arrays.asList(address.split(","));
                String sentinelMaster = getSentinelMaster(address);
                boolean isSentinel = !sentinelMaster.isEmpty();
                if (addressList.size() == 1) {
                    // 单节点配置
                    configureSingleNode(addressList.get(0), redisProperties);
                } else if (addressList.size() > 1 && !isSentinel) {
                    // 集群配置
                    configureCluster(addressList, redisProperties);
                } else if (addressList.size() > 1) {
                    // Sentinel 配置
                    addressList = addressList.stream().filter(tmp -> !tmp.equals(sentinelMaster))
                            .collect(Collectors.toList());
                    configureSentinel(addressList, redisProperties, mqConfig, sentinelMaster);
                }
            }

            // 设置其他相关配置，如 auth、username、password 等。
            if (mqConfig.getAuth() != null) {
                configureAuth(mqConfig.getUsername(), mqConfig.getPassword(), redisProperties);
            }
        }
    }

    /**
     * 检查 Redis Mq 配置是否有效。
     *
     * @param mqConfig Redis Mq 配置
     * @return 配置是否有效
     */
    private boolean isRedisMqConfigValid(GlobalConfig.MqConfig mqConfig) {
        return mqConfig != null && MqType.REDIS.equals(mqConfig.getMqType());
    }

    /**
     * 根据单节点配置更新 RedisProperties 对象。
     *
     * @param node            单节点地址
     * @param redisProperties Redis 相关配置属性
     */
    private void configureSingleNode(String node, RedisProperties redisProperties) {
        String[] nodeArr = node.split(":");
        if (nodeArr.length == 1) {
            redisProperties.setHost(nodeArr[0]);
        } else if (nodeArr.length == 2) {
            redisProperties.setHost(nodeArr[0]);
            redisProperties.setPort(Integer.parseInt(nodeArr[1]));
        }
    }

    /**
     * 根据集群配置更新 RedisProperties 对象。
     *
     * @param nodes           集群节点列表
     * @param redisProperties Redis 相关配置属性
     */
    private void configureCluster(List<String> nodes, RedisProperties redisProperties) {
        redisProperties.setCluster(new RedisProperties.Cluster().setNodes(nodes));
    }

    /**
     * 根据 Sentinel 配置更新 RedisProperties 对象。
     *
     * @param nodes           Sentinel 节点列表
     * @param redisProperties Redis 相关配置属性
     * @param mqConfig        Redis Mq 配置
     * @param master          Sentinel 主节点名称
     */
    private void configureSentinel(List<String> nodes, RedisProperties redisProperties,
                                   GlobalConfig.MqConfig mqConfig, String master) {
        // 设置 Sentinel 配置
        RedisProperties.Sentinel sentinel = new RedisProperties.Sentinel()
                .setNodes(nodes)
                .setMaster(master);
        if (mqConfig.getUsername() != null) {
            sentinel.setUsername(mqConfig.getUsername());
        }
        if (mqConfig.getPassword() != null) {
            sentinel.setPassword(mqConfig.getPassword());
        }
        redisProperties.setSentinel(sentinel);
    }

    /**
     * 从 Sentinel 地址中获取主节点名称。
     *
     * @param address Sentinel 地址
     * @return Sentinel 主节点名称
     */
    private String getSentinelMaster(String address) {
        String sentinelPattern = "(\\w+)";
        Pattern pattern = Pattern.compile(sentinelPattern);
        Matcher matcher = pattern.matcher(address);
        return matcher.find() ? matcher.group(1) : "";
    }

    /**
     * 根据认证信息更新 RedisProperties 对象。
     *
     * @param username        认证用户名
     * @param password        认证密码
     * @param redisProperties Redis 相关配置属性
     */
    private void configureAuth(String username, String password, RedisProperties redisProperties) {
        if (username != null) {
            redisProperties.setUsername(username);
        }
        if (password != null) {
            redisProperties.setPassword(password);
        }
    }


}
