package io.github.guoshiqiufeng.loki.autoconfigure.config;

import io.github.guoshiqiufeng.loki.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.core.handler.Handler;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.handler.impl.KafkaHandler;
import io.github.guoshiqiufeng.loki.enums.MqType;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * kafka自动配置
 * @author yanghq
 * @version 1.0
 * @since 2023/12/26 17:14
 */
@Configuration
@ConditionalOnProperty(prefix = "loki.global-config.mq-config", name = "mq-type", havingValue = "KAFKA")
public class KafkaAutoConfiguration {

    /**
     * Handler Bean列表，包含KafkaHandler
     *
     * @param properties    Loki配置
     * @param handlerHolder 处理器持有者
     * @return Handler 实例列表
     */
    @Bean
    public List<Handler> kafkaHandler(LokiProperties properties, HandlerHolder handlerHolder) {
        ArrayList<Handler> handler = new ArrayList<Handler>(1);
        if (properties.getGlobalConfig().getMqConfig().getMqType().equals(MqType.KAFKA)) {
            KafkaHandler kafkaHandler = new KafkaHandler(properties, handlerHolder);
            handler.add(kafkaHandler);
        } else {
            throw new LokiException("mq type is not support ");
        }
        return handler;
    }

    /**
     * HandlerHolder Bean，用于持有处理器
     *
     * @return HandlerHolder 实例
     */
    @Bean
    public HandlerHolder handlerHolder() {
        return new HandlerHolder();
    }
}
