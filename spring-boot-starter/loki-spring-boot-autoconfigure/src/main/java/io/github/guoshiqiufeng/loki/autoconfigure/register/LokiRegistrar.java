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
package io.github.guoshiqiufeng.loki.autoconfigure.register;

import com.alibaba.fastjson2.JSON;
import io.github.guoshiqiufeng.loki.Listener;
import io.github.guoshiqiufeng.loki.MessageContent;
import io.github.guoshiqiufeng.loki.annotation.MessageListener;
import io.github.guoshiqiufeng.loki.support.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
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

    /**
     * 具体事件处理持有者
     */
    private final HandlerHolder handlerHolder;

    /**
     * loki配置
     */
    private final LokiProperties lokiProperties;

    /**
     * 消息监听器列表
     */
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
    @SuppressWarnings("unchecked")
    private void initListener(List<Listener<T>> listenerList) {
        for (int i = 0; i < listenerList.size(); i++) {
            Listener<T> listener = listenerList.get(i);
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

                handlerHolder.route(getMqType()).pushMessageListener(consumerGroup,
                        i, topic, tag, consumptionThreadCount,
                        maxCacheMessageCount, messageContent -> {
                            // log.debug("messageContent:{}", messageContent)
                            String body = messageContent.getBody();
                            // TODO 序列化
                            T bodyObject = null;
                            if (body != null && body.trim().startsWith("{")) {
                                bodyObject = JSON.parseObject(body, interfaceGenericType);
                            } else if (String.class.getName().equals(interfaceGenericType.getName())) {
                                bodyObject = (T) body;
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

    /**
     * 获取mq类型
     *
     * @return mq类型
     */
    private MqType getMqType() {
        return lokiProperties.getGlobalConfig().getMqConfig().getMqType();
    }
}
