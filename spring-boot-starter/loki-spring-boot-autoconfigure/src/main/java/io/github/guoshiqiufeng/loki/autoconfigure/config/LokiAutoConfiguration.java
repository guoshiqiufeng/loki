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
package io.github.guoshiqiufeng.loki.autoconfigure.config;

import io.github.guoshiqiufeng.loki.Listener;
import io.github.guoshiqiufeng.loki.autoconfigure.register.LokiRegistrar;
import io.github.guoshiqiufeng.loki.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.core.handler.Handler;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.handler.impl.KafkaHandler;
import io.github.guoshiqiufeng.loki.core.handler.impl.RedisHandler;
import io.github.guoshiqiufeng.loki.core.handler.impl.RocketMqHandler;
import io.github.guoshiqiufeng.loki.core.toolkit.RocketMqConfigUtils;
import io.github.guoshiqiufeng.loki.enums.MqType;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * loki自动配置类
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/16 09:26
 */
@Slf4j
@Configuration
public class LokiAutoConfiguration {

    /**
     * LokiRegistrar Bean，用于注册处理器和消息监听器
     *
     * @param handlerHolder  处理器持有者
     * @param handler        处理器列表
     * @param lokiProperties Loki配置
     * @param listenerList   消息监听器列表
     * @param <T>            监听器消息类型
     * @return LokiRegistrar 实例
     */
    @Bean
    public <T> LokiRegistrar<T> lokiRegistrar(HandlerHolder handlerHolder, List<Handler> handler, LokiProperties lokiProperties,
                                              List<Listener<T>> listenerList) {
        return new LokiRegistrar<T>(handlerHolder, lokiProperties, listenerList);
    }

}
