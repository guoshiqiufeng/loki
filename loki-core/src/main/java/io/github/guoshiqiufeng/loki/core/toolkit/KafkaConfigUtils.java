package io.github.guoshiqiufeng.loki.core.toolkit;

import io.github.guoshiqiufeng.loki.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.core.config.LokiProperties;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * kafka配置工具类
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/16 13:03
 */
@Slf4j
@UtilityClass
public class KafkaConfigUtils {

    private final Map<String, KafkaProducer<String, String>> producerMap = new HashMap<>();

    /**
     * 获取 Producer
     *
     * @param beanName   beanName
     * @param properties 配置
     * @return Producer
     */
    public KafkaProducer<String, String> getProducer(String beanName, LokiProperties properties) {
        if (beanName == null || beanName.isEmpty()) {
            beanName = "defaultProducer";
        }
        if (producerMap.get(beanName) == null) {
            KafkaProducer<String, String> producer = producerBuilder(beanName, properties);
            producerMap.put(beanName, producer);
        }
        return producerMap.get(beanName);
    }


    /**
     * 创建 Producer
     *
     * @param beanName   beanName
     * @param properties 配置
     * @return Producer
     */
    public KafkaProducer<String, String> producerBuilder(String beanName, LokiProperties properties) {
        Properties clientConfiguration = getClientConfiguration(properties);
        KafkaProducer<String, String> producer = new KafkaProducer<>(clientConfiguration, new StringSerializer(), new StringSerializer());
        log.info(String.format("%s started successful on bootstrap.servers %s", beanName, clientConfiguration.getProperty(ProducerConfig.CLIENT_ID_CONFIG)));
        producerMap.put(beanName, producer);
        return producer;
    }

    /**
     * 获取 PushConsumerBuilder
     *
     * @param properties 配置
     * @return PushConsumerBuilder
     */
    public KafkaConsumer<String, String> getPushConsumerBuilder(LokiProperties properties) {
        Properties config = getClientConfiguration(properties);
        return new KafkaConsumer<String, String>(config, new StringDeserializer(), new StringDeserializer());
    }

    /**
     * 获取 ClientConfiguration
     *
     * @param properties 配置
     * @return ClientConfiguration
     */
    private Properties getClientConfiguration(LokiProperties properties) {
        Properties config = new Properties();
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {
        }
        GlobalConfig.MqConfig mqConfig = properties.getGlobalConfig().getMqConfig();
        config.put(ProducerConfig.CLIENT_ID_CONFIG, hostName);
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, mqConfig.getAddress());
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        return config;
    }

}
