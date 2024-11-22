package com.edu.interviewex;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.edu.interviewex.dao")
public class InterviewExApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewExApplication.class, args);
    }

}
