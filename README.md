# Spring Cloud Stream

<a name="FcaRS"></a>
## 原生 Kafka

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

<a name="6IHM8"></a>
## Spring Kafka & Spring Boot Kafka

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

<a name="b286c4d7"></a>
## Spring Cloud Stream Kafka
> 参考资料：[https://cloud.spring.io/spring-cloud-static/spring-cloud-stream-binder-kafka/3.0.2.RELEASE/reference/html/spring-cloud-stream-binder-kafka.html#_apache_kafka_binder](https://cloud.spring.io/spring-cloud-static/spring-cloud-stream-binder-kafka/3.0.2.RELEASE/reference/html/spring-cloud-stream-binder-kafka.html#_apache_kafka_binder)


<a name="0aed8e84"></a>
#### 添加服务依赖
**pom.xml**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-kafka</artifactId>
    <version>3.0.2.RELEASE</version>
</dependency>
```

<a name="62f79389"></a>
#### 添加输出源配置项
**application.properties**
```javascript
#spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
#spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.cloud.stream.bindings.output.destination=test
```

> Spring Cloud Stream Kafka默认采用 `org.apache.kafka.common.serialization.ByteArraySerializer` 进行 Value 的序列化，如果使用了其他 Serializer 会报错 ClassCastException 异常。


<a name="d866bb49"></a>
#### 定义输出源 Bean
```java
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
```

上述代码关键在于使用注解类 `@EnableBinding` 将 Source 对象进行了注入，而 Soucre 是 Spring Cloud Stream 中用来发布消息的，类似发布者 Publisher的概念。

<a name="34fcf663"></a>
#### 添加接收器配置项
**application.properties**
```properties
spring.cloud.stream.bindings.input.destination=test
```

<a name="032bd29d"></a>
#### 定义接收器 Bean
```java
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
```

上述代码采用了三种接收器形式来接受消息，`Sink` 实现类，`@ServiceActivator`注解类，[@StreamListener ]() 注解类。

但需要注意的是，对于同一个 topic，接收端处理消息的优先级： `SubscribableChannel` 》`@ServiceActivator` 》 `@StreamListener`。优先级高的接收器处理完之后，后续的接收器就无法处理了。

<a name="2f6592cb"></a>
## Spring Cloud Stream Rabbit

参考 **Spring Cloud Stream Kafka**，调整服务依赖即可：<br />**pom.xml**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
    <version>3.0.2.RELEASE</version>
</dependency>
```
