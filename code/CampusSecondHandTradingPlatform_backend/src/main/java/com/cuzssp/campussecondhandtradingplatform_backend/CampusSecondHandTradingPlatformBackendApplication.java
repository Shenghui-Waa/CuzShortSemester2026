package com.cuzssp.campussecondhandtradingplatform_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@MapperScan("com.cuzssp.campussecondhandtradingplatform_backend.mapper")
public class CampusSecondHandTradingPlatformBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusSecondHandTradingPlatformBackendApplication.class, args);
    }

}