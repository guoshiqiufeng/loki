package io.github.guoshiqiufeng.loki.support.redis.impl.spring;

import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerRecord;
import io.github.guoshiqiufeng.loki.support.redis.consumer.SpringDataRedisMessageListener;
import io.github.guoshiqiufeng.loki.support.redis.impl.BaseRedisClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/3/19 16:48
 */
public class SpringDataRedisImpl extends BaseRedisClient {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisMessageListenerContainer container;

    public SpringDataRedisImpl(StringRedisTemplate stringRedisTemplate, RedisMessageListenerContainer redisMessageListenerContainer) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.container = redisMessageListenerContainer;
    }

    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 消息
     * @return 发布结果
     */
    @Override
    public long publish(String channel, String message) {
        byte[] rawChannel = stringRedisTemplate.getStringSerializer().serialize(channel);
        byte[] rawMessage = stringRedisTemplate.getStringSerializer().serialize(message);

        if (rawChannel == null || rawMessage == null) {
            return 0L;
        }
        return Optional.ofNullable(
                        stringRedisTemplate.execute(connection ->
                                connection.publish(rawChannel, rawMessage), true))
                .orElse(0L);
    }


    /**
     * 订阅消息
     *
     * @param function 回调
     * @param channels 频道
     */
    @Override
    public void subscribe(Function<ConsumerRecord, Void> function, String... channels) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(new SpringDataRedisMessageListener(stringRedisTemplate, function));
        for (String channel : channels) {
            container.addMessageListener(adapter, new ChannelTopic(channel));
        }
    }

    /**
     * 订阅消息
     *
     * @param function 回调
     * @param patterns 规则
     */
    @Override
    public void psubscribe(Function<ConsumerRecord, Void> function, String... patterns) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(new SpringDataRedisMessageListener(stringRedisTemplate, function));
        for (String pattern : patterns) {
            container.addMessageListener(adapter, new PatternTopic(pattern));
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 建
     * @return
     */
    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * set
     *
     * @param key          建
     * @param value        值
     * @param expireAtTime 过期时间戳
     */
    @Override
    public void set(String key, String value, Long expireAtTime) {
        stringRedisTemplate.opsForValue().set(key, value);
        if (expireAtTime != null) {
            stringRedisTemplate.expire(key, expireAtTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * hset
     *
     * @param key   建
     * @param field 字段
     * @param value 值
     */
    @Override
    public void hset(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * hget
     *
     * @param key   建
     * @param field 字段
     * @return
     */
    @Override
    public String hget(String key, String field) {
        return (String) stringRedisTemplate.opsForHash().get(key, field);
    }

    /**
     * 删除
     *
     * @param key   建
     * @param field 字段
     */
    @Override
    public void hdel(String key, String... field) {
        stringRedisTemplate.opsForHash().delete(key, (Object[]) field);
    }

    /**
     * hkeys
     *
     * @param key 建
     * @return
     */
    @Override
    public Set<String> hkeys(String key) {
        return stringRedisTemplate.opsForHash().keys(key).stream().map(Object::toString).collect(Collectors.toSet());
    }
}
