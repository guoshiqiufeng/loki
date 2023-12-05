package io.github.guoshiqiufeng.loki.spring.boot.starter.test.controller;

import io.github.guoshiqiufeng.loki.spring.boot.starter.test.TestEntity;
import io.github.guoshiqiufeng.loki.spring.boot.starter.test.mapper.TestMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/15 20:41
 */
@Slf4j
@RestController
public class TestController {

    @Resource
    private TestMapper testMapper;

    @GetMapping("send")
    public String send() {
        TestEntity entity = new TestEntity();
        entity.setId("9521");
        entity.setMessage("test");
        String messageId = testMapper.send(entity);
        log.debug("send messageId:{}", messageId);
        return "success";
    }

    @GetMapping("sendAsync")
    public String sendAsync() {
        TestEntity entity = new TestEntity();
        entity.setId("9521");
        entity.setMessage("sendAsync");
        testMapper.sendAsync(entity);
        return "success";
    }

    @GetMapping("sendAsync2")
    public String sendAsync2() {
        TestEntity entity = new TestEntity();
        entity.setId("9521");
        entity.setMessage("sendAsync2");
        CompletableFuture<String> future = testMapper.sendAsync(entity);
        ExecutorService sendCallbackExecutor = Executors.newCachedThreadPool();
        future.whenCompleteAsync((messageId, throwable) -> {
            if (null != throwable) {
                log.error("Failed to send message", throwable);
                // Return early.
                return;
            }
            log.info("Send message successfully, messageId={}", messageId);
        }, sendCallbackExecutor);
        return "success";
    }
}
