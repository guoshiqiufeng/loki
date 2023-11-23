package com.github.guoshiqiufeng.loki.annotation;

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
     * @return
     */
    String topic();

    /**
     * 标签
     *
     * @return
     */
    String tag() default "";

    /**
     * 生产者
     *
     * @return
     */
    String producer() default "defaultProducer";

    /**
     * 延时时间
     *
     * @return
     */
    long deliveryTimestamp() default 0;

    /**
     * 消费者组
     *
     * @return
     */
    String consumerGroup() default "defaultConsumerGroup";
}
