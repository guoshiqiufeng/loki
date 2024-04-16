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
