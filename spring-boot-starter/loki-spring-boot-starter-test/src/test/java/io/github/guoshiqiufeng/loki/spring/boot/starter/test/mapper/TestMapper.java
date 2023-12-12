package io.github.guoshiqiufeng.loki.spring.boot.starter.test.mapper;

import io.github.guoshiqiufeng.loki.annotation.SendMessage;
import io.github.guoshiqiufeng.loki.core.mapper.BaseMapper;
import io.github.guoshiqiufeng.loki.spring.boot.starter.test.TestEntity;

import java.util.concurrent.CompletableFuture;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/15 20:24
 */
public interface TestMapper extends BaseMapper<TestEntity> {


    @SendMessage(message = "#message")
    String customSend(String message);

    @SendMessage(topic = "topic2", message = "#message")
    String customSend2(String message);

    @SendMessage(topic = "", async = true, message = "#message")
    String customAsyncSend(String message);

    @SendMessage(topic = "", tag = "custom", message = "#entity.message", messageKey = "#entity.id")
    void customSend(TestEntity entity);

    @SendMessage(topic = "", tag = "custom", async = true, message = "#entity.message", messageKey = "#entity.id")
    CompletableFuture<String> customAsyncSend(TestEntity entity);


}
