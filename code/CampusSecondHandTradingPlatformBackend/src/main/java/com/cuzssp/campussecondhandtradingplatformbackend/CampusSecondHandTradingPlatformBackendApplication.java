package com.cuzssp.campussecondhandtradingplatformbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cuzssp.campussecondhandtradingplatformbackend.mapper")
public class CampusSecondHandTradingPlatformBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusSecondHandTradingPlatformBackendApplication.class, args);
    }

}
