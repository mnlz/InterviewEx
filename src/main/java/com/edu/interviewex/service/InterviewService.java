package com.edu.interviewex.service;

import com.edu.interviewex.entity.Interview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 面试题服务接口
 */
public interface InterviewService {
    /**
     * 获取所有面试题列表
     * @return 面试题列表
     */
    List<Interview> getAllInterviews();

    /**
     * 根据ID获取面试题
     * @param id 面试题ID
     * @return 面试题信息
     */
    Interview getInterviewById(Long id);

    /**
     * 创建新的面试题
     * @param interview 面试题信息
     * @return 创建结果
     */
    int createInterview(Interview interview);

    /**
     * 更新面试题信息
     * @param interview 面试题信息
     * @return 更新结果
     */
    int updateInterview(Interview interview);

    /**
     * 删除面试题
     * @param id 面试题ID
     * @return 删除结果
     */
    int deleteInterview(Long id);

    /**
     * 根据公司名称模糊搜索面试题（分页）
     * @param companyKeyword 公司关键字，多个关键字用逗号分隔（如：哔哩哔哩,B站）
     * @param pageable 分页参数
     * @return 分页后的面试题列表
     */
    Page<Interview> searchByCompany(String companyKeyword, Pageable pageable);

    /**
     * 高级搜索：支持标题和公司名称搜索（分页）
     * @param title 标题关键字
     * @param company 公司关键字
     * @param pageable 分页参数
     * @return 分页后的面试题列表
     */
    Page<Interview> advancedSearch(String title, String company, Pageable pageable);
}