package io.github.guoshiqiufeng.loki.autoconfigure.register;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/15 15:25
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(BaseMapperComponentRegistrar.class)
public @interface LokiMapperScan {

    /**
     * 扫描路径
     *
     * @return
     */
    String[] value() default {};
}
