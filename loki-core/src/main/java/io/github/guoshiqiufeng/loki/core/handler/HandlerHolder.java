package io.github.guoshiqiufeng.loki.core.handler;

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

    public void putHandler(Integer type, Handler handler) {
        handlers.put(type, handler);
    }

    public Handler route(Integer type) {
        return handlers.get(type);
    }

}
