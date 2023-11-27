package io.github.guoshiqiufeng.loki;

/**
 * 消息监听器
 * @author yanghq
 * @version 1.0
 * @since 2023/11/22 17:12
 * @param <T> 消息类型
 */
public interface MessageListener<T> {

    /**
     * 消息监听
     * @param messageContent 消息内容
     */
    void onMessage(MessageContent<T> messageContent);

}
