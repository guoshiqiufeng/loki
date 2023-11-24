package io.github.guoshiqiufeng.loki.core.config;

import io.github.guoshiqiufeng.loki.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.mapper.BaseMapper;
import io.github.guoshiqiufeng.loki.core.mapper.BaseMapperImpl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存类
 * @author yanghq
 * @version 1.0
 * @since 2023/11/21 14:07
 */
public class BaseCache {

    /**
     * 用于存放BaseMapper的所有实例
     */
    private static final Map<Class<?>, BaseMapper<?>> baseMapperInstanceMap = new ConcurrentHashMap<>();

    /**
     * 初始化缓存
     *
     * @param mapperInterface mapper接口
     * @param entityClass     实体类
     */
    public static void initCache(Class<?> mapperInterface, Class<?> entityClass, LokiProperties lokiProperties,
                                 HandlerHolder handlerHolder) {
        // 初始化baseMapper的所有实现类实例
        BaseMapperImpl<?> baseMapper = new BaseMapperImpl<>();
        baseMapper.setHandlerHolder(handlerHolder);
        baseMapper.setEntityClass(entityClass);
        baseMapperInstanceMap.put(mapperInterface, baseMapper);
    }

    /**
     * 获取缓存中对应的BaseMapper
     *
     * @param mapperInterface mapper接口
     * @return 实现类
     */
    public static BaseMapper<?> getBaseMapperInstance(Class<?> mapperInterface) {
        return Optional.ofNullable(baseMapperInstanceMap.get(mapperInterface))
                .orElseThrow(() -> new LokiException("No interface instance exists ", mapperInterface));
    }
}
