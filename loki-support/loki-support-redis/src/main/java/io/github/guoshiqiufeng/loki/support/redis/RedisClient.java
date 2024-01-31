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

import io.github.guoshiqiufeng.loki.support.core.LokiClient;
import io.github.guoshiqiufeng.loki.support.core.PipelineApi;
import io.github.guoshiqiufeng.loki.support.redis.consumer.ConsumerRecord;

import java.util.function.Function;

/**
 * redis客户端
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 15:49
 */
public interface RedisClient extends LokiClient, PipelineApi {

    /**
     * 订阅消息
     *
     * @param function 回调
     * @param channels 频道
     */
    void subscribe(Function<ConsumerRecord, Void> function, String... channels);

    /**
     * 订阅消息
     *
     * @param function 回调
     * @param patterns 规则
     */
    void psubscribe(Function<ConsumerRecord, Void> function, String... patterns);
}
