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
package io.github.guoshiqiufeng.loki.support.core.pipeline;

import cn.hutool.extra.spring.SpringUtil;
import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerRecord;
import io.github.guoshiqiufeng.loki.support.core.pipeline.model.ConsumerRecordModel;
import io.github.guoshiqiufeng.loki.support.core.pipeline.model.ProducerRecordModel;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/30 17:44
 */
@UtilityClass
public class PipelineUtils {

    /**
     * 触发发送拦截
     *
     * @param producerRecord 消息
     * @return 拦截处理后消息
     */
    public ProducerRecord processSend(ProducerRecord producerRecord) {
        PipelineHandler pipelineHandler = SpringUtil.getBean(PipelineHandler.class);
        if (pipelineHandler == null) {
            return producerRecord;
        }
        ProducerRecordModel producerRecordModel = new ProducerRecordModel();
        BeanUtils.copyProperties(producerRecord, producerRecordModel);
        PipelineContext<ProducerRecordModel> context = PipelineContext.<ProducerRecordModel>builder()
                .code(PipelineTypeEnum.SEND.name())
                .processModel(producerRecordModel)
                .needBreak(false)
                .response("").build();
        PipelineContext<ProducerRecordModel> process = pipelineHandler.process(context);
        return process.getProcessModel();
    }

    /**
     * 触发接收拦截
     *
     * @param consumerRecord 消息
     * @return 拦截处理后消息
     */
    public ConsumerRecord processListener(ConsumerRecord consumerRecord) {
        PipelineHandler pipelineHandler = SpringUtil.getBean(PipelineHandler.class);
        if (pipelineHandler == null) {
            return consumerRecord;
        }
        ConsumerRecordModel consumerRecordModel = new ConsumerRecordModel();
        BeanUtils.copyProperties(consumerRecord, consumerRecordModel);
        PipelineContext<ConsumerRecordModel> context = PipelineContext.<ConsumerRecordModel>builder()
                .code(PipelineTypeEnum.LISTENER.name())
                .processModel(consumerRecordModel)
                .needBreak(false)
                .response("").build();
        PipelineContext<ConsumerRecordModel> process = pipelineHandler.process(context);
        return process.getProcessModel();
    }
}
