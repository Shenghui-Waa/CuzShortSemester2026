package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AdminService;

import com.cuzssp.campussecondhandtradingplatform_backend.service.CategoryService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired private AdminService adminService;

    /**
     * 获取仪表盘
     * @return
     */
    @GetMapping("/dashboard")
    public Result<?> getDashboard() {
        return adminService.getDashboard();
    }

    /**
     * 获取用户列表
     * @param page
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("/users")
    public Result<?> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        return adminService.getUserList(page, pageSize, keyword);
    }

    /**
     * 修改用户状态
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/users/{id}/status")
    public Result<?> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        return adminService.updateUserStatus(id, status);
    }

    /**
     * 获取商品列表
     * @param page
     * @param pageSize
     * @param keyword
     * @param status
     * @return
     */
    @GetMapping("/products")
    public Result<?> getProductList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        return adminService.getProductList(page, pageSize, keyword, status);
    }

    /**
     * 修改商品状态
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/products/{id}/status")
    public Result<?> updateProductStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        return adminService.updateProductStatus(id, status);
    }

    /**
     * 获取订单表
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/orders")
    public Result<?> getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status
    ) {
        return adminService.getOrders(page, pageSize, status);
    }

}
