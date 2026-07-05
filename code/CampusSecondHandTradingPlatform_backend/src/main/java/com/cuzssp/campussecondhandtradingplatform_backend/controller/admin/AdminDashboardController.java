package com.cuzssp.campussecondhandtradingplatform_backend.controller.admin;

import com.cuzssp.campussecondhandtradingplatform_backend.service.AdminService;

import com.cuzssp.campussecondhandtradingplatform_backend.service.CategoryService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/dashboard")
    public Result<?> getDashboard() {
        return adminService.getDashboard();
    }

    @PostMapping("/categories")
    public Result<?> createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @PutMapping("/categories/{id}")
    public Result<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/categories/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}
