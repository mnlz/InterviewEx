package com.edu.interviewex.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Data
public class Question {
    private Long id;
    private String company;
    private String question;
    private String category;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime editTime;
    private Integer status;
}
