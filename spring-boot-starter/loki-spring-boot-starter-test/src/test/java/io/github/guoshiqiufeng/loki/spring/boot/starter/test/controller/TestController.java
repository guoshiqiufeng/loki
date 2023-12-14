/*
 * Copyright (c) 2023-2023, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.loki.spring.boot.starter.test.controller;

import com.alibaba.fastjson2.JSON;
import io.github.guoshiqiufeng.loki.spring.boot.starter.test.TestEntity;
import io.github.guoshiqiufeng.loki.spring.boot.starter.test.mapper.TestMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

    @GetMapping("customSend")
    public String customSend() throws ExecutionException, InterruptedException {
        TestEntity entity = new TestEntity();
        entity.setId("9521");
        entity.setMessage("test");
        CompletableFuture<String> stringCompletableFuture = testMapper.customAsyncSend(entity);
        String messageId = stringCompletableFuture.get();
        log.debug("send messageId:{}", messageId);
        //String messageId = testMapper.customSend(entity);
        //log.debug("send messageId:{}", messageId);
        return "success";
    }
}
