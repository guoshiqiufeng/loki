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

/**
 * kafka消费工具类
 */
@Slf4j
@UtilityClass
public class KafkaConsumeUtils {

    /**
     * 消费消息
     * @param consumer 消费者
     * @param topic 主题
     * @param tag 标签
     * @param function 回调方法
     */
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
            if (log.isErrorEnabled()) {
                log.error("Unexpected error", e);
            }
        } finally {
            consumer.close();
        }
    }

    /**
     * 获取tag
     * @param headers 头信息
     * @return tag
     */
    private String getTagFromHeaders(Headers headers) {
        return Optional.ofNullable(headers.headers(Constant.KAFKA_TAG))
                .map(h -> StreamSupport.stream(h.spliterator(), false)
                        .collect(Collectors.toList()))
                .filter(list -> !list.isEmpty())
                .map(list -> new String(list.get(0).value()))
                .orElse(null);
    }
}