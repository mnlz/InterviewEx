package com.edu.interviewex.service.impl;

import com.edu.interviewex.dao.InterviewDao;
import com.edu.interviewex.entity.Interview;
import com.edu.interviewex.service.InterviewService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Arrays;

/**
 * 面试题服务实现类
 */
@Service
public class InterviewServiceImpl implements InterviewService {
    @Autowired
    private InterviewDao interviewDao;

    @Override
    public List<Interview> getAllInterviews() {
        return interviewDao.selectAll();
    }

    @Override
    public Interview getInterviewById(Long id) {
        return interviewDao.selectById(id);
    }

    @Override
    public int createInterview(Interview interview) {
        return interviewDao.insert(interview);
    }

    @Override
    public int updateInterview(Interview interview) {
        return interviewDao.update(interview);
    }

    @Override
    public int deleteInterview(Long id) {
        return interviewDao.deleteById(id);
    }

    /**
     * 根据公司名称进行模糊搜索（分页）
     * 如果没有提供关键字，则返回所有面试题
     * 如果提供了多个关键字，用逗号分隔，会对每个关键字进行模糊匹配
     * 
     * @param companyKeyword 公司关键字，如："哔哩哔哩,B站"
     * @param pageable 分页参数
     * @return 分页后的面试题列表，按编辑时间降序排序
     */
    @Override
    public Page<Interview> searchByCompany(String companyKeyword, Pageable pageable) {
        // 处理搜索关键字
        List<String> keywords = null;
        if (companyKeyword != null && !companyKeyword.trim().isEmpty()) {
            keywords = Arrays.asList(companyKeyword.split(","));
        }
        
        // 使用PageHelper进行分页
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());
        List<Interview> interviews = interviewDao.searchByCompanyWithPage(keywords);
        
        // 获取分页信息
        PageInfo<Interview> pageInfo = new PageInfo<>(interviews);
        
        // 返回分页结果
        return new PageImpl<>(
            pageInfo.getList(),
            pageable,
            pageInfo.getTotal()
        );
    }

    @Override
    public Page<Interview> advancedSearch(String title, String company, Pageable pageable) {
        // 使用PageHelper进行分页
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());
        List<Interview> interviews = interviewDao.advancedSearch(title, company);
        
        // 获取分页信息
        PageInfo<Interview> pageInfo = new PageInfo<>(interviews);
        
        // 转换为Spring Data的Page对象
        return new PageImpl<>(
            pageInfo.getList(),
            pageable,
            pageInfo.getTotal()
        );
    }
}