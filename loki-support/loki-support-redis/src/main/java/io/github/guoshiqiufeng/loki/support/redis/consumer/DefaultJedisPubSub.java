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
package io.github.guoshiqiufeng.loki.support.redis.consumer;

import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerRecord;
import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPubSub;

import java.util.function.Function;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/10 17:31
 */
@Slf4j
public class DefaultJedisPubSub extends JedisPubSub {

    final Function<ConsumerRecord, Void> function;

    public DefaultJedisPubSub(Function<ConsumerRecord, Void> function) {
        this.function = function;
    }

    @Override
    public void onMessage(String channel, String message) {
        if (log.isDebugEnabled()) {
            log.debug("{} onMessage : {}", channel, message);
        }
        ConsumerRecord consumerRecord = new ConsumerRecord(channel, null, null,
                null, null, message);
        consumerRecord = PipelineUtils.processListener(consumerRecord);
        if (consumerRecord != null) {
            function.apply(consumerRecord);
        }
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        if (log.isDebugEnabled()) {
            log.debug("pattern: {}  channel: {} onPMessage : {}", pattern, channel, message);
        }
        ConsumerRecord consumerRecord = new ConsumerRecord(channel, null, null,
                null, null, message);
        consumerRecord = PipelineUtils.processListener(consumerRecord);
        if (consumerRecord != null) {
            function.apply(consumerRecord);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        if (log.isDebugEnabled()) {
            log.debug("{} subscribe success", channel);
        }
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        if (log.isDebugEnabled()) {
            log.debug("{} unsubscribe success", channel);
        }
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        if (log.isDebugEnabled()) {
            log.debug("{} PUnsubscribe success", pattern);
        }
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        if (log.isDebugEnabled()) {
            log.debug("{} PSubscribe success", pattern);
        }
    }

}
