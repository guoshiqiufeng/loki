## LOKI

[![Maven central](https://img.shields.io/maven-central/v/io.github.guoshiqiufeng/loki.svg?style=flat-square)](https://search.maven.org/search?q=g:io.github.guoshiqiufeng%20AND%20a:loki)
[![License](https://img.shields.io/:license-apache-brightgreen.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![CodeQL](https://github.com/guoshiqiufeng/loki/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/guoshiqiufeng/loki/actions/workflows/github-code-scanning/codeql)
[![Commit-Activity](https://img.shields.io/github/commit-activity/m/guoshiqiufeng/loki)](https://github.com/guoshiqiufeng/loki/graphs/commit-activity)

Read in other languages: [简体中文](README-zh.md)

### Introduction

Unified message sending and consumption framework that simplifies the use of message queues. Provides unified message
sending and consumption interfaces, supports multiple message queue implementations, and currently supports RocketMQ
4.x and above, Kafka 2.x and above, and Redis 5.X and above

### Documentation

https://guoshiqiufeng.github.io/loki-doc/en/

### Development Framework

- Java 21
- Gradle 8.8
- Spring Boot 2.7.18
- rocketmq-client 5.2.0
- rocketmq-client-java 5.0.7 (RocketMQ-grpc)
- kafka-clients 3.7.0
- jedis 5.1.3
- spring-data-redis (Optional)

### Features

* Available - ✅
* In progress - 🚧

| Features                                       | Rocketmq-gRPC | Rocketmq-Remoting | Kafka | Redis |   
|------------------------------------------------|:-------------:|:-----------------:|-------|-------| 
| 【BaseMapper】Send standard messages             |       ✅       |         ✅         | ✅     | ✅     |    
| 【BaseMapper】Send async messages                |       ✅       |         ✅         | ✅     | ✅     |    
| 【BaseMapper】Send timed/delay messages          |       ✅       |         ✅         | 🚧    | ✅     |    
| 【LokiClient】Send standard messages             |       ✅       |         ✅         | ✅     | ✅     |    
| 【LokiClient】Send async messages                |       ✅       |         ✅         | ✅     | ✅     |    
| 【LokiClient】Send timed/delay messages          |       ✅       |         ✅         | 🚧    | ✅     |    
| Producer with transactional messages           |      🚧       |        🚧         | 🚧    | 🚧    |
| 【Topic】 consumer with message listener         |       ✅       |         ✅         | ✅     | ✅     |    
| 【Topic-Pattern】 consumer with message listener |      🚧       |        🚧         | ✅     | ✅     |    

Note:

- Rocketmq-Remoting `Send timed/delay messages` is only supported in `rocketmq 5.0` and above
- Redis `Send timed/delay messages` requires Redis to enable `notify-keyspace-events Ex` notification.

### Use

> Can be referred to [loki-test](https://github.com/guoshiqiufeng/loki-test)
