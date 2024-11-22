package com.edu.interviewex.entity;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
@Configuration
@Data
public class Interview {
    private Long id;
    private String title;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime  editTime;
    private Integer read;
    private String post;
    private String company;
    private Integer status;

    // Getters and setters
    // 省略getter和setter方法，您可以使用IDE自动生成这些方法
}
