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

import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerRecord;
import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineUtils;
import io.github.guoshiqiufeng.loki.support.kafka.consumer.KafkaConsumerRecord;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * kafka消费工具类
 *
 * @author yanghq
 */
@Slf4j
@UtilityClass
public class KafkaConsumeUtils {

    public void consumeMessageForPattern(KafkaConsumer<String, String> consumer, String topicPattern, String tag, Function<ConsumerRecord, Void> function) {
        try {
            consumer.subscribe(Pattern.compile(topicPattern));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(Long.MAX_VALUE));
                records.forEach(record -> {
                    KafkaConsumerRecord<String, String> kafkaConsumerRecord = new KafkaConsumerRecord<String, String>(record);
                    if (tag == null || tag.isEmpty() || "*".equals(tag)) {
                        ConsumerRecord consumerRecord = covertConsumerRecord(kafkaConsumerRecord);
                        consumerRecord = PipelineUtils.processListener(consumerRecord);
                        if (consumerRecord != null) {
                            function.apply(consumerRecord);
                        }
                    } else {
                        if (tag.equals(kafkaConsumerRecord.tag())) {
                            ConsumerRecord consumerRecord = covertConsumerRecord(kafkaConsumerRecord);
                            consumerRecord = PipelineUtils.processListener(consumerRecord);
                            if (consumerRecord != null) {
                                function.apply(consumerRecord);
                            }
                        }
                    }
                });
            }
        } catch (WakeupException e) {
            // ignore, we're closing
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Unexpected error", e);
            }
        } finally {
            consumer.close();
        }
    }

    /**
     * 消费消息
     *
     * @param consumer 消费者
     * @param topic    主题
     * @param tag      标签
     * @param function 回调方法
     */
    public void consumeMessage(KafkaConsumer<String, String> consumer, String topic, String tag, Function<ConsumerRecord, Void> function) {
        try {
            consumer.subscribe(Collections.singletonList(topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(Long.MAX_VALUE));
                records.forEach(record -> {
                    KafkaConsumerRecord<String, String> kafkaConsumerRecord = new KafkaConsumerRecord<String, String>(record);
                    if (tag == null || tag.isEmpty() || "*".equals(tag)) {
                        ConsumerRecord consumerRecord = covertConsumerRecord(kafkaConsumerRecord);
                        consumerRecord = PipelineUtils.processListener(consumerRecord);
                        if (consumerRecord != null) {
                            function.apply(consumerRecord);
                        }
                    } else {
                        if (tag.equals(kafkaConsumerRecord.tag())) {
                            ConsumerRecord consumerRecord = covertConsumerRecord(kafkaConsumerRecord);
                            consumerRecord = PipelineUtils.processListener(consumerRecord);
                            if (consumerRecord != null) {
                                function.apply(consumerRecord);
                            }
                        }
                    }
                });
            }
        } catch (WakeupException e) {
            // ignore, we're closing
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Unexpected error", e);
            }
        } finally {
            consumer.close();
        }
    }

    private ConsumerRecord covertConsumerRecord(KafkaConsumerRecord<String, String> kafkaConsumerRecord) {
        return new ConsumerRecord(kafkaConsumerRecord.topic(),
                kafkaConsumerRecord.tag(), getMsgId(kafkaConsumerRecord),
                null, Collections.singletonList(kafkaConsumerRecord.key()),
                kafkaConsumerRecord.value());
    }

    private String getMsgId(KafkaConsumerRecord<String, String> recordMetadata) {
        return recordMetadata.partition() + "_" + recordMetadata.offset();
    }
}
