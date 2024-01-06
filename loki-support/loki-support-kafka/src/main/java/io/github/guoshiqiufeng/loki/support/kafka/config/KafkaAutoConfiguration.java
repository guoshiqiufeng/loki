package io.github.guoshiqiufeng.loki.support.kafka.config;

import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.kafka.KafkaClient;
import io.github.guoshiqiufeng.loki.support.kafka.impl.KafkaDefaultImpl;
import io.github.guoshiqiufeng.loki.support.kafka.utils.KafkaConfigUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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
