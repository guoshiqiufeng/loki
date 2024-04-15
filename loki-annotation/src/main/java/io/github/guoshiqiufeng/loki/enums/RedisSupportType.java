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
package io.github.guoshiqiufeng.loki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/3/26 16:39
 */
@Getter
@AllArgsConstructor
public enum RedisSupportType {

    /**
     * 默认使用jedis
     */
    DEFAULT("default", "默认实现"),

    /**
     * 使用spring data
     */
    SPRING_DATA("spring-data", "Spring Data 实现"),

    ;


    /**
     * 值
     */
    private final String value;

    /**
     * 描述
     */
    private final String desc;
}
