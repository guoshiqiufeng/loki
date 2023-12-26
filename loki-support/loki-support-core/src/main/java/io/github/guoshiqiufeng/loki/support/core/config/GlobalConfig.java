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

import io.github.guoshiqiufeng.loki.enums.MqType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 全局配置
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/10 14:14
 */
@Data
@Accessors(chain = true)
public class GlobalConfig implements Serializable {

    /**
     * 是否开启logo
     */
    private boolean banner;

    /**
     * mq配置
     */
    private MqConfig mqConfig;

    /**
     * mq配置类
     */
    @Data
    @Accessors(chain = true)
    public static class MqConfig {

        /**
         * 类型
         */
        private MqType mqType;

        /**
         * 地址
         */
        private String address;

        /**
         * 开启授权
         */
        private Boolean auth;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 链接超时时间，单位秒，默认180s
         */
        private int connectTimeout;

        /**
         * 发送最大尝试次数, 默认3次
         */
        private int maxAttempts;
    }
}
