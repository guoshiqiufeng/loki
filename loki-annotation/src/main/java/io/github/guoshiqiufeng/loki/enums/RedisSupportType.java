package io.github.guoshiqiufeng.loki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/3/26 16:39
 */
@Getter
@AllArgsConstructor
public enum RedisSupportType {

    /**
     * 默认使用jedis
     */
    DEFAULT("default", "默认实现"),

    /**
     * 使用spring data
     */
    SPRING_DATA("spring-data", "Spring Data 实现"),

    ;


    /**
     * 值
     */
    private final String value;

    /**
     * 描述
     */
    private final String desc;
}
