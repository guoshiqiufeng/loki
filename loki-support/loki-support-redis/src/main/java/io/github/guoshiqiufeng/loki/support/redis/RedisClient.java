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
package io.github.guoshiqiufeng.loki.support.redis;

import io.github.guoshiqiufeng.loki.constant.Constant;
import io.github.guoshiqiufeng.loki.support.core.LokiClient;
import io.github.guoshiqiufeng.loki.support.core.ProducerResult;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import io.github.guoshiqiufeng.loki.support.redis.consumer.ConsumerRecord;
import io.github.guoshiqiufeng.loki.support.redis.consumer.DefaultJedisPubSub;
import redis.clients.jedis.JedisPubSub;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * redis客户端
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 15:49
 */
public interface RedisClient extends LokiClient {

    /**
     * 发送消息
     *
     * @param groupName 组名称
     * @param record    发送信息
     * @return 发送消息结果
     */
    @Override
    default CompletableFuture<ProducerResult> sendAsync(String groupName, io.github.guoshiqiufeng.loki.support.core.ProducerRecord record) {
        if (record == null) {
            throw new LokiException("sendAsync fail : record is null!");
        }

        return CompletableFuture.supplyAsync(() -> publish(record.getTopic(), record.getMessage()))
                .thenApplyAsync(recordMetadata -> {
                    ProducerResult result = new ProducerResult();
                    result.setTopic(record.getTopic());
                    return result;
                });
    }

    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 消息
     * @return 发布结果
     */
    long publish(String channel, String message);

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param channels    频道
     */
    public void subscribe(JedisPubSub jedisPubSub, String... channels);

    /**
     * 订阅消息
     *
     * @param jedisPubSub 消息处理器
     * @param patterns    规则
     */
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns);

    default public void subscribe(Function<ConsumerRecord, Void> function, String... channels) {
        subscribe(new DefaultJedisPubSub(function), channels);
    }

    default public void psubscribe(Function<ConsumerRecord, Void> function, String... patterns) {
        psubscribe(new DefaultJedisPubSub(function), patterns);
    }
}
