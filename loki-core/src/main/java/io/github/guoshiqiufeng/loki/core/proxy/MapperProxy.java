package io.github.guoshiqiufeng.loki.core.proxy;

import io.github.guoshiqiufeng.loki.core.config.BaseCache;
import io.github.guoshiqiufeng.loki.core.mapper.BaseMapper;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理类
 *
 * @param <T> 代理类泛型
 * @author yanghq
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {
    private static final long serialVersionUID = -523967211560155711L;

    /**
     * The class object representing the mapper interface associated with this proxy.
     */
    private final Class<T> mapperInterface;

    /**
     * Constructs a new {@code MapperProxy} instance for the specified mapper interface.
     *
     * @param mapperInterface The class object representing the mapper interface.
     */
    public MapperProxy(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    /**
     * Invokes the specified method on a dynamically obtained instance of {@code BaseMapper},
     * using the provided arguments.
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to the invoked interface method
     * @param args   an array of objects containing the values of the invoked method parameters
     * @return the result of the method invocation
     * @throws Throwable if the method invocation encounters an exception
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Retrieve the dynamically obtained instance of BaseMapper associated with the mapperInterface
        BaseMapper<?> baseMapperInstance = BaseCache.getBaseMapperInstance(mapperInterface);

        // Invoke the method on the BaseMapper instance with the provided arguments
        return method.invoke(baseMapperInstance, args);
    }

}