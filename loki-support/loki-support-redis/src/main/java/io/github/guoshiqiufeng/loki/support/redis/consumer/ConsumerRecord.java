package io.github.guoshiqiufeng.loki.support.redis.consumer;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 消费记录
 * @author yanghq
 * @version 1.0
 * @since 2023/12/26 10:44
 */
@Data
@Accessors(chain = true)
public class ConsumerRecord implements Serializable {

    /**
     * topic
     */
    private String topic;

    /**
     * message
     */
    private String message;
}
