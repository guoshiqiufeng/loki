## LOKI

[![Maven central](https://img.shields.io/maven-central/v/io.github.guoshiqiufeng/loki.svg?style=flat-square)](https://search.maven.org/search?q=g:io.github.guoshiqiufeng%20AND%20a:loki)
[![License](https://img.shields.io/:license-apache-brightgreen.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![CodeQL](https://github.com/guoshiqiufeng/loki/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/guoshiqiufeng/loki/actions/workflows/github-code-scanning/codeql)

### 介绍

统一的消息发送、消费框架，简化mq使用。提供统一的消息发送、消费接口，支持多种mq实现，目前支持rocketmq 5.x、Kafka 3.x、Redis 6.X

### 文档

https://guoshiqiufeng.github.io/loki-doc/

### 开发框架

- Java 21
- Gradle 8.5
- RocketMQ 5.x
- Spring Boot 2.7.18
- Kafka 3.x（kafka-clients 3.6.1）
- Redis 6.X (jedis 5.1.0)

### 功能

| Mq        | Send          | Listener      | Listener  TopicPattern |
|-----------|---------------|-----------|------------------------|
| RocketMQ    | 普通消息、定时/延时消息、顺序消息 | push      | 暂不支持                   |
| Kafka |   普通消息        |   poll        | 支持                     |
| Redis      | publish       | subscribe | psubscribe             |

### 使用

> 可参考 [loki-test](https://github.com/guoshiqiufeng/loki-test)
