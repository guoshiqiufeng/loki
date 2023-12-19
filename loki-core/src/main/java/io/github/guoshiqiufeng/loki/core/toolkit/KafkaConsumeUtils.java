package io.github.guoshiqiufeng.loki.core.toolkit;

import io.github.guoshiqiufeng.loki.constant.Constant;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.header.Headers;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@UtilityClass
public class KafkaConsumeUtils {

    public void consumeMessage(KafkaConsumer<String, String> consumer, String topic, String tag, Function<ConsumerRecord<String, String>, Void> function) {
        try {
            consumer.subscribe(Collections.singletonList(topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(Long.MAX_VALUE));
                records.forEach(record -> {
                    if (tag == null || tag.isEmpty() || "*".equals(tag)) {
                        function.apply(record);
                    } else {
                        String recordTag = getTagFromHeaders(record.headers());
                        if (tag.equals(recordTag)) {
                            function.apply(record);
                        }
                    }
                });
            }
        } catch (WakeupException e) {
            // ignore, we're closing
        } catch (Exception e) {
            log.error("Unexpected error", e);
        } finally {
            consumer.close();
        }
    }

    private String getTagFromHeaders(Headers headers) {
        return Optional.ofNullable(headers.headers(Constant.KAFKA_TAG))
                .map(h -> StreamSupport.stream(h.spliterator(), false)
                        .collect(Collectors.toList()))
                .filter(list -> !list.isEmpty())
                .map(list -> new String(list.get(0).value()))
                .orElse(null);
    }
}