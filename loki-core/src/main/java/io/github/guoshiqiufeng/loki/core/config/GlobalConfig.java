package io.github.guoshiqiufeng.loki.core.config;

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
    private boolean banner = true;

    /**
     * mq配置
     */
    private MqConfig mqConfig;


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
        private Boolean auth = false;

        /**
         * 用户名
         */
        private String username = "loki";

        /**
         * 密码
         */
        private String password = "loki";

        /**
         * 链接超时时间，单位秒，默认180s
         */
        private int connectTimeout = 180;
    }
}
