package io.github.guoshiqiufeng.loki.annotation;

import java.lang.annotation.*;

/**
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
