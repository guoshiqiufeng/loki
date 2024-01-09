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
package io.github.guoshiqiufeng.loki.support.kafka.impl;

import io.github.guoshiqiufeng.loki.support.kafka.KafkaClient;
import io.github.guoshiqiufeng.loki.support.kafka.config.KafkaProperties;
import io.github.guoshiqiufeng.loki.support.kafka.utils.KafkaConfigUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

/**
 * kafka客户端默认实现
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/1/6 10:36
 */
public class KafkaDefaultImpl implements KafkaClient {

    /**
     * 配置文件
     */
    final KafkaProperties kafkaProperties;
    ;

    /**
     * 构造方法
     *
     * @param kafkaProperties kafka配置
     */
    public KafkaDefaultImpl(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * 发送消息
     *
     * @param producerName 生产者名称
     * @param record       消息
     * @return Future
     */
    @Override
    public Future<RecordMetadata> send(String producerName, ProducerRecord<String, String> record) {
        KafkaProducer<String, String> producer = KafkaConfigUtils.getProducer(producerName, kafkaProperties);
        return producer.send(record);
    }

    /**
     * 获取消费者
     *
     * @param consumerGroup 消费者组
     * @param index         消费者索引
     * @return 消费者
     */
    @Override
    public KafkaConsumer<String, String> getConsumer(String consumerGroup, Integer index) {
        return KafkaConfigUtils.getConsumerBuilder(kafkaProperties, consumerGroup, index);
    }
}
