package com.github.guoshiqiufeng.loki.annotation;

import com.github.guoshiqiufeng.loki.MessageContent;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/22 17:12
 */
public interface MessageListener<T> {

    void onMessage(MessageContent<T> messageContent);

}
