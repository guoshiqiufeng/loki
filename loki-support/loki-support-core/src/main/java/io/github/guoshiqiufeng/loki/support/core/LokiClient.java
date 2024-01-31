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
package io.github.guoshiqiufeng.loki.support.core;

import java.util.concurrent.CompletableFuture;

/**
 * client接口，用于统一操作
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/1/20 10:07
 */
public interface LokiClient {

    /**
     * 发送消息
     *
     * @param record 发送信息
     * @return 发送消息结果
     */
    default ProducerResult send(ProducerRecord record) {
        return send(null, record);
    }

    /**
     * 发送消息
     *
     * @param groupName 组名称
     * @param record    发送信息
     * @return 发送消息结果
     */
    ProducerResult send(String groupName, ProducerRecord record);

    /**
     * 异步发送消息
     *
     * @param record 发送信息
     * @return 发送消息结果
     */
    default CompletableFuture<ProducerResult> sendAsync(ProducerRecord record) {
        return sendAsync(null, record);
    }

    /**
     * 异步发送消息
     *
     * @param groupName 组名称
     * @param record    发送信息
     * @return 发送消息结果
     */
    CompletableFuture<ProducerResult> sendAsync(String groupName, ProducerRecord record);
}
