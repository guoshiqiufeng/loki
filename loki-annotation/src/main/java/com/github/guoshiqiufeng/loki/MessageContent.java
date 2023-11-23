package com.github.guoshiqiufeng.loki;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/23 10:21
 */
@Data
@Accessors(chain = true)
public class MessageContent<T> implements Serializable {
    private static final long serialVersionUID = 4865190049485051725L;


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
     * 内容
     */
    private T body;
}
