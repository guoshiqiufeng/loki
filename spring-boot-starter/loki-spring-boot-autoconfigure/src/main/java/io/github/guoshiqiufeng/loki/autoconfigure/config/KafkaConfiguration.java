package io.github.guoshiqiufeng.loki.autoconfigure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Configuration;

/**
 * kafka自动配置
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/14 16:43
 */
@Slf4j
@ConditionalOnProperty(prefix = "loki.global-config.mq-config", name = "mq-type", havingValue = "KAFKA")
@Configuration
@AutoConfigureAfter(KafkaAutoConfiguration.class)
@ConditionalOnBean(KafkaProperties.class)
public class KafkaConfiguration {

    /**
     * kafka配置参数覆盖
     * @param properties kafka配置参数
     * @return kafka配置参数
     */
//    @Bean
//    @ConfigurationProperties(prefix = "spring.kafka")
//    public KafkaProperties kafkaProperties(LokiProperties properties) {
//        KafkaProperties kafkaProperties = new KafkaProperties();
//        ArrayList<String> bootstrapServers = new ArrayList<>(1);
//        bootstrapServers.add(properties.getGlobalConfig().getMqConfig().getAddress());
//        kafkaProperties.setBootstrapServers(bootstrapServers);
//        return kafkaProperties;
//    }

    /**
     * 构造函数
     */
//    public KafkaConfiguration(KafkaProperties kafkaProperties, LokiProperties properties) {
//        ArrayList<String> bootstrapServers = new ArrayList<>(1);
//        bootstrapServers.add(properties.getGlobalConfig().getMqConfig().getAddress());
//        kafkaProperties.setBootstrapServers(bootstrapServers);
//        log.debug("kafka auto configuration init");
//    }
}
