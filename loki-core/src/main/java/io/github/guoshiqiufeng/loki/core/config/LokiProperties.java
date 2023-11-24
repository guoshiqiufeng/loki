package io.github.guoshiqiufeng.loki.core.config;

import io.github.guoshiqiufeng.loki.core.toolkit.GlobalConfigUtils;
import lombok.Data;

/**
 * Loki配置类
 * @author yanghq
 * @version 1.0
 * @since 2023/11/16 09:47
 */
@Data
public class LokiProperties {

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 全局配置
     */
    private GlobalConfig globalConfig = GlobalConfigUtils.defaults();


}
