/*
 * Copyright (c) 2023-2024, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.loki.core.handler;

import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.support.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息处理事件抽象类
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/21 13:20
 */
@Slf4j
public abstract class AbstractHandler implements Handler {

    /**
     * loki配置
     */
    protected LokiProperties properties;


    /**
     * 具体事件处理持有者
     */
    protected HandlerHolder handlerHolder;

    /**
     * 标识渠道的Code
     * 子类初始化的时候指定
     */
    protected Integer type;

    /**
     * 初始化
     */
    protected void init() {
        // 将handler添加到handlerHolder中
        handlerHolder.putHandler(type, this);
    }

    /**
     * 构造函数
     *
     * @param properties    loki配置
     * @param handlerHolder 具体事件处理持有者
     */
    public AbstractHandler(LokiProperties properties, HandlerHolder handlerHolder) {
        this.properties = properties;
        this.handlerHolder = handlerHolder;
    }

    /**
     * 校验参数
     */
    protected boolean validateParameters(String topic, String body) {
        if (StringUtils.isEmpty(topic)) {
            if (log.isErrorEnabled()) {
                log.error("{}# send message error: topic is null", this.getClass().getSimpleName());
            }
            return false;
        }
        if (StringUtils.isEmpty(body)) {
            if (log.isErrorEnabled()) {
                log.error("{}# send message error: body is null", this.getClass().getSimpleName());
            }
            return false;
        }
        return true;
    }
}
