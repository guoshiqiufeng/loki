package io.github.guoshiqiufeng.loki.core.mapper;

import com.alibaba.fastjson2.JSON;
import io.github.guoshiqiufeng.loki.annotation.SendMessage;
import io.github.guoshiqiufeng.loki.core.entity.MessageInfo;
import io.github.guoshiqiufeng.loki.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.toolkit.EntityInfoHelper;
import io.github.guoshiqiufeng.loki.enums.MqType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * 基础mapper实现类
 *
 * @param <T> 消息类型
 * @author yanghq
 * @version 1.0
 * @since 2023/11/21 16:24
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

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

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


    public Object sendByAnnotation(SendMessage sendMessageAnnotation, Method method, Object[] args) {

        // send message
        boolean async = sendMessageAnnotation.async();
        String messageExpression = sendMessageAnnotation.message();
        String messageKeyExpression = sendMessageAnnotation.messageKey();

        String producer = sendMessageAnnotation.producer();
        String topic = sendMessageAnnotation.topic();
        String tag = sendMessageAnnotation.tag();
        long deliveryTimestamp = sendMessageAnnotation.deliveryTimestamp();

        if (topic == null || topic.isEmpty()) {
            // 获取默认topic
            MessageInfo messageInfo = EntityInfoHelper.getMessageInfo(entityClass);
            if (messageInfo != null && messageInfo.getTopic() != null && !messageInfo.getTopic().isEmpty()) {
                topic = messageInfo.getTopic();
                log.debug("BaseMapperImpl# sendByAnnotation set default topic:{}", topic);
            }
        }

        StandardEvaluationContext context = new MethodBasedEvaluationContext(null, method, args, nameDiscoverer);

        String messageContent = "";
        if (messageExpression != null && !messageExpression.isEmpty()) {
            Object messageContentValue = expressionParser.parseExpression(messageExpression).getValue(context);
            if (messageContentValue instanceof String) {
                messageContent = (String) messageContentValue;
            } else {
                messageContent = JSON.toJSONString(messageContentValue);
            }
        }
        String messageKeys = null;
        if (messageKeyExpression != null && !messageKeyExpression.isEmpty()) {
            Object messageKeyValue = expressionParser.parseExpression(messageKeyExpression).getValue(context);
            if (messageKeyValue instanceof String) {
                messageKeys = (String) messageKeyValue;
            } else {
                messageKeys = JSON.toJSONString(messageKeyValue);
            }
        }

        return async ? handlerHolder.route(MqType.ROCKET_MQ.getCode()).sendAsync(producer, topic, tag,
                messageContent, deliveryTimestamp, messageKeys) :
                handlerHolder.route(MqType.ROCKET_MQ.getCode()).send(producer, topic, tag,
                        messageContent, deliveryTimestamp, messageKeys);
    }


}
