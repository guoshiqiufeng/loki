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
package io.github.guoshiqiufeng.loki.core.mapper;

import java.util.concurrent.CompletableFuture;

/**
 * 基础mapper
 * @author yanghq
 * @version 1.0
 * @since 2023/11/10 14:51
 * @param <T> 消息类型
 */
public interface BaseMapper<T> extends Mapper<T> {

    /**
     * 发送消息
     *
     * @param entity 消息实体
     * @return messageId 消息id
     */
    String send(T entity);

    /**
     * 发送异步消息
     *
     * @param entity 消息实体
     * @return messageId 消息id
     */
    CompletableFuture<String> sendAsync(T entity);


}
