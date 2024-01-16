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

import lombok.Data;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.context.properties.source.MutuallyExclusiveConfigurationPropertiesException;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.unit.DataSize;

import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;

/**
 * Configuration properties for Spring for Apache Kafka.
 * Users should refer to Kafka documentation for complete descriptions of these properties.
 *
 * @author Gary Russell, Stephane Nicoll, Artem Bilan, Nakul Mishra, Tomaz Fernandes
 * @since 1.5.0
 */
@Data
public class KafkaProperties {

    /**
     * Comma-delimited list of host:port pairs to use for establishing the initial
     * connections to the Kafka cluster. Applies to all components unless overridden.
     */
    private List<String> bootstrapServers = new ArrayList<>(Collections.singletonList("localhost:9092"));

    /**
     * ID to pass to the server when making requests. Used for server-side logging.
     */
    private String clientId;

    /**
     * Additional properties, common to producers and consumers, used to configure the
     * client.
     */
    private final Map<String, String> properties = new HashMap<>();

    private final Consumer consumer = new Consumer();

    private final Producer producer = new Producer();

    private final Ssl ssl = new Ssl();

    private final Security security = new Security();

    private Map<String, Object> buildCommonProperties() {
        Map<String, Object> properties = new HashMap<>();
        if (this.bootstrapServers != null) {
            properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        }
        if (this.clientId != null) {
            properties.put(CommonClientConfigs.CLIENT_ID_CONFIG, this.clientId);
        }
        properties.putAll(this.ssl.buildProperties());
        properties.putAll(this.security.buildProperties());
        if (!CollectionUtils.isEmpty(this.properties)) {
            properties.putAll(this.properties);
        }
        return properties;
    }

