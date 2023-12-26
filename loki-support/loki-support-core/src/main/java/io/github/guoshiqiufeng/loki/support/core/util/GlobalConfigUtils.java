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
package io.github.guoshiqiufeng.loki.support.core.util;

import io.github.guoshiqiufeng.loki.support.core.config.GlobalConfig;
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
