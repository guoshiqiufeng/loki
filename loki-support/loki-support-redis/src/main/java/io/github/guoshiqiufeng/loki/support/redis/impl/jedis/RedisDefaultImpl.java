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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.params.SetParams;

import java.util.Set;

/**
 * 默认实现：单机版redis
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 17:34
 */
public class RedisDefaultImpl extends BaseJedisClient {

    private final JedisPool jedisPool;

    public RedisDefaultImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
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
        try (Jedis jedis = jedisPool.getResource()) {
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
        try (Jedis jedis = jedisPool.getResource()) {
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
        try (Jedis jedis = jedisPool.getResource()) {
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
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        }
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
        try (Jedis jedis = jedisPool.getResource()) {
            if (expireAtTime != null) {
                jedis.set(key, value, new SetParams().pxAt(expireAtTime));
            } else {
                jedis.set(key, value);
            }
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
        try (Jedis jedis = jedisPool.getResource()) {
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
        try (Jedis jedis = jedisPool.getResource()) {
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
        try (Jedis jedis = jedisPool.getResource()) {
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
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hkeys(key);
        }
    }
}
