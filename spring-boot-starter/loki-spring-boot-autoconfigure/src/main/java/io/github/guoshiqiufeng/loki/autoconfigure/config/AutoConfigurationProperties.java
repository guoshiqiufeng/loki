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
package io.github.guoshiqiufeng.loki.autoconfigure.config;

import io.github.guoshiqiufeng.loki.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.core.toolkit.GlobalConfigUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Loki配置类-springboot
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/24 15:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AutoConfigurationProperties extends LokiProperties {

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 全局配置
     */
    @NestedConfigurationProperty
    private GlobalConfig globalConfig = GlobalConfigUtils.defaults();

    /**
     * 构造函数
     */
    public AutoConfigurationProperties() {
    }
}
