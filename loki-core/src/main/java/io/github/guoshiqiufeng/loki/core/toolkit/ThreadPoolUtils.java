package io.github.guoshiqiufeng.loki.core.toolkit;

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
     * 单线程池
     */
    public static ExecutorService getSingleThreadPool() {
        return getThreadPool(1, 1);
    }

    public static ExecutorService getThreadPool(int corePoolSize) {
        return getThreadPool(corePoolSize, corePoolSize * 2);
    }

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
