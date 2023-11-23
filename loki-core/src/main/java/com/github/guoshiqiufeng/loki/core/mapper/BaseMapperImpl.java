package com.github.guoshiqiufeng.loki.core.mapper;

import com.alibaba.fastjson2.JSON;
import com.github.guoshiqiufeng.loki.core.entity.MessageInfo;
import com.github.guoshiqiufeng.loki.core.exception.LokiException;
import com.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import com.github.guoshiqiufeng.loki.core.toolkit.EntityInfoHelper;
import com.github.guoshiqiufeng.loki.enums.MqType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/21 16:24
 */
@Slf4j
public class BaseMapperImpl<T> implements BaseMapper<T> {

    @Setter
    private HandlerHolder handlerHolder;

    @Setter
    private Class<?> entityClass;

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
