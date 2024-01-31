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
package io.github.guoshiqiufeng.loki.support.core;

import cn.hutool.extra.spring.SpringUtil;
import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineContext;
import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineHandler;
import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineTypeEnum;
import io.github.guoshiqiufeng.loki.support.core.pipeline.ProducerRecordModel;
import org.springframework.beans.BeanUtils;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/30 17:44
 */
public interface PipelineApi {

    /**
     * 触发拦截
     *
     * @param producerRecord 消息
     * @return 拦截处理后消息
     */
    default ProducerRecord processSend(ProducerRecord producerRecord) {
        PipelineHandler pipelineHandler = SpringUtil.getBean(PipelineHandler.class);
        if (pipelineHandler == null) {
            return producerRecord;
        }
        ProducerRecordModel producerRecordModel = ProducerRecordModel.builder().build();
        BeanUtils.copyProperties(producerRecord, producerRecordModel);
        PipelineContext<ProducerRecordModel> context = PipelineContext.<ProducerRecordModel>builder()
                .code(PipelineTypeEnum.SEND.name())
                .processModel(producerRecordModel)
                .needBreak(false)
                .response("").build();
        PipelineContext<ProducerRecordModel> process = pipelineHandler.process(context);
        return process.getProcessModel();
    }
}
