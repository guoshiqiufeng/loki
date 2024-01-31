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
package io.github.guoshiqiufeng.loki.support.core.pipeline.model;

import io.github.guoshiqiufeng.loki.support.core.pipeline.PipelineModel;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 消息发送实体
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/1/30 15:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProducerRecordModel extends ProducerRecord implements PipelineModel, Serializable {

}
