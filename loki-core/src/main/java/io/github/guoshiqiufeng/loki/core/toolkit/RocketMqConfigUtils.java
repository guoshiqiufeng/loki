package io.github.guoshiqiufeng.loki.core.toolkit;


import io.github.guoshiqiufeng.loki.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.core.config.LokiProperties;
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
        // TODO 获取/创建对应 Producer
        if (producerMap.get(beanName) == null) {
            // TODO 创建 Producer
            Producer producer = producerBuilder(beanName, properties);
            // TODO 添加到 producerMap
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
                //.setTopics("loki")
                // 设置重试次数，默认为3
                .setMaxAttempts(3)
                .build();
        log.info(String.format("%s started successful on endpoints %s", beanName, clientConfiguration.getEndpoints()));
        producerMap.put(beanName, producer);
        return producer;
    }

    /**
     * 获取 PushConsumerBuilder
     * @param properties 配置
     * @return PushConsumerBuilder
     */
    public PushConsumerBuilder getPushConsumerBuilder(LokiProperties properties) {
        ClientConfiguration clientConfiguration = getClientConfiguration(properties);
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        return provider.newPushConsumerBuilder()
                .setClientConfiguration(clientConfiguration);
    }

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

