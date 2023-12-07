package io.github.guoshiqiufeng.loki.autoconfigure.register;

import com.alibaba.fastjson2.JSON;
import io.github.guoshiqiufeng.loki.MessageContent;
import io.github.guoshiqiufeng.loki.MessageListener;
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
    private final List<MessageListener<T>> messageListenerList;

    /**
     * 构造函数
     *
     * @param handlerHolder       处理器持有者
     * @param lokiProperties      loki配置
     * @param messageListenerList 消息监听器列表
     */
    public LokiRegistrar(HandlerHolder handlerHolder, LokiProperties lokiProperties, List<MessageListener<T>> messageListenerList) {
        this.handlerHolder = handlerHolder;
        this.lokiProperties = lokiProperties;
        this.messageListenerList = messageListenerList;
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
        if (!CollectionUtils.isEmpty(messageListenerList)) {
            initListener(messageListenerList);
        }
    }


    /**
     * 创建监听
     *
     * @param messageListenerList 消息监听器列表
     */
    private void initListener(List<MessageListener<T>> messageListenerList) {
        for (MessageListener<T> messageListener : messageListenerList) {
            @SuppressWarnings("unchecked")
            Class<T> interfaceGenericType = (Class<T>) TypeUtils.getInterfaceGenericType(messageListener.getClass(), 0);
            try {
                MessageInfo messageInfo = EntityInfoHelper.getMessageInfo(interfaceGenericType);

                handlerHolder.route(MqType.ROCKET_MQ.getCode()).pushMessageListener(messageInfo.getConsumerGroup(),
                        messageInfo.getTopic(), messageInfo.getTag(), messageInfo.getConsumptionThreadCount(),
                        messageInfo.getMaxCacheMessageCount(), messageContent -> {
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
                                    .setBody(bodyObject);
                            messageListener.onMessage(tMessageContent);
                            return null;
                        });
            } catch (Exception e) {
                log.warn("messageListener:{} init error", messageListener.getClass().getName(), e);
            }
        }
    }
}
