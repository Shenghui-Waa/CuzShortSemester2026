package com.cuzssp.campussecondhandtradingplatform_backend.controller.admin;


import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AdminService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public Result<?> getUserList(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(required = false) String keyword) {
        return adminService.getUserList(page, pageSize, keyword);
    }

    @PutMapping("/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        return adminService.updateUserStatus(id, status);
    }
}
