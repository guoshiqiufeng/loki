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

import io.github.guoshiqiufeng.loki.core.handler.Handler;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.handler.impl.RocketMqHandler;
import io.github.guoshiqiufeng.loki.enums.MqType;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.support.rocketmq.RocketClient;
import io.github.guoshiqiufeng.loki.support.rocketmq.util.RocketMqConfigUtils;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * rocketmq自动配置
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/26 17:13
 */
@Configuration
@ConditionalOnProperty(prefix = "loki.global-config.mq-config", name = "mq-type", havingValue = "ROCKET_MQ")
public class RocketMqAutoConfiguration {

    /**
     * 默认生产者
     *
     * @param properties loki配置
     * @return 默认生产者
     * @throws ClientException 异常
     */
    @Bean
    @ConditionalOnMissingBean(Producer.class)
    public Producer defaultProducer(LokiProperties properties) throws ClientException {
        return RocketMqConfigUtils.producerBuilder("defaultProducer", properties);
    }

    /**
     * Handler Bean列表，包含RocketMqHandler
     *
     * @param properties    Loki配置
     * @param handlerHolder 处理器持有者
     * @return Handler 实例列表
     */
    @Bean
    @ConditionalOnMissingBean(Handler.class)
    public List<Handler> rocketHandler(LokiProperties properties, HandlerHolder handlerHolder, RocketClient rocketClient) {
        ArrayList<Handler> handler = new ArrayList<Handler>(1);
        if (properties.getGlobalConfig().getMqConfig().getMqType().equals(MqType.ROCKET_MQ)) {
            RocketMqHandler rocketMqHandler = new RocketMqHandler(properties, handlerHolder, rocketClient);
            handler.add(rocketMqHandler);
        } else {
            throw new LokiException("mq type is not support ");
        }
        return handler;
    }

    /**
     * HandlerHolder Bean，用于持有处理器
     *
     * @return HandlerHolder 实例
     */
    @Bean
    @ConditionalOnMissingBean(HandlerHolder.class)
    public HandlerHolder handlerHolder() {
        return new HandlerHolder();
    }
}
