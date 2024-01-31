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
package io.github.guoshiqiufeng.loki.support.core.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yanghq
 * @version 1.0
 * @since 2021-08-14 11:41
 */
@UtilityClass
public class ListUtils {

    /**
     * 判断是否不为空
     *
     * @param list list对象
     * @return boolean 非空返回true,空则返回false
     */
    public boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * 判断是否为空
     *
     * @param list list对象
     * @return boolean 空返回true,非空则返回false
     */
    public boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 将集合转换为List
     *
     * @param source 集合
     * @param mapper 转换函数
     * @param <T>    源类型
     * @param <U>    目标类型
     * @return List
     */
    public static <T, U> List<U> toList(Collection<T> source, Function<T, U> mapper) {
        if (CollectionUtils.isEmpty(source)) {
            return new ArrayList<>();
        }
        return source.stream().map(mapper).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 将集合转换为List
     *
     * @param source    集合
     * @param mapper    转换函数
     * @param predicate 过滤函数
     * @param <T>       源类型
     * @param <U>       目标类型
     * @return Set
     */
    public static <T, U> List<U> toList(Collection<T> source, Function<T, U> mapper, Predicate<U> predicate) {
        if (CollectionUtils.isEmpty(source)) {
            return new ArrayList<>();
        }
        return source.stream().map(mapper).filter(Objects::nonNull).filter(predicate).collect(Collectors.toList());
    }

    /**
     * 对象转换
     *
     * @param obj   list对象
     * @param clazz 转换的类
     * @param <T>   clazz的类
     * @return list 转换后的list
     */
    public <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

}
