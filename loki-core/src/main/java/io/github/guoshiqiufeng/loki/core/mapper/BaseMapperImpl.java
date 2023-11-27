package io.github.guoshiqiufeng.loki.core.mapper;

import com.alibaba.fastjson2.JSON;
import io.github.guoshiqiufeng.loki.core.entity.MessageInfo;
import io.github.guoshiqiufeng.loki.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.toolkit.EntityInfoHelper;
import io.github.guoshiqiufeng.loki.enums.MqType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * 基础mapper实现类
 * @author yanghq
 * @version 1.0
 * @since 2023/11/21 16:24
 * @param <T> 消息类型
 */
@Slf4j
public class BaseMapperImpl<T> implements BaseMapper<T> {

    /**
     * 具体事件处理持有者
     */
    @Setter
    private HandlerHolder handlerHolder;

    /**
     * 实体类class
     */
    @Setter
    private Class<?> entityClass;

    /**
     * 发送消息
     *
     * @param entity 消息实体
     * @return messageId 消息id
     */
    @Override
    public String send(T entity) {
        if (entity == null) {
            throw new LokiException("send entity must not be null");
        }
        log.debug("BaseMapperImpl# send message:{}", entity);
        MessageInfo messageInfo = EntityInfoHelper.getMessageInfo(entityClass);

        // 遍历字段， 获取是否存在@MessageKey注解
        String[] messageKeys = EntityInfoHelper.getMessageKeys(entityClass, entity);

        // TODO 根据序列化方式序列化消息
        String message = JSON.toJSONString(entity);
        return handlerHolder.route(MqType.ROCKET_MQ.getCode()).send(messageInfo.getProducer(),
                messageInfo.getTopic(), messageInfo.getTag(), message, messageInfo.getDeliveryTimestamp(), messageKeys);
    }

    /**
     * 发送异步消息
     *
     * @param entity 消息实体
     * @return messageId 消息id
     */
    @Override
    public CompletableFuture<String> sendAsync(T entity) {
        if (entity == null) {
            throw new LokiException("send entity must not be null");
        }
        log.debug("BaseMapperImpl# sendAsync message:{}", entity);
        MessageInfo messageInfo = EntityInfoHelper.getMessageInfo(entityClass);

        // 遍历字段， 获取是否存在@MessageKey注解
        String[] messageKeys = EntityInfoHelper.getMessageKeys(entityClass, entity);

        // TODO 根据序列化方式序列化消息
        String message = JSON.toJSONString(entity);
        return handlerHolder.route(MqType.ROCKET_MQ.getCode()).sendAsync(messageInfo.getProducer(),
                messageInfo.getTopic(), messageInfo.getTag(), message, messageInfo.getDeliveryTimestamp(), messageKeys);
    }


}
