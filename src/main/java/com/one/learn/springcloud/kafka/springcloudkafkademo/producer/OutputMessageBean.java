package com.one.learn.springcloud.kafka.springcloudkafkademo.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

/**
 * 输出源 Bean
 *
 * @author one
 * @date 2020/02/27
 */
@Slf4j
@EnableBinding(Source.class)
public class OutputMessageBean {

    private final Source source;

    @Autowired
    public OutputMessageBean(Source source) {
        this.source = source;
    }

    public void sendMessage(String body) {
        Message<String> message = new GenericMessage(body);
        boolean send = source.output().send(message);
        log.info("{} send result {}", body, send);
    }
}
