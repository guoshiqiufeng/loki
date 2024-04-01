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
package io.github.guoshiqiufeng.loki.support.redis.impl.jedis;

import io.github.guoshiqiufeng.loki.support.redis.impl.BaseRedisClient;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.params.SetParams;

import java.util.Set;

/**
 * 集群版redis实现
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 17:26
 */
public class RedisClusterImpl extends BaseJedisClient {

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

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param patterns    规则
     */
    @Override
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        jedisCluster.psubscribe(jedisPubSub, patterns);
    }

    /**
     * 判断key是否存在
     *
     * @param key 建
     * @return
     */
    @Override
    public boolean exists(String key) {
        return jedisCluster.exists(key);
    }

    /**
     * set
     *
     * @param key          建
     * @param value        值
     * @param expireAtTime 过期时间戳
     */
    @Override
    public void set(String key, String value, Long expireAtTime) {
        if (expireAtTime != null) {
            jedisCluster.set(key, value, new SetParams().pxAt(expireAtTime));
        } else {
            jedisCluster.set(key, value);
        }
    }

    /**
     * hset
     *
     * @param key   建
     * @param field 字段
     * @param value 值
     */
    @Override
    public void hset(String key, String field, String value) {
        jedisCluster.hset(key, field, value);
    }

    /**
     * hget
     *
     * @param key   建
     * @param field 字段
     * @return
     */
    @Override
    public String hget(String key, String field) {
        return jedisCluster.hget(key, field);
    }

    /**
     * 删除
     *
     * @param key   建
     * @param field 字段
     */
    @Override
    public void hdel(String key, String... field) {
        jedisCluster.hdel(key, field);
    }

    /**
     * hkeys
     *
     * @param key 建
     * @return
     */
    @Override
    public Set<String> hkeys(String key) {
        return jedisCluster.hkeys(key);
    }
}
