package io.github.guoshiqiufeng.loki.autoconfigure.register;

import com.alibaba.fastjson2.JSON;
import io.github.guoshiqiufeng.loki.Listener;
import io.github.guoshiqiufeng.loki.MessageContent;
import io.github.guoshiqiufeng.loki.annotation.MessageListener;
import io.github.guoshiqiufeng.loki.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.core.entity.MessageInfo;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.toolkit.EntityInfoHelper;
import io.github.guoshiqiufeng.loki.core.toolkit.TypeUtils;
import io.github.guoshiqiufeng.loki.enums.MqType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * loki注册类
 *
 * @param <T> 监听器消息类型
 * @author yanghq
 * @version 1.0
 * @since 2023/11/16 13:41
 */
@Slf4j
public class LokiRegistrar<T> {

    private final HandlerHolder handlerHolder;

    private final LokiProperties lokiProperties;
    private final List<Listener<T>> listenerList;

    /**
     * 构造函数
     *
     * @param handlerHolder  处理器持有者
     * @param lokiProperties loki配置
     * @param listenerList   消息监听器列表
     */
    public LokiRegistrar(HandlerHolder handlerHolder, LokiProperties lokiProperties, List<Listener<T>> listenerList) {
        this.handlerHolder = handlerHolder;
        this.lokiProperties = lokiProperties;
        this.listenerList = listenerList;
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        // init config
        GlobalConfig globalConfig = lokiProperties.getGlobalConfig();
        if (globalConfig.isBanner()) {
            String version = Optional.ofNullable(this.getClass().getPackage().getImplementationVersion()).map(v -> "v" + v).
                    orElse("UNKNOWN");
            System.out.println("    __    ____  __ __ ____\n" +
                    "   / /   / __ \\/ //_//  _/\n" +
                    "  / /   / / / / ,<   / /  \n" +
                    " / /___/ /_/ / /| |_/ /   \n" +
                    "/_____/\\____/_/ |_/___/   \n" +
                    " :: LOKI ::       (" + version + ")\n"
            );
        }
        GlobalConfig.MqConfig mqConfig = globalConfig.getMqConfig();
        log.debug("mqConfig:{}", mqConfig);
        // init listener
        // 创建监听
        if (!CollectionUtils.isEmpty(listenerList)) {
            initListener(listenerList);
        }
    }


    /**
     * 创建监听
     *
     * @param listenerList 消息监听器列表
     */
    private void initListener(List<Listener<T>> listenerList) {
        for (Listener<T> listener : listenerList) {
            @SuppressWarnings("unchecked")
            Class<T> interfaceGenericType = (Class<T>) TypeUtils.getInterfaceGenericType(listener.getClass(), 0);
            try {
                MessageInfo messageInfo = null;
                try {
                    messageInfo = EntityInfoHelper.getMessageInfo(interfaceGenericType);
                } catch (Exception ignored) {
                }

                String consumerGroup = null, topic = null, tag = null;
                Integer consumptionThreadCount = null, maxCacheMessageCount = null;

                if (messageInfo != null) {
                    consumerGroup = messageInfo.getConsumerGroup();
                    topic = messageInfo.getTopic();
                    tag = messageInfo.getTag();
                    consumptionThreadCount = messageInfo.getConsumptionThreadCount();
                    maxCacheMessageCount = messageInfo.getMaxCacheMessageCount();
                }

                MessageListener annotation = listener.getClass().getAnnotation(MessageListener.class);
                if (annotation != null) {
                    consumerGroup = annotation.consumerGroup();
                    topic = annotation.topic();
                    tag = annotation.tag();
                    consumptionThreadCount = annotation.consumptionThreadCount();
                    maxCacheMessageCount = annotation.maxCacheMessageCount();
                }
                if (messageInfo == null && annotation == null) {
                    log.warn("messageListener:{} init error", listener.getClass().getName());
                    return;
                }

                handlerHolder.route(MqType.ROCKET_MQ.getCode()).pushMessageListener(consumerGroup,
                        topic, tag, consumptionThreadCount,
                        maxCacheMessageCount, messageContent -> {
                            // log.debug("messageContent:{}", messageContent)
                            String body = messageContent.getBody();
                            // TODO 序列化
                            T bodyObject = null;
                            if (body != null && body.trim().startsWith("{")) {
                                bodyObject = JSON.parseObject(body, interfaceGenericType);
                            }

                            MessageContent<T> tMessageContent = new MessageContent<T>()
                                    .setMessageId(messageContent.getMessageId())
                                    .setTopic(messageContent.getTopic())
                                    .setTag(messageContent.getTag())
                                    .setKeys(messageContent.getKeys())
                                    .setMessageGroup(messageContent.getMessageGroup())
                                    .setBody(bodyObject)
                                    .setBodyMessage(body);
                            listener.onMessage(tMessageContent);
                            return null;
                        });
            } catch (Exception e) {
                log.warn("messageListener:{} init error", listener.getClass().getName(), e);
            }
        }
    }
}
