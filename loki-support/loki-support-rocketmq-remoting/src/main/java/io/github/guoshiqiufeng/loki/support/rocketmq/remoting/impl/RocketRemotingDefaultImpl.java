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
package io.github.guoshiqiufeng.loki.support.rocketmq.remoting.impl;

import io.github.guoshiqiufeng.loki.support.rocketmq.remoting.RocketRemotingClient;
import io.github.guoshiqiufeng.loki.support.rocketmq.remoting.config.RocketMQProperties;
import io.github.guoshiqiufeng.loki.support.rocketmq.remoting.util.RocketRemotingConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/18 10:15
 */
@Slf4j
public class RocketRemotingDefaultImpl implements RocketRemotingClient {

    private final RocketMQProperties rocketProperties;

    public RocketRemotingDefaultImpl(RocketMQProperties rocketProperties) {
        this.rocketProperties = rocketProperties;
    }


    /**
     * 发送消息
     *
     * @param producerName
     * @param message
     */
    @Override
    public String send(String producerName, Message message) {
        DefaultMQProducer producer = RocketRemotingConfigUtils.getProducer(producerName, rocketProperties);
        try {
            return producer.send(message).getMsgId();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("send message error, producerName:{}, message:{} ", producerName, message, e);
            }
        }
        return null;
    }

    /**
     * 获取消费者
     *
     * @param consumerGroup 消费者组
     * @param index         消费者索引
     * @return 消费者
     */
    @Override
    public DefaultMQPushConsumer getConsumer(String consumerGroup, Integer index) {
        return RocketRemotingConfigUtils.getConsumerBuilder(rocketProperties, consumerGroup, index);
    }

}
