package io.github.guoshiqiufeng.loki.core.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 消费配置
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/1/10 16:44
 */
@Data
@Accessors(chain = true)
public class ConsumerConfig implements Serializable {

    /**
     * 消费分组
     */
    private String consumerGroup;

    /**
     * 序号
     */
    private Integer index;

    /**
     * 主题
     */
    private String topic;

    /**
     * 主题正则匹配
     */
    private String topicPattern;

    /**
     * 标签
     */
    private String tag;

    /**
     * 消费线数
     */
    private Integer consumptionThreadCount = 20;

    /**
     * 最大缓存信息数
     */
    private Integer maxCacheMessageCount = 1024;

}
