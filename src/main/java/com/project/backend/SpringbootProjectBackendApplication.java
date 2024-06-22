package com.project.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.project.backend.mapper")
public class SpringbootProjectBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootProjectBackendApplication.class, args);
    }

}
