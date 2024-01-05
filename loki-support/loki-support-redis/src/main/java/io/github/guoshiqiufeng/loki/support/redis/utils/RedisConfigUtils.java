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
package io.github.guoshiqiufeng.loki.support.redis.utils;

import io.github.guoshiqiufeng.loki.enums.MqType;
import io.github.guoshiqiufeng.loki.support.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.redis.config.RedisProperties;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * redis配置工具类
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


    /**
     * 转换 Loki 配置和 Redis 配置，更新 RedisProperties 对象。
     *
     * @param lokiProperties  Loki 配置属性
     * @param redisProperties Redis 相关配置属性
     */
    public void convert(LokiProperties lokiProperties, RedisProperties redisProperties) {
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
