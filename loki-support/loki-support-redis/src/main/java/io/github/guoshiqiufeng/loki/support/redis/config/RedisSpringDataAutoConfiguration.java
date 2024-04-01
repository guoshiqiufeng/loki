package io.github.guoshiqiufeng.loki.support.redis.config;

import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.redis.RedisClient;
import io.github.guoshiqiufeng.loki.support.redis.impl.spring.SpringDataRedisImpl;
import io.github.guoshiqiufeng.loki.support.redis.utils.RedisConfigUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/3/19 17:51
 */
@Configuration
@ConditionalOnClass(RedisOperations.class)
@ConditionalOnProperty(name = "loki.redis.support-type", havingValue = "spring-data", matchIfMissing = true)
public class RedisSpringDataAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedisMessageListenerContainer.class)
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    @ConditionalOnMissingBean(RedisClient.class)
    public RedisClient redisClient(LokiProperties lokiProperties, RedisProperties redisProperties,
                                   StringRedisTemplate stringRedisTemplate, RedisMessageListenerContainer redisMessageListenerContainer) {
        RedisConfigUtils.convert(lokiProperties, redisProperties);
        return new SpringDataRedisImpl(stringRedisTemplate, redisMessageListenerContainer);
    }
}
