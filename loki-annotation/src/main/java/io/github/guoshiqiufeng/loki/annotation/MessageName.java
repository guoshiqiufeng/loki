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
     * @return 最大缓存信息数
     */
    int maxCacheMessageCount() default 1024;
}
