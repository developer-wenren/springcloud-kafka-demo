package com.one.learn.springcloud.kafka.springcloudkafkademo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

import javax.annotation.PostConstruct;

/**
 * 接受端 Bean
 *
 * @author one
 * @date 2020/02/27
 */
@Slf4j
@EnableBinding(Sink.class)
public class InputMessageBean {
    @Autowired
    @Qualifier(Sink.INPUT)
    private SubscribableChannel channel;

    private final Sink sink;

    public InputMessageBean(Sink sink) {
        this.sink = sink;
    }

    @PostConstruct
    private void init() {
//        sink.input().subscribe(message -> {
//            GenericMessage<byte[]> msg = (GenericMessage<byte[]>) message;
//            log.info("\n=====\nsubscribed message : {}\n======", new String(msg.getPayload()));
//        });
        // 与上方代码等价
        channel.subscribe(message -> {
            GenericMessage<byte[]> msg = (GenericMessage<byte[]>) message;
            log.info("\n=====\nsubscribed message : {}\n======", new String(msg.getPayload()));
        });
    }


    @ServiceActivator(inputChannel = Sink.INPUT)
    public void onMessage2(Object data) {
        log.info("\n=====\n@ServiceActivator message : {}\n======", data);
    }

    @StreamListener(Sink.INPUT)
    public void onMessage(Object data) {
        log.info("\n=====\n@StreamListener message : {}\n======", data);
    }
}
