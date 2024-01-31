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
package io.github.guoshiqiufeng.loki.support.core.config;

import io.github.guoshiqiufeng.loki.support.core.pipeline.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/30 15:02
 */
@Configuration
public class PipelineAutoConfiguration {

    @Bean("lokiSendTemplate")
    @ConditionalOnMissingBean(name = "lokiSendTemplate")
    public PipelineTemplate<ProducerRecordModel> lokiSendTemplate(List<PipelineProcess<ProducerRecordModel>> processList) {
        PipelineTemplate<ProducerRecordModel> processTemplate = new PipelineTemplate<>();
        processTemplate.setProcessList(processList);
        return processTemplate;
    }

    @Bean("lokiListenerTemplate")
    @ConditionalOnMissingBean(name = "lokiListenerTemplate")
    public PipelineTemplate<ListenerModel> lokiListenerTemplate(List<PipelineProcess<ListenerModel>> processList) {
        PipelineTemplate<ListenerModel> processTemplate = new PipelineTemplate<>();
        processTemplate.setProcessList(processList);
        return processTemplate;
    }

    /**
     * pipeline流程控制器
     *
     * @return PipelineHandler
     */
    @Bean
    @ConditionalOnMissingBean(PipelineHandler.class)
    public PipelineHandler pipelineHandler(List<PipelineProcess<ProducerRecordModel>> produceProcessList,
                                           List<PipelineProcess<ListenerModel>> processList) {
        PipelineHandler processHandler = new PipelineHandler();
        Map<String, PipelineTemplate<? extends PipelineModel>> templateConfig = new HashMap<>(2);
        templateConfig.put(PipelineTypeEnum.SEND.name(), lokiSendTemplate(produceProcessList));
        templateConfig.put(PipelineTypeEnum.LISTENER.name(), lokiListenerTemplate(processList));
        processHandler.setTemplateConfig(templateConfig);
        return processHandler;
    }
}
