package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.service.*;
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

}
