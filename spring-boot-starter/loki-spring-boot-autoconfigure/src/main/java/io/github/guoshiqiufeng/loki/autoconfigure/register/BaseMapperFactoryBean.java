package io.github.guoshiqiufeng.loki.autoconfigure.register;


import io.github.guoshiqiufeng.loki.core.config.BaseCache;
import io.github.guoshiqiufeng.loki.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.proxy.MapperProxy;
import io.github.guoshiqiufeng.loki.core.toolkit.TypeUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

/**
 * BaseMapper工厂类
 *
 * @param <T> 泛型
 * @author yanghq
 * @version 1.0
 * @since 2023/11/15 16:08
 */
@Slf4j
public class BaseMapperFactoryBean<T> implements FactoryBean<T> {

    /**
     * mapper 接口的类对象
     *
     * @param mapperInterface mapper 接口的类对象
     */
    @Setter
    private Class<T> mapperInterface;

    /**
     * Loki 框架的配置属性
     */
    @Autowired
    private LokiProperties lokiProperties;

    /**
     * Handler 持有者，用于管理消息处理器
     */
    @Autowired
    private HandlerHolder handlerHolder;

    /**
     * 创建并返回代理对象的方法。
     *
     * @return 代理对象
     * @throws Exception 创建代理对象时可能抛出的异常
     */
    @Override
    public T getObject() throws Exception {
        // 创建 MapperProxy 实例
        MapperProxy<T> esMapperProxy = new MapperProxy<>(mapperInterface);

        // 获取实体类
        Class<?> entityClass = TypeUtils.getInterfaceGenericType(mapperInterface, 0);

        // 初始化缓存和处理器
        BaseCache.initCache(mapperInterface, entityClass, lokiProperties, handlerHolder);

        // 创建代理
        @SuppressWarnings("unchecked")
        T t = (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, esMapperProxy);

        return t;
    }

    /**
     * 返回该工厂Bean创建的对象的类型。
     *
     * @return 该工厂Bean创建的对象的类型
     */
    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }


    /**
     * 构造方法
     */
    public BaseMapperFactoryBean() {
    }
}
