## loki

[![Maven central](https://img.shields.io/maven-central/v/io.github.guoshiqiufeng/loki.svg?style=flat-square)](https://search.maven.org/search?q=g:io.github.guoshiqiufeng%20AND%20a:loki)
[![License](https://img.shields.io/:license-apache-brightgreen.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)

## loki介绍

统一的消息发送、消费框架，简化mq使用。提供统一的消息发送、消费接口，支持多种mq实现，目前支持rocketmq 5.x

### 开发框架

- java 21
- gradle 8.4
- rocketmq 5.x
- spring boot 2.7.17

### 使用

> 可参考 [loki-spring-boot-starter-test](spring-boot-starter%2Floki-spring-boot-starter-test)

#### 1. 添加依赖

- groovy

```groovy
    api project("io.github.guoshiqiufeng:loki-spring-boot-starter:0.0.1")
```

- maven

```maven
   <dependency>
        <groupId>io.github.guoshiqiufeng</groupId>
        <artifactId>loki-spring-boot-starter</artifactId>
        <version>0.0.1</version>
   </dependency>
```

#### 2. 消息发送

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

#### 3. 消息接收

```java

@Component
public class TestListener implements MessageListener<TestEntity> {
    @Override
    public void onMessage(MessageContent<TestEntity> entity) {
        log.debug("entity:{}", entity);
    }
}

```

