package com.github.guoshiqiufeng.loki.test.rocketmq;

import com.github.guoshiqiufeng.loki.annotation.MessageKey;
import com.github.guoshiqiufeng.loki.annotation.MessageName;
import lombok.Data;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/10 14:47
 */
@Data
@MessageName(topic = "test")
public class Entity {

    @MessageKey
    private String id;
}
