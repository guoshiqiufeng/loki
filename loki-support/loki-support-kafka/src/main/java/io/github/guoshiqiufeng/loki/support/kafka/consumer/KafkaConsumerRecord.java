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
package io.github.guoshiqiufeng.loki.support.kafka.consumer;

import io.github.guoshiqiufeng.loki.constant.Constant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * kafka消费记录
 *
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
