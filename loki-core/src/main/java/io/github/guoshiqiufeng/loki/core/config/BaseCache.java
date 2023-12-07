package io.github.guoshiqiufeng.loki.core.config;

import io.github.guoshiqiufeng.loki.core.exception.LokiException;
import io.github.guoshiqiufeng.loki.core.handler.HandlerHolder;
import io.github.guoshiqiufeng.loki.core.mapper.BaseMapper;
import io.github.guoshiqiufeng.loki.core.mapper.BaseMapperImpl;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存类
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/21 14:07
 */
@UtilityClass
public class BaseCache {

    /**
     * 用于存放BaseMapper的所有实例
     */
    private final Map<Class<?>, BaseMapper<?>> BASE_MAPPER_INSTANCE_MAP = new ConcurrentHashMap<>();


    private final Map<Class<?>, Class<?>> BASE_ENTITY_INSTANCE_MAP = new ConcurrentHashMap<>();

    /**
     * 初始化缓存
     *
     * @param mapperInterface mapper接口
     * @param entityClass     实体类
     * @param lokiProperties  loki配置
     * @param handlerHolder   具体事件处理持有者
     */
    public void initCache(Class<?> mapperInterface, Class<?> entityClass, LokiProperties lokiProperties,
                                 HandlerHolder handlerHolder) {
        // 初始化baseMapper的所有实现类实例
        BaseMapperImpl<?> baseMapper = new BaseMapperImpl<>();
        baseMapper.setHandlerHolder(handlerHolder);
        baseMapper.setEntityClass(entityClass);
        BASE_MAPPER_INSTANCE_MAP.put(mapperInterface, baseMapper);
        BASE_ENTITY_INSTANCE_MAP.put(mapperInterface, entityClass);
    }

    public Class<?> getEntityClass(Class<?> mapperInterface) {
        return BASE_ENTITY_INSTANCE_MAP.get(mapperInterface);
    }

    /**
     * 获取缓存中对应的BaseMapper
     *
     * @param mapperInterface mapper接口
     * @return 实现类
     */
    public BaseMapper<?> getBaseMapperInstance(Class<?> mapperInterface) {
        return Optional.ofNullable(BASE_MAPPER_INSTANCE_MAP.get(mapperInterface))
                .orElseThrow(() -> new LokiException("No interface instance exists %s", mapperInterface));
    }
}
