package io.github.guoshiqiufeng.loki.core.toolkit;

import io.github.guoshiqiufeng.loki.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.enums.MqType;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局配置工具类
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/16 09:55
 */
@UtilityClass
public class GlobalConfigUtils {


    /**
     * 缓存全局信息
     */
    private final Map<String, GlobalConfig> GLOBAL_CONFIG = new ConcurrentHashMap<>();

    /**
     * 获取默认 GlobalConfig
     *
     * @return GlobalConfig默认配置
     */
    public GlobalConfig defaults() {
        return new GlobalConfig()
                .setBanner(Boolean.TRUE)
                .setMqConfig(
                        new GlobalConfig.MqConfig()
                                .setMqType(MqType.ROCKET_MQ)
                                .setAddress("127.0.0.1:8081")
                                .setAuth(Boolean.FALSE)
                                .setUsername("loki")
                                .setPassword("loki")
                                .setConnectTimeout(180)
                                .setMaxAttempts(3)
                );
    }
}
