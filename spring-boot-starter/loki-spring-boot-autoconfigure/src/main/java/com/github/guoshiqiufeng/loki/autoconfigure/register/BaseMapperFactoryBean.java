package com.github.guoshiqiufeng.loki.autoconfigure.register;


import com.github.guoshiqiufeng.loki.core.config.BaseCache;
import com.github.guoshiqiufeng.loki.core.config.LokiProperties;
import com.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import com.github.guoshiqiufeng.loki.core.proxy.MapperProxy;
import com.github.guoshiqiufeng.loki.core.toolkit.TypeUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/15 16:08
 */
@Slf4j
@Data
public class BaseMapperFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    @Autowired
    private LokiProperties lokiProperties;

    @Autowired
    private HandlerHolder handlerHolder;

    @Override
    public T getObject() throws Exception {
        MapperProxy<T> esMapperProxy = new MapperProxy<>(mapperInterface);

        // 获取实体类
        Class<?> entityClass = TypeUtils.getInterfaceGenericType(mapperInterface, 0);

        BaseCache.initCache(mapperInterface, entityClass, lokiProperties, handlerHolder);

        // 创建监听
//        handlerHolder.route(MqType.ROCKET_MQ.getCode()).pushMessageListener(
//                "*"
//                , "loki");

        // 创建代理
        T t = (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, esMapperProxy);

        return t;
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }

}
