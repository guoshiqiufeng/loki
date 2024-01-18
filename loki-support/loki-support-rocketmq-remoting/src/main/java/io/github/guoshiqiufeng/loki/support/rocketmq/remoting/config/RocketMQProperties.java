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
package io.github.guoshiqiufeng.loki.support.rocketmq.remoting.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/1/18 10:53
 */
@Data
public class RocketMQProperties {

    private String nameServer;
    private String accessChannel;
    private Producer producer;
    private Consumer consumer = new Consumer();

    @Data
    public static class Producer {
        private String group;
        private int sendMessageTimeout = 3000;
        private int compressMessageBodyThreshold = 4096;
        private int retryTimesWhenSendFailed = 2;
        private int retryTimesWhenSendAsyncFailed = 2;
        private boolean retryNextServer = false;
        private int maxMessageSize = 4194304;
        private String accessKey;
        private String secretKey;
        private boolean enableMsgTrace = true;
        private String customizedTraceTopic = "RMQ_SYS_TRACE_TOPIC";
    }
    @Data
    public static final class Consumer {
        private String group;
        private String topic;
        private String messageModel = "CLUSTERING";
        private String selectorType = "TAG";
        private String selectorExpression = "*";
        private String accessKey;
        private String secretKey;
        private int pullBatchSize = 10;
        private long pullInterval = 0L;
        private Map<String, Map<String, Boolean>> listeners = new HashMap<>();
    }
}
