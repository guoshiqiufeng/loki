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
package io.github.guoshiqiufeng.loki.support.kafka.utils;

import io.github.guoshiqiufeng.loki.support.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.kafka.config.KafkaProperties;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.*;
import java.util.stream.Collectors;

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
    public KafkaProducer<String, String> getProducer(String beanName, KafkaProperties properties) {
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
    public KafkaProducer<String, String> producerBuilder(String beanName, KafkaProperties properties) {
        Properties clientConfiguration = new Properties();
        clientConfiguration.putAll(properties.buildProducerProperties());
        clientConfiguration.put(ProducerConfig.CLIENT_ID_CONFIG, beanName);
        KafkaProducer<String, String> producer = new KafkaProducer<>(clientConfiguration);
        if (log.isInfoEnabled()) {
            log.info(String.format("%s started successful on bootstrap.servers %s", beanName, clientConfiguration.getProperty(ProducerConfig.CLIENT_ID_CONFIG)));
        }
        producerMap.put(beanName, producer);
        return producer;
    }

    /**
     * 获取 PushConsumerBuilder
     *
     * @param properties 配置
     * @param groupId    消费分组id
     * @param index      排序
     * @return PushConsumerBuilder
     */
    public KafkaConsumer<String, String> getConsumerBuilder(KafkaProperties properties, String groupId, int index) {
        Properties config = new Properties();
        config.putAll(properties.buildConsumerProperties());
        config.put(ProducerConfig.CLIENT_ID_CONFIG, groupId + "_" + index);
        config.put(CommonClientConfigs.GROUP_ID_CONFIG, groupId);
        config.put(CommonClientConfigs.GROUP_INSTANCE_ID_CONFIG, groupId + "_" + UUID.randomUUID());
        return new KafkaConsumer<String, String>(config);
    }

    /**
     * 转换配置
     * @param lokiProperties loki配置
     * @param kafkaProperties kafka配置
     */
    public void convert(LokiProperties lokiProperties, KafkaProperties kafkaProperties) {
        GlobalConfig.MqConfig mqConfig = lokiProperties.getGlobalConfig().getMqConfig();
        if (mqConfig.getAddress() != null && !mqConfig.getAddress().isEmpty()) {
            kafkaProperties.setBootstrapServers(Arrays.stream(mqConfig.getAddress().split(",")).collect(Collectors.toList()));
        }
        String hostName = "unknown";
        try {
            hostName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            log.error("get hostName error", e);
        }
        kafkaProperties.setClientId(hostName);
    }
}
