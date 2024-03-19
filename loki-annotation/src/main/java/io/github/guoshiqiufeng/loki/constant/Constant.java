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
package io.github.guoshiqiufeng.loki.constant;

/**
 * 常量
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/14 14:09
 */
public interface Constant {

    /**
     * kafka 标签名
     */
    String KAFKA_TAG = "kafka_tag_id";

    /**
     * redis key 前缀
     */
    String REDIS_KEY_PREFIX = "loki:message:";

    /**
     * redis延时发送列表
     */
    String REDIS_DELIVERY_KEY = "loki:delivery";
}
