## LOKI

[![Maven central](https://img.shields.io/maven-central/v/io.github.guoshiqiufeng/loki.svg?style=flat-square)](https://search.maven.org/search?q=g:io.github.guoshiqiufeng%20AND%20a:loki)
[![License](https://img.shields.io/:license-apache-brightgreen.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)

### 介绍

统一的消息发送、消费框架，简化mq使用。提供统一的消息发送、消费接口，支持多种mq实现，目前支持rocketmq 5.x

### 文档

https://guoshiqiufeng.github.io/loki-doc/

### 开发框架

- Java 21
- Gradle 8.5
- RocketMQ 5.x
- Spring Boot 2.7.18
- Kafka 3.X（kafka-clients 3.6.1）

### 使用

> 可参考
>
> - [loki-test](https://github.com/guoshiqiufeng/loki-test)
>
> - [loki-spring-boot-starter-test](spring-boot-starter%2Floki-spring-boot-starter-test)

#### 1. 添加依赖

##### 1.1 全部依赖

- groovy

```groovy
    api project("io.github.guoshiqiufeng:loki-spring-boot-starter:0.3.0")
```

- maven

```maven
   <dependency>
        <groupId>io.github.guoshiqiufeng</groupId>
        <artifactId>loki-spring-boot-starter</artifactId>
        <version>0.3.0</version>
   </dependency>
```

##### 1.2 只用rocketmq依赖

- groovy

```groovy
    api project("io.github.guoshiqiufeng:loki-spring-boot-starter-rocketmq:0.3.0")
```

- maven

```maven
   <dependency>
        <groupId>io.github.guoshiqiufeng</groupId>
        <artifactId>loki-spring-boot-starter-rocketmq</artifactId>
        <version>0.3.0</version>
   </dependency>
```

##### 1.3 只用kafka依赖

- groovy

```groovy
    api project("io.github.guoshiqiufeng:loki-spring-boot-starter-kafka:0.3.0")
```

- maven

```maven
   <dependency>
        <groupId>io.github.guoshiqiufeng</groupId>
        <artifactId>loki-spring-boot-starter-kafka</artifactId>
        <version>0.3.0</version>
   </dependency>
```

#### 2. 配置

```yaml
loki:
  global-config:
    mq-config:
      mq-type: ROCKET_MQ # 默认为 ROCKET_MQ
      address: 127.0.0.1:8081 # 默认为 127.0.0.1:8081
      auth: false     #若不用密码可不配置
      username: username #若不用密码可不配置
      password: password #若不用密码可不配置
      connect-timeout: 300   #默认为 300 s
      max-attempts: 5    #重试次数，默认为5次
```

#### 3. 启动类添加注解

```java
@LokiMapperScan
@SpringBootApplication
public class LokiTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LokiTestApplication.class, args);
    }

}
```

#### 4. 消息发送

```java

@Data
@MessageName(topic = "loki", tag = "create")
public class TestEntity {

    @MessageKey
    private String id;

    private String message;
}

```

```java
public interface TestMapper extends BaseMapper<TestEntity> {

}
```

```java

@RestController
public class TestController {

    @Resource
    private TestMapper testMapper;

    @GetMapping("send")
    public String send() {
        TestEntity entity = new TestEntity();
        entity.setId("9521");
        entity.setMessage("test");
        String messageId = testMapper.send(entity);
        log.debug("send messageId:{}", messageId);
        return "success";
    }

    @GetMapping("sendAsync")
    public String sendAsync() {
        TestEntity entity = new TestEntity();
        entity.setId("9521");
        entity.setMessage("sendAsync");
        testMapper.sendAsync(entity);
        return "success";
    }
}
```

#### 5. 消息接收

```java

@Component
public class TestListener implements Listener<TestEntity> {
    @Override
    public void onMessage(MessageContent<TestEntity> entity) {
        log.debug("entity:{}", entity);
    }
}
```

或

```java
@MessageListener(topic = "loki")
@Component
public class TestListener implements Listener<String> {
    @Override
    public void onMessage(MessageContent<String> entity) {
        log.debug("entity:{}", entity);
    }
}
