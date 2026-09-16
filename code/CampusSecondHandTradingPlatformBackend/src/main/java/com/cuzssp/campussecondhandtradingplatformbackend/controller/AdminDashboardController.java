package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.AnnouncementRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.CategoryRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ResetPasswordRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.UserRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.service.AdminService;
import com.cuzssp.campussecondhandtradingplatformbackend.service.UserService;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ProductService;
import com.cuzssp.campussecondhandtradingplatformbackend.service.OrderService;
import com.cuzssp.campussecondhandtradingplatformbackend.service.CategoryService;
import com.cuzssp.campussecondhandtradingplatformbackend.service.AnnouncementService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.annotation.Validated;

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
        return Result.success(adminService.getDashboard());
    }

    // 用户管理
    /**
     * 获取用户列表
     */
    @GetMapping("/user")
    public Result<?> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        return Result.success(userService.getUserList(page, pageSize, keyword));
    }

    /**
     * 添加管理员
     */
    @PostMapping("/user/newadmin")
    public Result<?> addAdmin(
            @Validated({Default.class, UserRequest.Create.class})
            @RequestBody UserRequest request
    ) {
        return Result.success(userService.addAdmin(request));
    }

    /**
     * 修改用户状态
     */
    @PutMapping("/user/{id}/status")
    public Result<?> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        return Result.success(userService.updateUserStatus(id, status));
    }

    /**
     * 重置密码
     */
    @PutMapping("/user/{id}/reset-password")
    public Result<?> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        return Result.success(userService.resetPassword(id, request));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/user/{id}")
    public Result<?> deleteUser(
            @PathVariable Long id
    ) {
        return Result.success(userService.deleteUserById(id));
    }

    // 商品管理
    /**
     * 获取商品列表
     */
    @GetMapping("/product")
    public Result<?> getProductList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(productService.getProductList(page, pageSize, keyword, status));
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/product/{id}")
    public Result<?> getProductDetail(
            @PathVariable Long id
    ) {
        return Result.success(productService.getProductDetailForAdmin(id));
    }

    /**
     * 修改商品状态
     */
    @PutMapping("/product/{id}/status")
    public Result<?> updateProductStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        return Result.success(productService.updateProduct(id, status));
    }

    // 订单管理 仅查看
    /**
     * 获取订单表
     */
    @GetMapping("/order")
    public Result<?> getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(orderService.getOrders(page, pageSize, status));
    }

    // 分类管理
    /**
     * 创建类别
     */
    @PostMapping("/category")
    public Result<?> createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {
        return Result.success(categoryService.createCategory(request));
    }

    /**
     * 修改类别信息 基于 id
     */
    @PutMapping("/category/{id}")
    public Result<?> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        return Result.success(categoryService.updateCategory(id, request));
    }

    /**
     * 删除类别 基于 id
     */
    @DeleteMapping("/category/{id}")
    public Result<?> deleteCategory(
            @PathVariable Long id
    ) {
        return Result.success(categoryService.removeCategory(id));
    }

    // 公告管理
    /**
     * 新增公告
     */
    @PostMapping("/announcement")
    public Result<?> createAnnouncement(
            @Valid @RequestBody AnnouncementRequest announcementRequest
    ) {
        return Result.success(announcementService.createAnnouncement(announcementRequest));
    }

    /**
     * 修改公告
     */
    @PutMapping("/announcement/{id}")
    public Result<?> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest announcementRequest
    ) {
        return Result.success(announcementService.updateAnnouncement(id, announcementRequest));
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/announcement/{id}")
    public Result<?> deleteAnnouncement(
            @PathVariable Long id
    ) {
        return Result.success(announcementService.removeAnnouncement(id));
    }

}
