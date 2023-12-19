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
package io.github.guoshiqiufeng.loki.annotation;

import java.lang.annotation.*;

/**
 * 消息发送注解
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/6 09:53
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SendMessage {

    /**
     * 主题
     * 默认从BaseMapper关联的实体上获取topic
     *
     * @return 主题
     */
    String topic() default "";

    /**
     * 是否异步发送
     *
     * @return 是否异步发送
     */
    boolean async() default false;

    /**
     * 发送消息
     *
     * @return 发送消息
     */
    String message() default "";

    /**
     * 消息key
     *
     * @return 消息key
     */
    String messageKey() default "";

    /**
     * 标签
     *
     * @return 标签
     */
    String tag() default "";

    /**
     * 生产者
     *
     * @return 生产者
     */
    String producer() default "defaultProducer";

    /**
     * 延时时间
     *
     * @return 延时时间
     */
    long deliveryTimestamp() default 0;

}
