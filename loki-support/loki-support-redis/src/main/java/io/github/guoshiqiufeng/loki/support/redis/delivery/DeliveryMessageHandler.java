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
package io.github.guoshiqiufeng.loki.support.redis.delivery;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import io.github.guoshiqiufeng.loki.constant.Constant;
import io.github.guoshiqiufeng.loki.support.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import io.github.guoshiqiufeng.loki.support.redis.RedisClient;
import io.github.guoshiqiufeng.loki.support.redis.impl.BaseRedisClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/3/13 17:07
 */
@Slf4j
public class DeliveryMessageHandler {

    /**
     * 处理延迟消息
     * @param redisClient redis客户端
     */
    public static void handleDeliveryMessage(RedisClient redisClient) {
        if(redisClient instanceof BaseRedisClient) {
            BaseRedisClient baseRedisClient = (BaseRedisClient) redisClient;
            baseRedisClient.psubscribe(record -> {
                if(log.isDebugEnabled()) {
                    log.debug("handleDeliveryMessage: {}", JSONUtil.toJsonStr(record));
                }
                sendDelayedMessage(record.getBodyMessage(), null, baseRedisClient);
                return null;
            }, "__keyevent@*__:expired");
        }
    }

    /**
     * 处理历史数据
     * @param redisClient
     */
    public static void processingHistoricalData(RedisClient redisClient, LokiProperties properties) {
        if(redisClient instanceof BaseRedisClient) {
            BaseRedisClient baseRedisClient = (BaseRedisClient) redisClient;
            baseRedisClient.hkeys(Constant.REDIS_DELIVERY_KEY).forEach(key -> {
                // 判断key 是否存在
                if (!baseRedisClient.exists(key)) {
                    // 判断是否开启
                    GlobalConfig globalConfig = properties.getGlobalConfig();
                    boolean flag = globalConfig.isRedisHistoryDelayMessageSend();
                    if (!flag) {
                        baseRedisClient.hdel(Constant.REDIS_DELIVERY_KEY, key);
                    } else {
                        sendDelayedMessage(key, globalConfig.getRedisHistoryDelayMessageSendTime(), baseRedisClient);
                    }
                }
            });
        }
    }

    /**
     * 发送延时消息
     * @param key
     * @param baseRedisClient
     */
    private static void sendDelayedMessage(String key, Long deliveryTimestamp, BaseRedisClient baseRedisClient) {
        if(StringUtils.isEmpty(key)) {
            return;
        }
        if(!key.startsWith(Constant.REDIS_KEY_PREFIX)) {
            return;
        }
        // 获取消息
        String value = baseRedisClient.hget(Constant.REDIS_DELIVERY_KEY, key);
        if(StringUtils.isNotEmpty(value)) {
            ProducerRecord producerRecord = JSONUtil.toBean(value, ProducerRecord.class);
            producerRecord.setDeliveryTimestamp(deliveryTimestamp);
            // 发送
            baseRedisClient.send(producerRecord);
            // 删除
            baseRedisClient.hdel(Constant.REDIS_DELIVERY_KEY, key);
        }
    }
}
