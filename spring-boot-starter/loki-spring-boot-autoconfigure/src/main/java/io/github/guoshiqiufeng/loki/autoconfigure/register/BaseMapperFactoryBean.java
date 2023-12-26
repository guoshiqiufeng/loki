/*
 * Copyright (c) 2023-2023, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.loki.autoconfigure.register;


import io.github.guoshiqiufeng.loki.core.config.BaseCache;
import io.github.guoshiqiufeng.loki.support.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.proxy.MapperProxy;
import io.github.guoshiqiufeng.loki.core.toolkit.TypeUtils;
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
