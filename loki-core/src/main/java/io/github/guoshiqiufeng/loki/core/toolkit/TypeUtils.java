/*
 * Copyright (c) 2023-2024, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.loki.core.toolkit;

import lombok.experimental.UtilityClass;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 工具类用于处理类型相关操作。
 *
 * @author yanghq
 */
@UtilityClass
public class TypeUtils {

    /**
     * 获取类实现的接口上的泛型类型。
     *
     * @param clazz 类对象
     * @param index 下标
     * @return 类型的Class对象
     * @throws IllegalArgumentException 如果类型不是Class或ParameterizedType
     */
    public Class<?> getInterfaceGenericType(Class<?> clazz, int index) {
        // 获取类实现的接口
        Type[] genericInterfaces = clazz.getGenericInterfaces();

        // 获取指定下标的接口类型
        ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[index];

        // 获取泛型参数的类型
        Type genericType = parameterizedType.getActualTypeArguments()[index];

        // 检查并返回类型
        return checkType(genericType, index);
    }

    /**
     * 递归检查并返回最终的类型。
     *
     * @param type  要检查的类型
     * @param index 下标
     * @return 类型的Class对象
     * @throws IllegalArgumentException 如果类型不是Class或ParameterizedType
     */
    private Class<?> checkType(Type type, int index) {
        if (type instanceof Class<?>) {
            // 如果是Class类型，直接返回
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            // 如果是ParameterizedType类型，递归获取最终类型
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type actualType = parameterizedType.getActualTypeArguments()[index];
            return checkType(actualType, index);
        } else {
            // 不支持的类型，抛出异常
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType"
                    + ", but <" + type + "> is of type " + className);
        }
    }
}