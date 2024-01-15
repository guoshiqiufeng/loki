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
package io.github.guoshiqiufeng.loki.core.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 消费配置
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/1/10 16:44
 */
@Data
@Accessors(chain = true)
public class ConsumerConfig implements Serializable {

    /**
     * 消费分组
     */
    private String consumerGroup;

    /**
     * 序号
     */
    private Integer index;

    /**
     * 主题
     */
    private String topic;

    /**
     * 主题正则匹配
     */
    private String topicPattern;

    /**
     * 标签
     */
    private String tag;

    /**
     * 消费线数
     */
    private Integer consumptionThreadCount = 20;

    /**
     * 最大缓存信息数
     */
    private Integer maxCacheMessageCount = 1024;

}
