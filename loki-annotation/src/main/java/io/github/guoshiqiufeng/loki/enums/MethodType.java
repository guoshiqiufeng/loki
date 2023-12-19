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
package io.github.guoshiqiufeng.loki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 方法类型
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/21 13:48
 */
@Getter
@AllArgsConstructor
public enum MethodType {

    /**
     * 发送消息
     */
    SEND("send", "发送"),

    /**
     * 异步发送消息
     */
    SEND_ASYNC("sendAsync", "异步发送"),


    ;


    /**
     * 编码
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据编码获取对应的 MethodType 枚举值
     *
     * @param code 编码
     * @return 对应的 MethodType 枚举值，如果不存在则返回 null
     */
    public static MethodType getByCode(String code) {
        for (MethodType methodType : MethodType.values()) {
            if (methodType.code.equals(code)) {
                return methodType;
            }
        }
        return null; // 如果没有找到匹配的枚举值，则返回 null
    }
}
