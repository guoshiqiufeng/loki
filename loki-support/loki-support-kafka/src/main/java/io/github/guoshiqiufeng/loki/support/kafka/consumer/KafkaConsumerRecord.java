package io.github.guoshiqiufeng.loki.support.kafka.consumer;

import io.github.guoshiqiufeng.loki.constant.Constant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * kafka消费记录
 * @author yanghq
 * @version 1.0
 * @since 2024/1/11 13:25
 */
public class KafkaConsumerRecord<K, V> extends ConsumerRecord<K, V> {

    /**
     * 标签
     */
    private final String tag;

    public KafkaConsumerRecord(ConsumerRecord<K, V> consumerRecord) {
        super(consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(),
                consumerRecord.timestamp(), consumerRecord.timestampType(),
                consumerRecord.serializedKeySize(), consumerRecord.serializedValueSize(),
                consumerRecord.key(), consumerRecord.value(),
                consumerRecord.headers(), consumerRecord.leaderEpoch());
        this.tag = getTagFromHeaders(consumerRecord.headers());
    }

    public String tag() {
        return this.tag;
    }

    /**
     * 获取tag
     *
     * @param headers headers
     * @return tag
     */
    private String getTagFromHeaders(Headers headers) {
        return Optional.ofNullable(headers.headers(Constant.KAFKA_TAG))
                .map(h -> StreamSupport.stream(h.spliterator(), false)
                        .collect(Collectors.toList()))
                .filter(list -> !list.isEmpty())
                .map(list -> new String(list.get(0).value()))
                .orElse("");
    }
}
