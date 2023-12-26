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
package io.github.guoshiqiufeng.loki.support.core.config;

import io.github.guoshiqiufeng.loki.support.core.util.GlobalConfigUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * loki配置
 * @author yanghq
 * @version 1.0
 * @since 2023/12/26 15:49
 */
@Configuration
public class LokiPropertiesAutoConfiguration {

    /**
     * 配置文件
     *
     * @return 配置文件
     */
    @Bean
    @ConfigurationProperties(prefix = "loki")
//    @ConditionalOnMissingBean(LokiProperties.class)
    public LokiProperties lokiProperties() {
        LokiProperties autoConfigurationProperties = new LokiProperties();
        autoConfigurationProperties.setGlobalConfig(GlobalConfigUtils.defaults());
        return autoConfigurationProperties;
    }
}
