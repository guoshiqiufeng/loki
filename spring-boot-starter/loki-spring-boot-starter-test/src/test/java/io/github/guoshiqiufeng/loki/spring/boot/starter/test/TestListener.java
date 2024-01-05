/*
 * Copyright (c) 2023-2024, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.loki.spring.boot.starter.test;

import io.github.guoshiqiufeng.loki.Listener;
import io.github.guoshiqiufeng.loki.MessageContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/22 17:15
 */
@Slf4j
@Component
public class TestListener implements Listener<TestEntity> {
    @Override
    public void onMessage(MessageContent<TestEntity> entity) {
        log.debug("entity:{}", entity);
    }
}