    public Map<String, Object> buildConsumerProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(this.consumer.buildProperties());
        return properties;
    }

    public Map<String, Object> buildProducerProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(this.producer.buildProperties());
        return properties;
    }

    @Data
    public static class Consumer implements Serializable {

        private final Ssl ssl = new Ssl();

        private final Security security = new Security();

        /**
         * Frequency with which the consumer offsets are auto-committed to Kafka if
         * 'enable.auto.commit' is set to true.
         */
        private Duration autoCommitInterval;

        /**
         * What to do when there is no initial offset in Kafka or if the current offset no
         * longer exists on the server.
         */
        private String autoOffsetReset;

        /**
         * Comma-delimited list of host:port pairs to use for establishing the initial
         * connections to the Kafka cluster. Overrides the global property, for consumers.
         */
        private List<String> bootstrapServers;

        /**
         * ID to pass to the server when making requests. Used for server-side logging.
         */
        private String clientId;

        /**
         * Whether the consumer's offset is periodically committed in the background.
         */
        private Boolean enableAutoCommit;

        /**
         * Maximum amount of time the server blocks before answering the fetch request if
         * there isn't sufficient data to immediately satisfy the requirement given by
         * "fetch-min-size".
         */
        private Duration fetchMaxWait;

        /**
         * Minimum amount of data the server should return for a fetch request.
         */
        private DataSize fetchMinSize;

        /**
         * Unique string that identifies the consumer group to which this consumer
         * belongs.
         */
        private String groupId;

        /**
         * Expected time between heartbeats to the consumer coordinator.
         */
        private Duration heartbeatInterval;

        /**
         * Isolation level for reading messages that have been written transactionally.
         */
        private org.springframework.boot.autoconfigure.kafka.KafkaProperties.IsolationLevel isolationLevel = org.springframework.boot.autoconfigure.kafka.KafkaProperties.IsolationLevel.READ_UNCOMMITTED;

        /**
         * Deserializer class for keys.
         */
        private Class<?> keyDeserializer = StringDeserializer.class;

        /**
         * Deserializer class for values.
         */
        private Class<?> valueDeserializer = StringDeserializer.class;

        /**
         * Maximum number of records returned in a single call to poll().
         */
        private Integer maxPollRecords;

        /**
         * Additional consumer-specific properties used to configure the client.
         */
        private final Map<String, String> properties = new HashMap<>();

        public Map<String, Object> buildProperties() {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getAutoCommitInterval)
                    .asInt(Duration::toMillis)
                    .to(properties.in(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG));
            map.from(this::getAutoOffsetReset).to(properties.in(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
            map.from(this::getBootstrapServers).to(properties.in(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
            map.from(this::getClientId).to(properties.in(ConsumerConfig.CLIENT_ID_CONFIG));
            map.from(this::getEnableAutoCommit).to(properties.in(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
            map.from(this::getFetchMaxWait)
                    .asInt(Duration::toMillis)
                    .to(properties.in(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG));
            map.from(this::getFetchMinSize)
                    .asInt(DataSize::toBytes)
                    .to(properties.in(ConsumerConfig.FETCH_MIN_BYTES_CONFIG));
            map.from(this::getGroupId).to(properties.in(ConsumerConfig.GROUP_ID_CONFIG));
            map.from(this::getHeartbeatInterval)
                    .asInt(Duration::toMillis)
                    .to(properties.in(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG));
            map.from(() -> getIsolationLevel().name().toLowerCase(Locale.ROOT))
                    .to(properties.in(ConsumerConfig.ISOLATION_LEVEL_CONFIG));
            map.from(this::getKeyDeserializer).to(properties.in(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
            map.from(this::getValueDeserializer).to(properties.in(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
            map.from(this::getMaxPollRecords).to(properties.in(ConsumerConfig.MAX_POLL_RECORDS_CONFIG));
            return properties.with(this.ssl, this.security, this.properties);
        }

    }

    @Data
    public static class Producer implements Serializable {

        private final Ssl ssl = new Ssl();

        private final Security security = new Security();

        /**
         * Number of acknowledgments the producer requires the leader to have received
         * before considering a request complete.
         */
        private String acks;

        /**
         * Default batch size. A small batch size will make batching less common and may
         * reduce throughput (a batch size of zero disables batching entirely).
         */
        private DataSize batchSize;

        /**
         * Comma-delimited list of host:port pairs to use for establishing the initial
         * connections to the Kafka cluster. Overrides the global property, for producers.
         */
        private List<String> bootstrapServers;

        /**
         * Total memory size the producer can use to buffer records waiting to be sent to
         * the server.
         */
        private DataSize bufferMemory;

        /**
         * ID to pass to the server when making requests. Used for server-side logging.
         */
        private String clientId;

        /**
         * Compression type for all data generated by the producer.
         */
        private String compressionType;

        /**
         * Serializer class for keys.
         */
        private Class<?> keySerializer = StringSerializer.class;

        /**
         * Serializer class for values.
         */
        private Class<?> valueSerializer = StringSerializer.class;

        /**
         * When greater than zero, enables retrying of failed sends.
         */
        private Integer retries;

        /**
         * When non empty, enables transaction support for producer.
         */
        private String transactionIdPrefix;

        /**
         * Additional producer-specific properties used to configure the client.
         */
        private final Map<String, String> properties = new HashMap<>();

        public Map<String, Object> buildProperties() {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getAcks).to(properties.in(ProducerConfig.ACKS_CONFIG));
            map.from(this::getBatchSize).asInt(DataSize::toBytes).to(properties.in(ProducerConfig.BATCH_SIZE_CONFIG));
            map.from(this::getBootstrapServers).to(properties.in(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
            map.from(this::getBufferMemory)
                    .as(DataSize::toBytes)
                    .to(properties.in(ProducerConfig.BUFFER_MEMORY_CONFIG));
            map.from(this::getClientId).to(properties.in(ProducerConfig.CLIENT_ID_CONFIG));
            map.from(this::getCompressionType).to(properties.in(ProducerConfig.COMPRESSION_TYPE_CONFIG));
            map.from(this::getKeySerializer).to(properties.in(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
            map.from(this::getRetries).to(properties.in(ProducerConfig.RETRIES_CONFIG));
            map.from(this::getValueSerializer).to(properties.in(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
            return properties.with(this.ssl, this.security, this.properties);
        }

    }

    @Data
    public static class Ssl implements Serializable {

        /**
         * Password of the private key in either key store key or key store file.
         */
        private String keyPassword;

        /**
         * Certificate chain in PEM format with a list of X.509 certificates.
         */
        private String keyStoreCertificateChain;

        /**
         * Private key in PEM format with PKCS#8 keys.
         */
        private String keyStoreKey;

        /**
         * Location of the key store file.
         */
        private Resource keyStoreLocation;

        /**
         * Store password for the key store file.
         */
        private String keyStorePassword;

        /**
         * Type of the key store.
         */
        private String keyStoreType;

        /**
         * Trusted certificates in PEM format with X.509 certificates.
         */
        private String trustStoreCertificates;

        /**
         * Location of the trust store file.
         */
        private Resource trustStoreLocation;

        /**
         * Store password for the trust store file.
         */
        private String trustStorePassword;

        /**
         * Type of the trust store.
         */
        private String trustStoreType;

        /**
         * SSL protocol to use.
         */
        private String protocol;

        public Map<String, Object> buildProperties() {
            validate();
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getKeyPassword).to(properties.in(SslConfigs.SSL_KEY_PASSWORD_CONFIG));
            map.from(this::getKeyStoreCertificateChain)
                    .to(properties.in(SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG));
            map.from(this::getKeyStoreKey).to(properties.in(SslConfigs.SSL_KEYSTORE_KEY_CONFIG));
            map.from(this::getKeyStoreLocation)
                    .as(this::resourceToPath)
                    .to(properties.in(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            map.from(this::getKeyStorePassword).to(properties.in(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));
            map.from(this::getKeyStoreType).to(properties.in(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG));
            map.from(this::getTrustStoreCertificates).to(properties.in(SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_CONFIG));
            map.from(this::getTrustStoreLocation)
                    .as(this::resourceToPath)
                    .to(properties.in(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            map.from(this::getTrustStorePassword).to(properties.in(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            map.from(this::getTrustStoreType).to(properties.in(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG));
            map.from(this::getProtocol).to(properties.in(SslConfigs.SSL_PROTOCOL_CONFIG));
            return properties;
        }

        private void validate() {
            MutuallyExclusiveConfigurationPropertiesException.throwIfMultipleNonNullValuesIn((entries) -> {
                entries.put("spring.kafka.ssl.key-store-key", this.getKeyStoreKey());
                entries.put("spring.kafka.ssl.key-store-location", this.getKeyStoreLocation());
            });
            MutuallyExclusiveConfigurationPropertiesException.throwIfMultipleNonNullValuesIn((entries) -> {
                entries.put("spring.kafka.ssl.trust-store-certificates", this.getTrustStoreCertificates());
                entries.put("spring.kafka.ssl.trust-store-location", this.getTrustStoreLocation());
            });
        }

        private String resourceToPath(Resource resource) {
            try {
                return resource.getFile().getAbsolutePath();
            } catch (IOException ex) {
                throw new IllegalStateException("Resource '" + resource + "' must be on a file system", ex);
            }
        }

    }

    @Data
    public static class Security implements Serializable {

        /**
         * Security protocol used to communicate with brokers.
         */
        private String protocol;

        public Map<String, Object> buildProperties() {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getProtocol).to(properties.in(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));
            return properties;
        }

    }

    private static class Properties extends HashMap<String, Object> {

        <V> java.util.function.Consumer<V> in(String key) {
            return (value) -> put(key, value);
        }

        Properties with(Ssl ssl, Security security, Map<String, String> properties) {
            putAll(ssl.buildProperties());
            putAll(security.buildProperties());
            putAll(properties);
            return this;
        }

    }
}
