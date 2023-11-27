package io.github.guoshiqiufeng.loki.core.handler;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler持有者，用于存储和获取Handler。
 * @author yanghq
 * @version 1.0
 * @since 2023/11/16 14:10
 */
public class HandlerHolder {

    private final Map<Integer, Handler> handlers = new HashMap<Integer, Handler>(128);

    /**
     * 存储Handler
     * @param type 类型
     * @param handler 事件
     */
    public void putHandler(Integer type, Handler handler) {
        handlers.put(type, handler);
    }

    /**
     * 获取Handler
     * @param type 类型
     * @return 事件Handler
     */
    public Handler route(Integer type) {
        return handlers.get(type);
    }

    /**
     * 构造方法
     */
    public HandlerHolder() {}
}
