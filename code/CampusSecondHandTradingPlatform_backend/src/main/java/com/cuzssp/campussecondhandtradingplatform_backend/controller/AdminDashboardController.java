package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.AnnouncementRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminService adminService;    // 管理员操作
    private final UserService userService;  // 账户操作 用户管理
    private final ProductService productService;    // 商品操作 商品管理
    private final OrderService orderService;    // 订单查验 订单管理
    private final CategoryService categoryService;  // 分类操作 分类管理
    private final AnnouncementService announcementService;  // 公告操作 公告管理

    /**
     * 获取仪表盘
     */
    @GetMapping("/dashboard")
    public Result<?> getDashboard() {
        return adminService.getDashboard();
    }

    // 用户管理
    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public Result<?> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        return userService.getUserList(page, pageSize, keyword);
    }

    /**
     * 添加管理员
     */
    @PostMapping("/users/newadmin")
    public Result<?> addAdmin(
            @Valid @RequestBody RegisterRequest request
    ) {
        return userService.addAdmin(request);
    }

    /**
     * 修改用户状态
     */
    @PutMapping("/users/{id}/status")
    public Result<?> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        return userService.updateUserStatus(id, status);
    }

    // 商品管理
    /**
     * 获取商品列表
     */
    @GetMapping("/products")
    public Result<?> getProductList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        return productService.getProductList(page, pageSize, keyword, status);
    }

    /**
     * 修改商品状态
     */
    @PutMapping("/products/{id}/status")
    public Result<?> updateProductStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        return productService.updateProduct(id, status);
    }

    // 订单管理 仅查看
    /**
     * 获取订单表
     */
    @GetMapping("/orders")
    public Result<?> getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status
    ) {
        return orderService.getOrders(page, pageSize, status);
    }

    // 分类管理
    /**
     * 创建类别
     */
    @PostMapping("/categories")
    public Result<?> createCategory(
            @RequestBody Category category
    ) {
        return categoryService.createCategory(category);
    }

    /**
     * 修改类别信息 基于 id
     */
    @PutMapping("/categories/{id}")
    public Result<?> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category
    ) {
        return categoryService.updateCategory(id, category);
    }

    /**
     * 删除类别 基于 id
     */
    @DeleteMapping("/categories/{id}")
    public Result<?> deleteCategory(
            @PathVariable Long id
    ) {
        return categoryService.removeCategory(id);
    }

    // 公告管理
    /**
     * 新增公告
     */
    @PostMapping("/announcements")
    public Result<?> createAnnouncement(
            @Valid @RequestBody AnnouncementRequest announcementRequest
    ) {
        return announcementService.createAnnouncement(announcementRequest);
    }

    /**
     * 修改公告
     */
    @PutMapping("/announcements/{id}")
    public Result<?> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest announcementRequest
    ) {
        return announcementService.updateAnnouncement(id, announcementRequest);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/announcements/{id}")
    public Result<?> deleteAnnouncement(
            @PathVariable Long id
    ) {
        return announcementService.removeAnnouncement(id);
    }

}
