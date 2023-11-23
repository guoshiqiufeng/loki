package io.github.guoshiqiufeng.loki.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/22 13:38
 */
@Data
@Accessors(chain = true)
public class MessageInfo {

    /**
     * topic
     */
    private String topic;

    /**
     * tag
     */
    private String tag;

    /**
     * 生产者
     */
    private String producer;

    /**
     * 延时
     */
    private long deliveryTimestamp;

    /**
     * 消费者
     */
    private String consumerGroup;

}
