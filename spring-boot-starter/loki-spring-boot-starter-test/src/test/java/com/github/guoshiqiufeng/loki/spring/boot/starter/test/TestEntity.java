package com.github.guoshiqiufeng.loki.spring.boot.starter.test;

import com.github.guoshiqiufeng.loki.annotation.MessageKey;
import com.github.guoshiqiufeng.loki.annotation.MessageName;
import lombok.Data;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/15 20:22
 */
@Data
@MessageName(topic = "loki", tag = "create")
public class TestEntity {

    @MessageKey
    private String id;

    private String message;
}
