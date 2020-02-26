package com.one.learn.springcloud.kafka.springcloudkafkademo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableKafkaStreams
@SpringBootApplication
public class SpringcloudKafkaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudKafkaDemoApplication.class, args);
    }

}
