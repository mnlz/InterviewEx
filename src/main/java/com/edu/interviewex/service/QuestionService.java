package com.edu.interviewex.service;

import com.edu.interviewex.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface QuestionService {
    // 分页查询所有问题
    Page<Question> findAll(Pageable pageable);
    
    // 搜索问题
    Page<Question> searchQuestions(String category, String keyword, Pageable pageable);
    
    // 获取问题详情
    Question getQuestionById(Long id);
    
    // 删除问题（更新状态）
    boolean deleteQuestion(Long id);
    
    // 获取所有类别
    List<String> getAllCategories();
}
