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

import cn.hutool.core.util.IdUtil;
import io.github.guoshiqiufeng.loki.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.core.config.LokiProperties;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * kafka配置工具类
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/16 13:03
 */
@Slf4j
@UtilityClass
public class KafkaConfigUtils {

    private final Map<String, KafkaProducer<String, String>> producerMap = new HashMap<>();

    /**
     * 获取 Producer
     *
     * @param beanName   beanName
     * @param properties 配置
     * @return Producer
     */
    public KafkaProducer<String, String> getProducer(String beanName, LokiProperties properties) {
        if (beanName == null || beanName.isEmpty()) {
            beanName = "defaultProducer";
        }
        if (producerMap.get(beanName) == null) {
            KafkaProducer<String, String> producer = producerBuilder(beanName, properties);
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
     */
    public KafkaProducer<String, String> producerBuilder(String beanName, LokiProperties properties) {
        Properties clientConfiguration = getClientConfiguration(properties, beanName);
        clientConfiguration.put(ProducerConfig.RETRIES_CONFIG, properties.getGlobalConfig().getMqConfig().getMaxAttempts());
        KafkaProducer<String, String> producer = new KafkaProducer<>(clientConfiguration, new StringSerializer(), new StringSerializer());
        log.info(String.format("%s started successful on bootstrap.servers %s", beanName, clientConfiguration.getProperty(ProducerConfig.CLIENT_ID_CONFIG)));
        producerMap.put(beanName, producer);
        return producer;
    }

    /**
     * 获取 PushConsumerBuilder
     *
     * @param properties 配置
     * @return PushConsumerBuilder
     */
    public KafkaConsumer<String, String> getPushConsumerBuilder(LokiProperties properties, String groupId, int index) {
        Properties config = getClientConfiguration(properties, groupId + "_" + index);
        config.put(CommonClientConfigs.GROUP_ID_CONFIG, groupId);
        config.put(CommonClientConfigs.GROUP_INSTANCE_ID_CONFIG, groupId + IdUtil.getSnowflake().nextIdStr());
        return new KafkaConsumer<String, String>(config, new StringDeserializer(), new StringDeserializer());
    }

    /**
     * 获取 ClientConfiguration
     *
     * @param properties 配置
     * @return ClientConfiguration
     */
    private Properties getClientConfiguration(LokiProperties properties, String beanName) {
        Properties config = new Properties();
        String hostName = "unknown";
        if (beanName != null && !beanName.isEmpty()) {
            hostName = beanName;
        }
        GlobalConfig.MqConfig mqConfig = properties.getGlobalConfig().getMqConfig();
        config.put(ProducerConfig.CLIENT_ID_CONFIG, hostName);
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, mqConfig.getAddress());
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        return config;
    }

}
