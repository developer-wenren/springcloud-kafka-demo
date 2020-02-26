package com.one.learn.springcloud.kafka.springcloudkafkademo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author one
 * @date 2020/02/26
 */
@Slf4j
@Component
public class EchoListener {
    @KafkaListener(id = "message", topics = "test")
    public void recive(String message) {
        log.info("recive message: {}", message);
    }
}
