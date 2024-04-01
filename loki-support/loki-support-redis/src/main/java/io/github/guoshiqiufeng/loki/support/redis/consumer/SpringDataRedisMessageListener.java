package io.github.guoshiqiufeng.loki.support.redis.consumer;

import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerRecord;
import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineUtils;
import io.github.guoshiqiufeng.loki.support.core.util.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/3/20 13:47
 */
@Slf4j
public class SpringDataRedisMessageListener implements MessageListener {

    final StringRedisTemplate stringRedisTemplate;
    final Function<ConsumerRecord, Void> function;

    public SpringDataRedisMessageListener(StringRedisTemplate stringRedisTemplate, Function<ConsumerRecord, Void> function) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.function = function;
    }

    /**
     * Callback for processing received objects through Redis.
     *
     * @param message message must not be {@literal null}.
     * @param pattern pattern matching the channel (if specified) - can be {@literal null}.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = stringRedisTemplate.getStringSerializer().deserialize(message.getChannel());
        String messageBody = stringRedisTemplate.getStringSerializer().deserialize(message.getBody());
        if (log.isDebugEnabled()) {
            log.debug("{} onMessage : {}", channel, messageBody);
        }
        ConsumerRecord consumerRecord = new ConsumerRecord(channel, null, null,
                null, null, messageBody);
        consumerRecord = PipelineUtils.processListener(consumerRecord);
        if (consumerRecord != null) {
            ConsumerRecord finalConsumerRecord = consumerRecord;
            CompletableFuture.runAsync(() -> function.apply(finalConsumerRecord), ThreadPoolUtils.getSingleThreadPool());
        }
    }
}
