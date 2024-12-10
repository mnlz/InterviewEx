package com.edu.interviewex.service.impl;

import com.edu.interviewex.dao.QuestionDao;
import com.edu.interviewex.entity.Question;
import com.edu.interviewex.service.QuestionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    
    @Autowired
    private QuestionDao questionDao;

    @Override
    public Page<Question> findAll(Pageable pageable) {
        // 使用PageHelper进行分页
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());
        List<Question> questions = questionDao.selectWithPage();
        
        // 获取分页信息
        PageInfo<Question> pageInfo = new PageInfo<>(questions);
        
        // 返回分页结果
        return new PageImpl<>(
            pageInfo.getList(),
            pageable,
            pageInfo.getTotal()
        );
    }

    @Override
    public Page<Question> searchQuestions(String category, String keyword, Pageable pageable) {
        // 使用PageHelper进行分页
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());
        List<Question> questions = questionDao.searchQuestions(category, keyword);
        
        // 获取分页信息
        PageInfo<Question> pageInfo = new PageInfo<>(questions);
        
        // 返回分页结果
        return new PageImpl<>(
            pageInfo.getList(),
            pageable,
            pageInfo.getTotal()
        );
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionDao.selectById(id);
    }

    @Override
    public boolean deleteQuestion(Long id) {
        return questionDao.updateStatus(id, 1) > 0;
    }

    @Override
    public List<String> getAllCategories() {
        return questionDao.getAllCategories();
    }
}
