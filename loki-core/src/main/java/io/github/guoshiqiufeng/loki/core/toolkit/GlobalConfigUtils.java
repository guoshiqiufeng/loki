package io.github.guoshiqiufeng.loki.core.toolkit;

import io.github.guoshiqiufeng.loki.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.enums.MqType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/16 09:55
 */
public class GlobalConfigUtils {


    /**
     * 缓存全局信息
     */
    private static final Map<String, GlobalConfig> GLOBAL_CONFIG = new ConcurrentHashMap<>();

    /**
     * 获取默认 GlobalConfig
     */
    public static GlobalConfig defaults() {
        return new GlobalConfig()
                .setBanner(Boolean.TRUE)
                .setMqConfig(
                        new GlobalConfig.MqConfig()
                                .setMqType(MqType.ROCKET_MQ)
                                .setAddress("127.0.0.1:8081")
                                .setAuth(Boolean.FALSE)
                                .setConnectTimeout(180)
                                .setMaxAttempts(3)
                );
    }
}
