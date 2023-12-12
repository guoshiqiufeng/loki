package io.github.guoshiqiufeng.loki.core.proxy;

import com.alibaba.fastjson2.JSON;
import io.github.guoshiqiufeng.loki.annotation.SendMessage;
import io.github.guoshiqiufeng.loki.core.config.BaseCache;
import io.github.guoshiqiufeng.loki.core.entity.MessageInfo;
import io.github.guoshiqiufeng.loki.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.mapper.BaseMapper;
import io.github.guoshiqiufeng.loki.core.mapper.BaseMapperImpl;
import io.github.guoshiqiufeng.loki.core.toolkit.EntityInfoHelper;
import io.github.guoshiqiufeng.loki.enums.MethodType;
import io.github.guoshiqiufeng.loki.enums.MqType;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.beans.ParameterDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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

        // default method support
        if(MethodType.getByCode(method.getName()) != null) {
            return method.invoke(baseMapperInstance, args);
        }

        // annotation
        SendMessage sendMessageAnnotation = method.getAnnotation(SendMessage.class);
        if(sendMessageAnnotation != null) {
            BaseMapperImpl<?> baseMapper= (BaseMapperImpl<?>) baseMapperInstance;
            return baseMapper.sendByAnnotation(sendMessageAnnotation, method, args);
        }
        throw new LokiException("No support method %s, in %s", method.getName(), mapperInterface);
    }

}