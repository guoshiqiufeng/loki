package io.github.guoshiqiufeng.loki.support.core.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/31 10:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerRecord {

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
    private String messageId;

    /**
     * 生产者
     */
    private String messageGroup;

    /**
     * keys
     */
    private Collection<String> keys;

    /**
     * 内容String 格式
     */
    private String bodyMessage;
}
