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
package io.github.guoshiqiufeng.loki.support.redis.impl;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.params.SetParams;

import java.util.Set;

/**
 * 哨兵redis实现
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 17:29
 */
public class RedisSentinelImpl extends BaseRedisClient {

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
        try (Jedis jedis = jedisSentinelPool.getResource()) {
            return jedis.publish(channel, message);
        }
    }

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param channels    频道
     */
    @Override
    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        try (Jedis jedis = jedisSentinelPool.getResource()) {
            jedis.subscribe(jedisPubSub, channels);
        }
    }

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param patterns    规则
     */
    @Override
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        try (Jedis jedis = jedisSentinelPool.getResource()) {
            jedis.psubscribe(jedisPubSub, patterns);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 建
     * @return
     */
    @Override
    public boolean exists(String key) {
        try (Jedis jedis = jedisSentinelPool.getResource()) {
            return jedis.exists(key);
        }
    }

    /**
     * set
     *
     * @param key    建
     * @param value  值
     * @param params 参数
     */
    @Override
    public void set(String key, String value, SetParams params) {
        try (Jedis jedis = jedisSentinelPool.getResource()) {
            jedis.set(key, value, params);
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
        try (Jedis jedis = jedisSentinelPool.getResource()) {
            jedis.hset(key, field, value);
        }
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
        try (Jedis jedis = jedisSentinelPool.getResource()) {
            return jedis.hget(key, field);
        }
    }

    /**
     * 删除
     *
     * @param key   建
     * @param field 字段
     */
    @Override
    public void hdel(String key, String... field) {
        try (Jedis jedis = jedisSentinelPool.getResource()) {
            jedis.hdel(key, field);
        }
    }

    /**
     * hkeys
     *
     * @param key 建
     * @return
     */
    @Override
    public Set<String> hkeys(String key) {
        try (Jedis jedis = jedisSentinelPool.getResource()) {
            return jedis.hkeys(key);
        }
    }
}
