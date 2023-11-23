package com.github.guoshiqiufeng.loki.core.handler;

import com.github.guoshiqiufeng.loki.core.config.LokiProperties;

import javax.annotation.PostConstruct;

/**
 * 消息处理事件抽象类
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/21 13:20
 */
public abstract class AbstractHandler implements Handler {


    protected LokiProperties properties;


    protected HandlerHolder handlerHolder;

    /**
     * 标识渠道的Code
     * 子类初始化的时候指定
     */
    protected Integer type;

    /**
     * 初始化
     */
    @PostConstruct
    protected void init() {
        // 将handler添加到handlerHolder中
        handlerHolder.putHandler(type, this);
    }

    public AbstractHandler(LokiProperties properties, HandlerHolder handlerHolder) {
        this.properties = properties;
        this.handlerHolder = handlerHolder;
    }
}
