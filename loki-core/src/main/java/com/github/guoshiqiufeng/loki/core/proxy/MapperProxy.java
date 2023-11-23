package com.github.guoshiqiufeng.loki.core.proxy;

import com.github.guoshiqiufeng.loki.core.config.BaseCache;
import com.github.guoshiqiufeng.loki.core.mapper.BaseMapper;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理类
 *
 * @param <T>
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {
    private static final long serialVersionUID = -523967211560155711L;
    private Class<T> mapperInterface;

    public MapperProxy(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        BaseMapper<?> baseMapperInstance = BaseCache.getBaseMapperInstance(mapperInterface);
        return method.invoke(baseMapperInstance, args);
    }

}