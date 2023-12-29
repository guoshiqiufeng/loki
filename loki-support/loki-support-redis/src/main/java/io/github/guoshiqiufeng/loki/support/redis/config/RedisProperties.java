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
package io.github.guoshiqiufeng.loki.support.redis.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.util.List;

/**
 * redis配置文件
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 15:50
 */
@Data
@Accessors(chain = true)
public class RedisProperties {

    /**
     * 主机
     */
    private String host = "localhost";

    /**
     * 端口号
     */
    private Integer port = 6379;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据库
     */
    private Integer database = 0;

    /**
     * 链接时间
     */
    private int timeout;

    /**
     *
     */
    private Cluster cluster;

    private Sentinel sentinel;

    private Jedis jedis = new Jedis();

    @Data
    public static class Cluster {
        /**
         * 节点地址
         */
        private List<String> nodes;

        /**
         * 最大重定向次数
         */
        private Integer maxRedirects;

    }

    @Data
    public static class Sentinel {
        /**
         * 节点地址
         */
        private List<String> nodes;
        /**
         * 主节点
         */
        private String master;

        /**
         * 用户名
         */
        private String username;
        /**
         * 密码
         */
        private String password;
    }

    @Data
    public static class Jedis {
        private Pool pool = new Pool();
    }

    /**
     * 连接池
     */
    @Data
    public static class Pool {

        /**
         * 指示连接池是否启用。
         */
        private Boolean enabled = true;

        /**
         * 连接池中的最大空闲连接数。
         */
        private int maxIdle = 8;

        /**
         * 连接池中的最小空闲连接数。
         */
        private int minIdle = 0;

        /**
         * 连接池中允许的最大活跃（或总）连接数。
         */
        private int maxActive = 8;

        /**
         * 当连接池耗尽时，池等待连接变为可用的最大时间（以毫秒为单位）。
         * 负值表示无限等待时间。
         */
        private Duration maxWait = Duration.ofMillis(-1);

        /**
         * 空闲对象清理器运行之间的时间间隔。当非空时，清理器处于启用状态。
         */
        private Duration timeBetweenEvictionRuns;

    }
}
