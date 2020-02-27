package com.one.learn.springcloud.kafka.springcloudkafkademo.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author one
 * @date 2020/02/26
 */
@Slf4j
@RestController
public class EchoController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutputMessageBean messageBean;

    public EchoController(KafkaTemplate<String, String> kafkaTemplate, OutputMessageBean messageBean) {
        this.kafkaTemplate = kafkaTemplate;
        this.messageBean = messageBean;
    }

    @GetMapping("/echo")
    public String echo() throws ExecutionException, InterruptedException {
        String message = "hello,world";
        ListenableFuture<SendResult<String, String>> test = kafkaTemplate.send("test", message);
        SendResult<String, String> stringStringSendResult = test.get();
        log.info("send done {}", stringStringSendResult.toString());
        return message;
    }

    @GetMapping("/echo/stream")
    public String echoStream() {
        String message = "hello,world";
        messageBean.sendMessage(message);
        return message;
    }

}
