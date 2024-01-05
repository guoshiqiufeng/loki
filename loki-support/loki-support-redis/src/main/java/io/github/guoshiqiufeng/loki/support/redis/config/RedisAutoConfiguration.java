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


    public JedisPool redisPoolFactory(RedisProperties redisProperties) {
        JedisPoolConfig jedisPoolConfig = RedisConfigUtils.getJedisPoolConfig(redisProperties);
        JedisClientConfig clientConfig = RedisConfigUtils.getJedisClientConfig(redisProperties);
        return new JedisPool(jedisPoolConfig, new HostAndPort(redisProperties.getHost(),
                redisProperties.getPort()), clientConfig);
    }

    public JedisSentinelPool jedisSentinelPool(RedisProperties redisProperties) {
        JedisPoolConfig jedisPoolConfig = RedisConfigUtils.getJedisPoolConfig(redisProperties);
        // 截取集群节点
        String[] sentinels = redisProperties.getSentinel().getNodes().toArray(new String[0]);
        // 创建set集合
        Set<HostAndPort> nodes;
        // 循环数组把集群节点添加到set集合中
        nodes = Arrays.stream(sentinels).map(HostAndPort::from).collect(Collectors.toSet());
        JedisClientConfig clientConfig = RedisConfigUtils.getJedisClientConfig(redisProperties);
        JedisClientConfig SentinelClientConfig = RedisConfigUtils.getJedisSentinelClientConfig(redisProperties);
        return new JedisSentinelPool(redisProperties.getSentinel().getMaster(), nodes, jedisPoolConfig,
                clientConfig, SentinelClientConfig);
    }

    public JedisCluster getJedisCluster(RedisProperties redisProperties) {
        ConnectionPoolConfig poolConfig = RedisConfigUtils.getJedisConnectionPoolConfig(redisProperties);
        // 截取集群节点
        String[] cluster = redisProperties.getCluster().getNodes().toArray(new String[0]);
        // 创建set集合
        Set<HostAndPort> nodes;
        // 循环数组把集群节点添加到set集合中
        nodes = Arrays.stream(cluster).map(HostAndPort::from).collect(Collectors.toSet());
        JedisClientConfig clientConfig = RedisConfigUtils.getJedisClientConfig(redisProperties);
        //需要密码连接的创建对象方式
        return new JedisCluster(nodes, clientConfig,
                redisProperties.getCluster().getMaxRedirects(), poolConfig);
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
     *
     * @param lokiProperties  loki配置
     * @param redisProperties redis配置
     */
    private void convert(LokiProperties lokiProperties, RedisProperties redisProperties) {
        GlobalConfig globalConfig = lokiProperties.getGlobalConfig();
        GlobalConfig.MqConfig mqConfig = globalConfig.getMqConfig();

        if (isRedisMqConfigValid(mqConfig)) {
            String address = mqConfig.getAddress();
            if (address != null && !address.isEmpty()) {
                List<String> addressList = Arrays.asList(address.split(","));
                String sentinelMaster = getSentinelMaster(address);
                boolean isSentinel = !sentinelMaster.isEmpty();
                if (addressList.size() == 1) {
                    // Single node configuration
                    configureSingleNode(addressList.get(0), redisProperties);
                } else if (addressList.size() > 1 && !isSentinel) {
                    // Cluster configuration
                    configureCluster(addressList, redisProperties);
                } else if (addressList.size() > 1) {
                    // Sentinel configuration
                    addressList = addressList.stream().filter(tmp -> !tmp.equals(sentinelMaster))
                            .collect(Collectors.toList());
                    configureSentinel(addressList, redisProperties, mqConfig, sentinelMaster);
                }
            }

            // Set other related configurations such as auth, username, password, etc.
            if (mqConfig.getAuth() != null) {
                configureAuth(mqConfig.getUsername(), mqConfig.getPassword(), redisProperties);
            }
        }
    }

    private boolean isRedisMqConfigValid(GlobalConfig.MqConfig mqConfig) {
        return mqConfig != null && MqType.REDIS.equals(mqConfig.getMqType());
    }

    private void configureSingleNode(String node, RedisProperties redisProperties) {
        String[] nodeArr = node.split(":");
        if (nodeArr.length == 1) {
            redisProperties.setHost(nodeArr[0]);
        } else if (nodeArr.length == 2) {
            redisProperties.setHost(nodeArr[0]);
            redisProperties.setPort(Integer.parseInt(nodeArr[1]));
        }
    }

    private void configureCluster(List<String> nodes, RedisProperties redisProperties) {
        redisProperties.setCluster(new RedisProperties.Cluster().setNodes(nodes));
    }

    private void configureSentinel(List<String> nodes, RedisProperties redisProperties, GlobalConfig.MqConfig mqConfig, String master) {
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

    private String getSentinelMaster(String address) {
        String sentinelPattern = "(\\w+)";
        Pattern pattern = Pattern.compile(sentinelPattern);
        Matcher matcher = pattern.matcher(address);
        return matcher.find() ? matcher.group(1) : "";
    }

    private void configureAuth(String username, String password, RedisProperties redisProperties) {
        if (username != null) {
            redisProperties.setUsername(username);
        }
        if (password != null) {
            redisProperties.setPassword(password);
        }
    }
}
