package com.edu.interviewex.dao;

import com.edu.interviewex.entity.Interview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface InterviewDao {
    int insert(Interview interview);

    List<Interview> selectAll();

    Interview selectById(Long id);

    int update(Interview interview);

    int deleteById(Long id);
    
    /**
     * Search interviews by company keywords
     * @param companyKeywords list of company keywords for fuzzy search
     * @return list of matching interviews
     */
    List<Interview> searchByCompany(@Param("companyKeywords") List<String> companyKeywords);

    /**
     * Search interviews by company keywords
     * @param keywords list of company keywords to search for
     * @param offset pagination offset
     * @param limit pagination limit
     * @return list of matching interviews
     */
    List<Interview> searchByCompanyWithPage(@Param("keywords") List<String> keywords,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);

    /**
     * Count total number of interviews matching company keywords
     * @param keywords list of company keywords to search for
     * @return total count of matching interviews
     */
    int countByCompany(@Param("keywords") List<String> keywords);

    /**
     * 高级搜索：支持标题和公司名称搜索
     * @param title 标题关键字
     * @param company 公司关键字
     * @return 匹配的面试题列表
     */
    List<Interview> advancedSearch(@Param("title") String title, @Param("company") String company);
}
