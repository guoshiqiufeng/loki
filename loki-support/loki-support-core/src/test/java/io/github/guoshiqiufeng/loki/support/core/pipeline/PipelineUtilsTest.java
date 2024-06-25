package io.github.guoshiqiufeng.loki.support.core.pipeline;

import io.github.guoshiqiufeng.loki.support.core.consumer.ConsumerRecord;
import io.github.guoshiqiufeng.loki.support.core.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/4/17 15:41
 */
public class PipelineUtilsTest {

    @Test
    public void testProcessSend() {
        ProducerRecord producerRecord = new ProducerRecord();
        producerRecord.setTopic("processSend");
        producerRecord.setTag("create");
        producerRecord.setMessage("test message");

        Assert.assertNull(PipelineUtils.processSend(producerRecord));
    }

    @Test
    public void testProcessListener() {
        ConsumerRecord consumerRecord = new ConsumerRecord();
        consumerRecord.setTopic("processListener");
        consumerRecord.setTag("create");
        consumerRecord.setMessageId("");
        consumerRecord.setMessageGroup("");
        consumerRecord.setBodyMessage("test message listener");

        Assert.assertNull(PipelineUtils.processListener(consumerRecord));
    }
}
