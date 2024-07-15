package com.project.kyhshop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.kyhshop.dao.CategoryDao;

@Controller
public class CategoryController {
    @Autowired
    CategoryDao categoryDao;

    // 카테고리 페이지
    @GetMapping("/category")
    public String categoryList(Model model) {
        List<Map<String, Object>> categories = categoryDao.selectAllCategories();
        model.addAttribute("categories", categories);
        return "/html/common/categorylist"; // 수정된 경로 적용
    }

    // 카테고리 등록 액션
    @PostMapping("/category/insert")
    public String insertCategory(@RequestParam String categoryName) {
        categoryDao.insertCategory(categoryName);
        return "redirect:/category";
    }
}
