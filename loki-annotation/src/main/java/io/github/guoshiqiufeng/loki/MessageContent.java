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
package io.github.guoshiqiufeng.loki;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;

/**
 * 消息内容
 *
 * @param <T> 消息类型
 * @author yanghq
 * @version 1.0
 * @since 2023/11/23 10:21
 */
@Data
@Accessors(chain = true)
public class MessageContent<T> implements Serializable {
    private static final long serialVersionUID = 4865190049485051725L;


    /**
     * topic
     */
    private String topic;

    /**
     * tag
     */
    private String tag;

    /**
     * 生产者
     */
    private String messageId;

    /**
     * 生产者
     */
    private String messageGroup;

    /**
     * keys
     */
    private Collection<String> keys;
    /**
     * 内容
     */
    private T body;

    /**
     * 内容String 格式
     */
    private String bodyMessage;
}
