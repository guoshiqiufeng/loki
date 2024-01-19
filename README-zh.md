## LOKI

[![Maven central](https://img.shields.io/maven-central/v/io.github.guoshiqiufeng/loki.svg?style=flat-square)](https://search.maven.org/search?q=g:io.github.guoshiqiufeng%20AND%20a:loki)
[![License](https://img.shields.io/:license-apache-brightgreen.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![CodeQL](https://github.com/guoshiqiufeng/loki/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/guoshiqiufeng/loki/actions/workflows/github-code-scanning/codeql)
[![Commit-Activity](https://img.shields.io/github/commit-activity/m/guoshiqiufeng/loki)](https://github.com/guoshiqiufeng/loki/graphs/commit-activity)

阅读其他语言版本: [English](README.md)

### 介绍

统一的消息发送、消费框架，简化mq使用。提供统一的消息发送、消费接口，支持多种mq实现，目前支持rocketmq 5.x、Kafka 3.x、Redis 5.X
以上

### 文档

https://guoshiqiufeng.github.io/loki-doc/

### 开发框架

- Java 21
- Gradle 8.5
- Spring Boot 2.7.18
- rocketmq-client-java 5.0.5 (RocketMQ-grpc)
- kafka-clients 3.6.1
- jedis 5.1.0

### 功能

* 可用 - ✅
* 进行中 - 🚧

| 功能                                             | Rocketmq-gRPC | Rocketmq-Remoting | Kafka | Redis |   
|------------------------------------------------|:-------------:|:-----------------:|-------|-------| 
| Send standard messages                         |       ✅       |         ✅         | ✅     | ✅     |    
| Send async messages                            |       ✅       |         ✅         | ✅     | ✅     |    
| Send timed/delay messages                      |       ✅       |        🚧         | 🚧    | 🚧    |    
| Producer with transactional messages           |      🚧       |        🚧         | 🚧    | 🚧    |
| 【Topic】 consumer with message listener         |       ✅       |         ✅         | ✅     | ✅     |    
| 【Topic-Pattern】 consumer with message listener |      🚧       |        🚧         | ✅     | ✅     |    

### 使用

> 可参考 [loki-test](https://github.com/guoshiqiufeng/loki-test)
