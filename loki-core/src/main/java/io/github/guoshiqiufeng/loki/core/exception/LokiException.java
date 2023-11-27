package io.github.guoshiqiufeng.loki.core.exception;

import lombok.AllArgsConstructor;

/**
 * 异常类
 * @author yanghq
 * @version 1.0
 * @since 2023/11/10 14:17
 */
@AllArgsConstructor
public class LokiException extends RuntimeException {

    private static final long serialVersionUID = 3441678874129097621L;

    /**
     * 构造函数
     * @param message 消息
     * @param args 参数
     */
    public LokiException(String message, Object... args) {
        super(String.format(message, args));
    }
}
