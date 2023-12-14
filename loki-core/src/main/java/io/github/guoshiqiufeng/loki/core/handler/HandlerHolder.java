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
package io.github.guoshiqiufeng.loki.core.handler;

import io.github.guoshiqiufeng.loki.enums.MqType;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler持有者，用于存储和获取Handler。
 * @author yanghq
 * @version 1.0
 * @since 2023/11/16 14:10
 */
public class HandlerHolder {

    private final Map<Integer, Handler> handlers = new HashMap<Integer, Handler>(128);

    /**
     * 存储Handler
     * @param type 类型
     * @param handler 事件
     */
    public void putHandler(Integer type, Handler handler) {
        handlers.put(type, handler);
    }

    /**
     * 获取Handler
     * @param type 类型
     * @return 事件Handler
     */
    public Handler route(Integer type) {
        return handlers.get(type);
    }

    /**
     * 获取Handler
     * @param mqType 类型
     * @return 事件Handler
     */
    public Handler route(MqType mqType) {
        return handlers.get(mqType.getCode());
    }

    /**
     * 构造方法
     */
    public HandlerHolder() {}
}
