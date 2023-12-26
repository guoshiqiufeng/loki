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
