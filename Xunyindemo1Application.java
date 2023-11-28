package com.example.xunyindemo_1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.xunyindemo_1.mapper")
public class Xunyindemo1Application {

    public static void main(String[] args) {
        SpringApplication.run(Xunyindemo1Application.class, args);
    }

}
