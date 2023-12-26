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
package io.github.guoshiqiufeng.loki.core.toolkit;


import io.github.guoshiqiufeng.loki.support.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.shaded.com.google.common.collect.Maps;

import java.time.Duration;
import java.util.Map;

/**
 * RocketMq配置工具类
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/18 10:03
 */
@Slf4j
@UtilityClass
public class RocketMqConfigUtils {

    private final Map<String, Producer> producerMap = Maps.newHashMap();

    /**
     * 获取 Producer
     *
     * @param beanName   beanName
     * @param properties 配置
     * @return Producer
     * @throws ClientException ClientException
     */
    public Producer getProducer(String beanName, LokiProperties properties) throws ClientException {
        if (beanName == null || beanName.isEmpty()) {
            beanName = "defaultProducer";
        }
        if (producerMap.get(beanName) == null) {
            Producer producer = producerBuilder(beanName, properties);
            producerMap.put(beanName, producer);
        }
        return producerMap.get(beanName);
    }

    /**
     * 创建 Producer
     *
     * @param beanName   beanName
     * @param properties 配置
     * @return Producer
     * @throws ClientException ClientException
     */
    public Producer producerBuilder(String beanName, LokiProperties properties) throws ClientException {
        ClientConfiguration clientConfiguration = getClientConfiguration(properties);
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        Producer producer = provider.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                // 设置重试次数，默认为3
                .setMaxAttempts(properties.getGlobalConfig().getMqConfig().getMaxAttempts())
                .build();
        log.info(String.format("%s started successful on endpoints %s", beanName, clientConfiguration.getEndpoints()));
        producerMap.put(beanName, producer);
        return producer;
    }

    /**
     * 获取 PushConsumerBuilder
     *
     * @param properties 配置
     * @return PushConsumerBuilder
     */
    public PushConsumerBuilder getPushConsumerBuilder(LokiProperties properties) {
        ClientConfiguration clientConfiguration = getClientConfiguration(properties);
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        return provider.newPushConsumerBuilder()
                .setClientConfiguration(clientConfiguration);
    }

    /**
     * 获取 ClientConfiguration
     *
     * @param properties 配置
     * @return ClientConfiguration
     */
    private ClientConfiguration getClientConfiguration(LokiProperties properties) {
        GlobalConfig.MqConfig mqConfig = properties.getGlobalConfig().getMqConfig();
        String endpoints = mqConfig.getAddress();
        Boolean auth = mqConfig.getAuth();
        String username = mqConfig.getUsername();
        String password = mqConfig.getPassword();
        int connectTimeout = mqConfig.getConnectTimeout();

        SessionCredentialsProvider sessionCredentialsProvider =
                new StaticSessionCredentialsProvider(username, password);
        return ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .setRequestTimeout(Duration.ofSeconds(connectTimeout))
                .enableSsl(auth)
                .setCredentialProvider(sessionCredentialsProvider)
                .build();
    }

}

