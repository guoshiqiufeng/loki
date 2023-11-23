package com.github.guoshiqiufeng.loki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/21 13:48
 */
@Getter
@AllArgsConstructor
public enum MethodType {

    /**
     * send
     */
    SEND("send", "发送");


    /**
     * 编码
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;
}
