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

/**
 * jedis连接池配置
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/12/25 14:25
 */
@Data
public class JedisPoolProperties {
    /**
     * 最大连接数
     */
    private int maxTotal = 200;
    /**
     * 最大空闲连接数
     */
    private int maxIdle = 100;
    /**
     * 每次释放连接的最大数目
     */
    private int numTestsPerEvictionRun = 1024;
    /**
     * 释放连接的扫描间隔（毫秒）
     */
    private int timeBetweenEvictionRunsMillis = 30000;
    /**
     * 连接最小空闲时间
     */
    private int minEvictableIdleTimeMillis = 1800000;
    /**
     * 连接空闲多久后释放,
     * 当空闲时间>该值 且 空闲连接>最大空闲连接数 时直接释放
     */
    private int softMinEvictableIdleTimeMillis = 10000;
    /**
     * 获取连接时的最大等待毫秒数,小于零:阻塞不确定的时间,默认-1
     */
    private int maxWaitMillis = 3000;
    /**
     * 在获取连接的时候检查有效性, 默认false
     */
    private Boolean testOnBorrow = true;
    /**
     * 在空闲时检查有效性, 默认false
     */
    private Boolean testWhileIdle = true;
    /**
     * 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
     */
    private Boolean blockWhenExhausted = false;
}
