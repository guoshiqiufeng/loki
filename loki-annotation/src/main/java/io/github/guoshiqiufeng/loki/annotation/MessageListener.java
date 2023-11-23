package io.github.guoshiqiufeng.loki.annotation;

import io.github.guoshiqiufeng.loki.MessageContent;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/22 17:12
 */
public interface MessageListener<T> {

    void onMessage(MessageContent<T> messageContent);

}
