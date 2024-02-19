/*
 * Copyright (c) 2023-2024, fubluesky (fubluesky@foxmail.com)
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

import cn.hutool.core.thread.ExecutorBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务线程池配置
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/2/15 14:18
 */
public class ThreadPoolUtils {

    /**
     * 获取单线程池
     *
     * @return ExecutorService  线程池
     */
    public static ExecutorService getSingleThreadPool() {
        return getThreadPool(1, 1);
    }

    /**
     * 获取指定核心线程池。最大线程池为核心线程数2倍
     *
     * @param corePoolSize 核心线程数
     * @return ExecutorService  线程池
     */
    public static ExecutorService getThreadPool(int corePoolSize) {
        return getThreadPool(corePoolSize, corePoolSize * 2);
    }

    /**
     * 获取指定线程池
     *
     * @param corePoolSize 核心线程数
     * @param maxPoolSize  最大线程数
     * @return ExecutorService  线程池
     */
    public static ExecutorService getThreadPool(int corePoolSize, int maxPoolSize) {
        return ExecutorBuilder.create()
                .setCorePoolSize(corePoolSize)
                .setMaxPoolSize(maxPoolSize)
                .setWorkQueue(new LinkedBlockingQueue<>(1024))
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .setAllowCoreThreadTimeOut(true)
                .setKeepAliveTime(10, TimeUnit.SECONDS)
                .build();
    }

}
