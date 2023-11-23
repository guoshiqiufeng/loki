package com.github.guoshiqiufeng.loki.spring.boot.starter.test;

import com.github.guoshiqiufeng.loki.MessageContent;
import com.github.guoshiqiufeng.loki.annotation.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/22 17:15
 */
@Slf4j
@Component
public class TestListener implements MessageListener<TestEntity> {
    @Override
    public void onMessage(MessageContent<TestEntity> entity) {
        log.debug("entity:{}", entity);
    }
}
