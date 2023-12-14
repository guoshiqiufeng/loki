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
 * 消息监听器注解
 * @author yanghq
 * @version 1.0
 * @since 2023/12/12 15:51
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface MessageListener {

    /**
     * 订阅topic
     * @return topic
     */
    String topic();

    /**
     * 订阅topic正则匹配
     * @return topic正则
     */
   // String topicPattern() default "";

    /**
     * 过滤标签
     * @return 标签
     */
    String tag() default "";

    /**
     * 消费者组
     *
     * @return 消费者组
     */
    String consumerGroup() default "defaultConsumerGroup";

    /**
     * 消费线数
     *
     * @return 发送重试次数
     */
    int consumptionThreadCount() default 20;

    /**
     * 最大缓存信息数
     * @return 最大缓存信息数
     */
    int maxCacheMessageCount() default 1024;
}
