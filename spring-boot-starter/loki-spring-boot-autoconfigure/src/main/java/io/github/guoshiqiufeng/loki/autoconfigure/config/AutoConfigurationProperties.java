package io.github.guoshiqiufeng.loki.autoconfigure.config;

import io.github.guoshiqiufeng.loki.core.config.GlobalConfig;
import io.github.guoshiqiufeng.loki.core.config.LokiProperties;
import io.github.guoshiqiufeng.loki.core.toolkit.GlobalConfigUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
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
}
