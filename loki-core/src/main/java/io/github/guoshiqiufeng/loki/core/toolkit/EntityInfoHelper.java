/*
 * Copyright (c) 2023-2023, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.loki.core.toolkit;

import io.github.guoshiqiufeng.loki.annotation.MessageKey;
import io.github.guoshiqiufeng.loki.annotation.MessageName;
import io.github.guoshiqiufeng.loki.core.entity.MessageInfo;
import io.github.guoshiqiufeng.loki.core.exception.LokiException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 实体信息工具类
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/22 13:33
 */
@Slf4j
@UtilityClass
public class EntityInfoHelper {

    /**
     * 获取消息信息
     *
     * @param entityClass 实体类class
     * @return 消息信息
     */
    public MessageInfo getMessageInfo(Class<?> entityClass) {
        MessageInfo result = new MessageInfo();
        // 获取实体注解中信息
        MessageName annotation = getAnnotation(entityClass, MessageName.class);
        if (annotation == null) {
            throw new LokiException("send entity is not set MessageName annotation");
        }
        // 获取topic
        String topic = annotation.topic();
        if (StringUtils.isEmpty(topic)) {
            throw new LokiException("send entity topic is empty");
        }
        // 获取tag
        String tag = annotation.tag();
        // 获取生产者
        String producer = annotation.producer();
        // 获取消费者分组
        String consumerGroup = annotation.consumerGroup();
        // 获取延时
        long deliveryTimestamp = annotation.deliveryTimestamp();
        int consumptionThreadCount = annotation.consumptionThreadCount();
        int maxCacheMessageCount = annotation.maxCacheMessageCount();

        result.setTopic(topic)
                .setTag(tag)
                .setProducer(producer)
                .setConsumerGroup(consumerGroup)
                .setDeliveryTimestamp(deliveryTimestamp)
                .setConsumptionThreadCount(consumptionThreadCount)
                .setMaxCacheMessageCount(maxCacheMessageCount)
        ;
        return result;
    }

    /**
     * 获取消息key
     *
     * @param entityClass 实体类class
     * @param entity      实体类
     * @return 消息key
     */
    public String[] getMessageKeys(Class<?> entityClass, Object entity) {
        Set<String> keys = new HashSet<>();
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            MessageKey messageKeyAnnotation = declaredField.getAnnotation(MessageKey.class);
            if (messageKeyAnnotation != null) {
                try {
                    declaredField.setAccessible(true);
                    Object value = declaredField.get(entity);
                    String messageKey = Optional.ofNullable(value)
                            .map(Object::toString).orElse(null);
                    log.debug("EntityInfoHelper# send message key:{}", messageKey);
                    if (messageKey != null) {
                        keys.add(messageKey);
                    }
                } catch (Exception e) {
                    throw new LokiException("send entity get message key error");
                }
            }
        }
        if (keys.isEmpty()) {
            return null;
        }
        return keys.toArray(new String[0]);
    }

    /**
     * 获取注解
     */
    private static <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return element.getAnnotation(annotationType);
    }
}
