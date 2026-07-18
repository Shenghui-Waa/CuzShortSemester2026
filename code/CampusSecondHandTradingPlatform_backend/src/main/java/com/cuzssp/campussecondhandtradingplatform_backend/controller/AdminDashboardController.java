package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.AnnouncementRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminService adminService;
    private final UserService userService;  // 账户操作 用户管理
    private final ProductService productService;    // 商品操作 商品管理
    private final OrderService orderService;    // 订单查验 订单管理
    private final CategoryService categoryService;  // 分类操作 分类管理
    private final AnnouncementService announcementService;  // 公告操作 公告管理

    /**
     * 获取仪表盘
     * @return 仪表盘信息
     */
    @GetMapping("/dashboard")
    public Result<?> getDashboard() {
        return adminService.getDashboard();
    }

    // 用户管理
    /**
     * 添加管理员
     * @param request 管理员信息注册请求体
     * @return 管理员用户信息
     */
    @PostMapping("/users/newadmin")
    public Result<?> addAdmin(
            @Valid @RequestBody RegisterRequest request
    ) {
        return userService.addAdmin(request);
    }

    /**
     * 获取用户列表
     * @param page 页码
     * @param pageSize 页大小
     * @param keyword 关键词
     * @return 满足 keyword 的用户列表
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
     * 修改用户状态
     * @param id 用户 ID
     * @param status 用户状态 0=正常 1=封禁
     * @return 成功则空数据返回
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
     * @param page 页码
     * @param pageSize 页大小
     * @param keyword 关键词
     * @param status 商品状态 0=待审核 1=在售 2=已售出 3=已下架
     * @return 满足 keyword & status 的商品列表
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
     * @param id 商品 ID
     * @param status 商品状态 0=待审核 1=在售 2=已售出 3=已下架
     * @return 成功则空数据返回
     */
    @PutMapping("/products/{id}/status")
    public Result<?> updateProductStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        return productService.updateProductStatus(id, status);
    }

    // 订单管理 仅查看
    /**
     * 获取订单表
     * @param page 页码
     * @param pageSize 页大小
     * @param status 订单状态 0=待付款 1=待发货 2=待收货 3=已完成 4=已取消
     * @return 满足 status 的订单列表
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
     * @param category 类别信息实体
     * @return 类别信息
     */
    @PostMapping("/category/add")
    public Result<?> createCategory(
            @RequestBody Category category
    ) {
        return categoryService.createCategory(category);
    }

    /**
     * 修改类别信息 基于 id
     * @param id 类别 ID
     * @param category 类别信息实体
     * @return 类别信息
     */
    @PutMapping("/category/upd/{id}")
    public Result<?> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category
    ) {
        return categoryService.updateCategory(id, category);
    }

    /**
     * 删除类别 基于 id
     * @param id 类别 ID
     * @return 成功则空数据返回
     */
    @DeleteMapping("/category/del/{id}")
    public Result<?> deleteCategory(
            @PathVariable Long id
    ) {
        return categoryService.deleteCategory(id);
    }

    // 公告管理
    /**
     * 新增公告
     * @param announcementRequest 公告信息请求体
     * @return 公告信息
     */
    @PostMapping("/announcement/add")
    public Result<?> createAnnouncement(
            @Valid @RequestBody AnnouncementRequest announcementRequest
    ) {
        return announcementService.createAnnouncement(announcementRequest);
    }

    /**
     * 修改公告
     * @param id 公告 ID
     * @param announcementRequest 公告信息请求体
     * @return 公告信息
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
     * @param id 公告 ID
     * @return 成功则空数据返回
     */
    @DeleteMapping("/announcement/del/{id}")
    public Result<?> deleteAnnouncement(
            @PathVariable Long id
    ) {
        return announcementService.removeAnnouncement(id);
    }



}
