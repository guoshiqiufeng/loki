package io.github.guoshiqiufeng.loki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支持的mq类型
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/10 13:27
 */
@Getter
@AllArgsConstructor
public enum MqType {

    /**
     * rocketmq
     */
    ROCKET_MQ(10, "RocketMQ");


    /**
     * 编码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;
}
