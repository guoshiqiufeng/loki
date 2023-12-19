/*
 * Copyright (c) 2023-2023, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.loki.autoconfigure.register;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 扫描mapper注解
 *
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
     * @return 扫描路径
     */
    String[] value() default {};
}
