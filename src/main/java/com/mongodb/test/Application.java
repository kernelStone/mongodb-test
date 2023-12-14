package com.mongodb.test;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan({
        "com.mongodb.test.*"
})
@EnableMongoRepositories({
        "com.mongodb.test.dao"
})
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).bannerMode(Banner.Mode.OFF).run(args);
    }
}
