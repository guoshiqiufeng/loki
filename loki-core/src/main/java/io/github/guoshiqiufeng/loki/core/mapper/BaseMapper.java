package io.github.guoshiqiufeng.loki.core.mapper;

import java.util.concurrent.CompletableFuture;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/10 14:51
 */
public interface BaseMapper<T> extends Mapper<T> {

    /**
     * 发送消息
     *
     * @param entity 消息实体
     * @return messageId 消息id
     */
    String send(T entity);

    /**
     * 发送异步消息
     *
     * @param entity 消息实体
     * @return messageId 消息id
     */
    CompletableFuture<String> sendAsync(T entity);


}
