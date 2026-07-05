package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Announcement;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.DashboardVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;

public interface AdminService {
    Result<DashboardVO> getDashboard();
    Result<PageResult<UserVO>> getUserList(Integer page, Integer pageSize, String keyword);
    Result<Void> updateUserStatus(Long userId, Integer status);
    Result<Void> auditProduct(Long productId, Integer status);
}
