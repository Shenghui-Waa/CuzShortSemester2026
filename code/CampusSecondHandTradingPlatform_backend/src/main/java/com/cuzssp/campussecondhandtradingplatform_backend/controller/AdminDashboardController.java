package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.AnnouncementRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AdminService;

import com.cuzssp.campussecondhandtradingplatform_backend.service.AnnouncementService;
import com.cuzssp.campussecondhandtradingplatform_backend.service.CategoryService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminService adminService;

    // 分类操作
    private final CategoryService categoryService;

    // 公告操作
    private final AnnouncementService announcementService;

    /**
     * 获取仪表盘
     * @return
     */
    @GetMapping("/dashboard")
    public Result<?> getDashboard() {
        return adminService.getDashboard();
    }

    // 用户管理
    /**
     * 添加管理员
     * @param request
     * @return
     */
    @PostMapping("/users/newadmin")
    public Result<?> addAdmin(
            @Valid @RequestBody RegisterRequest request
    ) {
        return adminService.addAdmin(request);
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

    // 商品管理
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
            @RequestParam Integer status
    ) {
        return adminService.updateProductStatus(id, status);
    }

    // 订单管理 仅查看
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

    // 分类管理
    /**
     * 创建分类信息
     * @param category
     * @return
     */
    @PostMapping("/category/add")
    public Result<?> createCategory(
            @RequestBody Category category
    ) {
        return categoryService.createCategory(category);
    }

    /**
     * 修改分类信息 基于 id
     * @param id
     * @param category
     * @return
     */
    @PutMapping("/category/upd/{id}")
    public Result<?> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category
    ) {
        return categoryService.updateCategory(id, category);
    }

    /**
     * 删除分类信息 基于 id
     * @param id
     * @return
     */
    @DeleteMapping("/category/del/{id}")
    public Result<?> deleteCategory(
            @PathVariable Long id
    ) {
        return categoryService.deleteCategory(id);
    }

    /**
     * 新增公告
     * @param announcementRequest
     * @return
     */
    @PostMapping("/announcement/add")
    public Result<?> createAnnouncement(
            @Valid @RequestBody AnnouncementRequest announcementRequest
    ) {
        return announcementService.createAnnouncement(announcementRequest);
    }

    /**
     * 修改公告
     * @param id
     * @param announcementRequest
     * @return
     */
    @PutMapping("/announcement/upd/{id}")
    public Result<?> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest announcementRequest
    ) {
        return announcementService.updateAnnouncement(id, announcementRequest);
    }

    /**
     * 删除公告
     * @param id
     * @return
     */
    @DeleteMapping("/announcement/del/{id}")
    public Result<?> deleteAnnouncement(
            @PathVariable Long id
    ) {
        return announcementService.removeAnnouncement(id);
    }



}
