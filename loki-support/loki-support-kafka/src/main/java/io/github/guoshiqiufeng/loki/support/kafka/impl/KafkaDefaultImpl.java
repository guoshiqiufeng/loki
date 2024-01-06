package io.github.guoshiqiufeng.loki.support.kafka.impl;

import io.github.guoshiqiufeng.loki.support.kafka.KafkaClient;
import io.github.guoshiqiufeng.loki.support.kafka.utils.KafkaConfigUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.concurrent.Future;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/6 10:36
 */
public class KafkaDefaultImpl implements KafkaClient {

    final KafkaProperties kafkaProperties;;

    public KafkaDefaultImpl(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public Future<RecordMetadata> send(String producerName, ProducerRecord<String, String> record) {
        KafkaProducer<String, String> producer = KafkaConfigUtils.getProducer(producerName, kafkaProperties);
        return producer.send(record);
    }

    @Override
    public KafkaConsumer<String, String> getConsumer(String consumerGroup, Integer index) {
        return KafkaConfigUtils.getConsumerBuilder(kafkaProperties, consumerGroup, index);
    }
}
