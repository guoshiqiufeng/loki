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

import lombok.Data;

import java.util.List;

/**
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/1/24 10:51
 */
@Data
public class ProducerRecord {

    /**
     * 主题
     */
    private String topic;

    /**
     * 标签
     */
    private String tag;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 延时时间
     */
    private Long deliveryTimestamp;

    /**
     * key
     */
    private List<String> keys;
}
