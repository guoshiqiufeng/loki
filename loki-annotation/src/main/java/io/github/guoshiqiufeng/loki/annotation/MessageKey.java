package io.github.guoshiqiufeng.loki.annotation;

import java.lang.annotation.*;

/**
 * 消息key
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/11/15 09:36
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface MessageKey {

    /**
     * 字段名（该值可无）
     * @return 字段名
     */
    String value() default "";
}
