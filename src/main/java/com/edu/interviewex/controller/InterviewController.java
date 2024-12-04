package com.edu.interviewex.controller;

import com.edu.interviewex.entity.Interview;
import com.edu.interviewex.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 面试题控制器
 */
@RestController
@RequestMapping("/api/interviews")
@CrossOrigin
public class InterviewController {
    @Autowired
    private InterviewService interviewService;

    /**
     * 获取所有面试题
     */
    @GetMapping
    public ResponseEntity<Page<Interview>> getAllInterviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(interviewService.searchByCompany(null, PageRequest.of(page, size)));
    }

    /**
     * 根据ID获取面试题
     */
    @GetMapping("/{id}")
    public ResponseEntity<Interview> getInterviewById(@PathVariable Long id) {
        Interview interview = interviewService.getInterviewById(id);
        if (interview != null) {
            return ResponseEntity.ok(interview);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 创建面试题
     */
    @PostMapping
    public ResponseEntity<Integer> createInterview(@RequestBody Interview interview) {
        return ResponseEntity.ok(interviewService.createInterview(interview));
    }

    /**
     * 更新面试题
     */
    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateInterview(@PathVariable Long id, @RequestBody Interview interview) {
        interview.setId(id);
        return ResponseEntity.ok(interviewService.updateInterview(interview));
    }

    /**
     * 删除面试题
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteInterview(@PathVariable Long id) {
        return ResponseEntity.ok(interviewService.deleteInterview(id));
    }

    /**
     * 搜索面试题（支持分页和公司名称模糊搜索）
     * 支持多个公司名称或简称，用逗号分隔
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Interview>> searchByCompany(
            @RequestParam(value = "companyNames", required = false) String companyNames,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<String> companyList = companyNames != null ? 
            Arrays.asList(companyNames.split(",")) : 
            Collections.emptyList();
        return ResponseEntity.ok(interviewService.searchByCompany(
            companyList.isEmpty() ? null : String.join(",", companyList), 
            PageRequest.of(page, size)));
    }

    /**
     * 高级搜索接口：支持标题和公司名称搜索
     */
    @GetMapping("/advanced-search")
    public ResponseEntity<Page<Interview>> advancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(interviewService.advancedSearch(title, company, PageRequest.of(page, size)));
    }
}
