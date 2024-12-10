package com.edu.interviewex.dao;

import com.edu.interviewex.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface QuestionDao {
    // 查询所有问题
    List<Question> selectWithPage();
    
    // 搜索问题
    List<Question> searchQuestions(@Param("category") String category,
                                 @Param("keyword") String keyword);
    
    // 获取总记录数
    int countQuestions(@Param("category") String category,
                      @Param("keyword") String keyword);
    
    // 根据ID查询问题
    Question selectById(@Param("id") Long id);
    
    // 更新问题状态
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    // 获取所有类别
    List<String> getAllCategories();
}
