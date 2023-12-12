package io.github.guoshiqiufeng.loki.spring.boot.starter.test;

import io.github.guoshiqiufeng.loki.Listener;
import io.github.guoshiqiufeng.loki.MessageContent;
import io.github.guoshiqiufeng.loki.annotation.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/12/12 16:22
 */
@Slf4j
@MessageListener(topic = "loki")
@Component
public class TestMessageListener implements Listener<String> {

    @Override
    public void onMessage(MessageContent<String> messageContent) {
        log.debug("messageContent:{}", messageContent);
    }
}
