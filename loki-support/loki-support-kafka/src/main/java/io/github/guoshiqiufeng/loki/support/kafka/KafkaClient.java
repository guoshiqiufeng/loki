package io.github.guoshiqiufeng.loki.support.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

/**
 * kafka客户端
 * @author yanghq
 * @version 1.0
 * @since 2024/1/6 10:35
 */
public interface KafkaClient {

    Future<RecordMetadata> send(String producerName, ProducerRecord<String, String> record);


    KafkaConsumer<String, String> getConsumer(String consumerGroup, Integer index);
}
