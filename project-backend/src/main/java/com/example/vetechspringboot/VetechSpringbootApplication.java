package com.example.vetechspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.example.vetechspringboot.mapper")
public class VetechSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetechSpringbootApplication.class, args);
    }

}
