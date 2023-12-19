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
 * 消息相关
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/10 13:57
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface MessageName {


    /**
     * 主题
     *
     * @return 主题
     */
    String topic();

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
     *
     * @return 最大缓存信息数
     */
    int maxCacheMessageCount() default 1024;
}
