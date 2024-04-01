package io.github.guoshiqiufeng.loki.support.core.config;

import io.github.guoshiqiufeng.loki.enums.RedisSupportType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/3/26 16:37
 */
@Data
@Accessors(chain = true)
public class RedisConfig implements Serializable {

    private RedisSupportType supportType;

}
