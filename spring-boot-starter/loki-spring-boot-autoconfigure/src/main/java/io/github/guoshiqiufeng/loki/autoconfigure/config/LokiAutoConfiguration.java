package io.github.guoshiqiufeng.loki.autoconfigure.config;

import io.github.guoshiqiufeng.loki.annotation.MessageListener;
import io.github.guoshiqiufeng.loki.autoconfigure.register.LokiRegistrar;
import io.github.guoshiqiufeng.loki.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.core.handler.Handler;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.handler.impl.RocketMqHandler;
import io.github.guoshiqiufeng.loki.core.toolkit.RocketMqConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * loki自动配置类
 * @author yanghq
 * @version 1.0
 * @since 2023/11/16 09:26
 */
@Slf4j
@Configuration
//@ConditionalOnProperty(prefix = "loki", name = "enabled", matchIfMissing = true)
public class LokiAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "loki")
    @ConditionalOnMissingBean(AutoConfigurationProperties.class)
    public AutoConfigurationProperties lokiProperties() {
        return new AutoConfigurationProperties();
    }

    @Bean
    //@ConditionalOnProperty(prefix = "loki.global-config.mq-config.mq-type", name = "ROCKET_MQ")
    @ConditionalOnMissingBean(Producer.class)
    public Producer defaultProducer(LokiProperties properties) throws ClientException {
        return RocketMqConfigUtils.producerBuilder("defaultProducer", properties);
    }

    @Bean
    public <T> LokiRegistrar<T> lokiRegistrar(HandlerHolder handlerHolder, List<Handler> handler, LokiProperties lokiProperties,
                                              List<MessageListener<T>> messageListenerList) {
        return new LokiRegistrar<T>(handlerHolder, lokiProperties, messageListenerList);
    }

    @Bean
    public HandlerHolder handlerHolder() {
        return new HandlerHolder();
    }

    @Bean
    public List<Handler> handler(LokiProperties properties, HandlerHolder handlerHolder) {
        RocketMqHandler rocketMqHandler = new RocketMqHandler(properties, handlerHolder);
        ArrayList<Handler> handler = new ArrayList<Handler>(1);
        handler.add(rocketMqHandler);
        return handler;
    }

}
