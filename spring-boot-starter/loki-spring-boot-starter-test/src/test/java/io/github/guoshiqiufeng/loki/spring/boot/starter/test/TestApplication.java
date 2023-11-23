package io.github.guoshiqiufeng.loki.spring.boot.starter.test;

import io.github.guoshiqiufeng.loki.autoconfigure.register.LokiMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yanghq
 * @version 1.0
 * @since 2023/11/15 20:12
 */
@LokiMapperScan
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
