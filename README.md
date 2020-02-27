## Spring Cloud Stream- Kafka

<a name="6ea95c8d"></a>
### 原生 Kafka

> 参考资料：[http://kafka.apache.org/quickstart](http://kafka.apache.org/quickstart)


<a name="6a9789db"></a>
#### 搭建单机环境

**1. 下载二进制包，解压**

```bash
tar kafka_2.13-2.4.0.tgz
cd kafka_2.13-2.4.0.tgz
```

> kafka所在目录路径不能有空格，否则启动会报错。
> **2. 利用解压后的文件，启动 Zookeeper**


```bash
sh bin/zookeeper-server-start.sh config/zookeeper.properties
```

**3. 启动 Kafka 服务端（broker）**

```bash
sh bin/kafka-server-start.sh config/server.properties
```

**4. 创建 topic （主题）**

```bash
sh bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test
```

可以用命令 `bin/kafka-topics.sh --list --bootstrap-server localhost:9092` 查看所有 topic。<br />**5. 启动生产者发送消息**

```bash
sh bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
> This is a message
```

**6. 启动消费者接受消息**

```bash
sh bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
> This is a message
```

> 关于搭建 Kafka集群，可参考官方快速指引：[http://kafka.apache.org/quickstart#quickstart_multibroker](http://kafka.apache.org/quickstart#quickstart_multibroker)


<a name="435fa2c7"></a>
#### Kafka 的优势

适合构建实时Stream（流式）计算平台系统，性能高，效率高。

<a name="23a89fca"></a>
### Spring Kafka & Spring Boot Kafka

> 参考资料；[https://docs.spring.io/spring-kafka/docs/2.4.2.RELEASE/reference/html/](https://docs.spring.io/spring-kafka/docs/2.4.2.RELEASE/reference/html/)


<a name="5b30e44c"></a>
#### 关键依赖

```xml
<dependency>
   <groupId>org.springframework.kafka</groupId>
   <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
   <groupId>org.apache.kafka</groupId>
   <artifactId>kafka-streams</artifactId>
</dependency>
```

<a name="59e7b003"></a>
#### Kafka 自动装配

Spring 利用注解 [@EnableKafkaStreams ]()  和  KafkaAutoConfiguration.java 开启 Kafka 的自动装配。

<a name="65cf4774"></a>
#### 关键配置项

**application.xml**

```properties
spring.application.name=springcloud-kafka
## 指定 kafka 服务端地址
spring.kafka.bootstrap-servers=localhost:9092
## 设置生产者的key序列化方式
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
## 设置生产者的value序列化方式
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
## 设置消费者的分组id
spring.kafka.listener.group-id=one-1
## 设置消费者的 key 反序列化方式
spring.kafka.listener.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
## 设置消费者的 value 反序列化方式
spring.kafka.listener.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

<a name="5dacb908"></a>
#### 消费者实现

```java
@Component
public class EchoListener {
   @KafkaListener(id = "message", topics = "test")
   public void recive(String message) {
       log.info("recive message: {}", message);
   }
}
```

<a name="a04435a9"></a>
#### 生产者实现

```java
@RestController
public class EchoController {
   private final KafkaTemplate<String, String> kafkaTemplate;
   public EchoController(KafkaTemplate<String, String> kafkaTemplate) {
       this.kafkaTemplate = kafkaTemplate;
   }
   @GetMapping("/echo")
   public String echo() throws ExecutionException, InterruptedException {
       String message = "hello,world";
       ListenableFuture<SendResult<String, String>> test = kafkaTemplate.send("test", message);
       SendResult<String, String> stringStringSendResult = test.get();
       log.info("send done {}", stringStringSendResult.toString());
       return message;
   }
}
```
