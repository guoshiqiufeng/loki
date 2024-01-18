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
package io.github.guoshiqiufeng.loki.support.rocketmq.remoting.util;

import io.github.guoshiqiufeng.loki.support.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.rocketmq.remoting.config.RocketMQProperties;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

import java.util.HashMap;
import java.util.Map;

/**
 * rocketmq-remoting配置工具类
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/1/18 16:03
 */
@Slf4j
@UtilityClass
public class RocketRemotingConfigUtils {

    private final Map<String, DefaultMQProducer> producerMap = new HashMap<>();

    /**
     * 获取 Producer
     *
     * @param beanName   beanName
     * @param properties 配置
     * @return Producer
     */
    public DefaultMQProducer getProducer(String beanName, RocketMQProperties properties) {
        if (beanName == null || beanName.isEmpty()) {
            beanName = "defaultProducer";
        }
        if (producerMap.get(beanName) == null) {
            DefaultMQProducer producer = producerBuilder(beanName, properties);
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
    public DefaultMQProducer producerBuilder(String beanName, RocketMQProperties properties) {
        RocketMQProperties.Producer producerConfig = properties.getProducer();
        boolean enableMsgTrace = false;
        String customizedTraceTopic = "";
        if (producerConfig != null) {
            enableMsgTrace = producerConfig.isEnableMsgTrace();
            customizedTraceTopic = producerConfig.getCustomizedTraceTopic();
        }
        DefaultMQProducer producer = new DefaultMQProducer(beanName, enableMsgTrace, customizedTraceTopic);
        producer.setNamesrvAddr(properties.getNameServer());
        if (producerConfig != null) {
            if (StringUtils.isNotEmpty(producerConfig.getGroup())) {
                producer.setProducerGroup(producerConfig.getGroup());
            }
            producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
            producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
            producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
            producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
            producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());
            producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        }
        if (log.isInfoEnabled()) {
            log.info(String.format("%s started successful on bootstrap.servers %s", beanName, properties.getNameServer()));
        }
        try {
            producer.start();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("producer start error", e);
            }
        }
        producerMap.put(beanName, producer);
        return producer;
    }

    /**
     * 获取 DefaultMQPushConsumer
     *
     * @param properties 配置
     * @param groupId    消费分组id
     * @param index      排序
     * @return PushConsumerBuilder
     */
    public DefaultMQPushConsumer getConsumerBuilder(RocketMQProperties properties, String groupId, int index) {
        //设置消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupId + "_" + index);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setNamesrvAddr(properties.getNameServer());
        RocketMQProperties.Consumer consumerConfig = properties.getConsumer();
        if (consumerConfig != null) {
            consumer.setPullBatchSize(consumerConfig.getPullBatchSize());
            consumer.setPullInterval(consumerConfig.getPullInterval());
        }

        return consumer;
    }

    /**
     * 转换配置
     *
     * @param lokiProperties     loki配置
     * @param rocketMQProperties rocket配置
     */
    public void convert(LokiProperties lokiProperties, RocketMQProperties rocketMQProperties) {
        GlobalConfig.MqConfig mqConfig = lokiProperties.getGlobalConfig().getMqConfig();
        if (mqConfig.getAddress() != null && !mqConfig.getAddress().isEmpty()) {
            rocketMQProperties.setNameServer(mqConfig.getAddress());
        }
        RocketMQProperties.Producer producer = rocketMQProperties.getProducer();
        if (producer != null) {
            producer.setRetryTimesWhenSendFailed(mqConfig.getMaxAttempts());
            producer.setRetryTimesWhenSendAsyncFailed(mqConfig.getMaxAttempts());
        }
    }
}
