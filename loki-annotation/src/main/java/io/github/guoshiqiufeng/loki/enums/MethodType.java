package io.github.guoshiqiufeng.loki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 方法类型
 * @author yanghq
 * @version 1.0
 * @since 2023/11/21 13:48
 */
@Getter
@AllArgsConstructor
public enum MethodType {

    /**
     * 发送消息
     */
    SEND("send", "发送"),

    /**
     * 异步发送消息
     */
    SEND_ASYNC("sendAsync", "异步发送"),


    ;


    /**
     * 编码
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据编码获取对应的 MethodType 枚举值
     *
     * @param code 编码
     * @return 对应的 MethodType 枚举值，如果不存在则返回 null
     */
    public static MethodType getByCode(String code) {
        for (MethodType methodType : MethodType.values()) {
            if (methodType.code.equals(code)) {
                return methodType;
            }
        }
        return null; // 如果没有找到匹配的枚举值，则返回 null
    }
}
