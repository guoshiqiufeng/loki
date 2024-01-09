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
package io.github.guoshiqiufeng.loki.support.kafka.config;

import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.kafka.KafkaClient;
import io.github.guoshiqiufeng.loki.support.kafka.impl.KafkaDefaultImpl;
import io.github.guoshiqiufeng.loki.support.kafka.utils.KafkaConfigUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * kafka配置
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/1/6 10:13
 */
@Configuration
public class KafkaAutoConfiguration {


    /**
     * 配置文件
     *
     * @return 配置文件
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.kafka")
    @ConditionalOnMissingBean(KafkaProperties.class)
    public KafkaProperties kafkaProperties() {
        return new KafkaProperties();
    }

    @Bean
    @ConditionalOnMissingBean(KafkaClient.class)
    public KafkaClient kafkaClient(LokiProperties lokiProperties, KafkaProperties kafkaProperties) {
        KafkaConfigUtils.convert(lokiProperties, kafkaProperties);
        return new KafkaDefaultImpl(kafkaProperties);
    }

}
