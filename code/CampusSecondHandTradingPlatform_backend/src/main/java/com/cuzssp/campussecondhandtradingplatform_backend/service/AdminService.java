package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;

public interface AdminService {
    Result<DashboardVO> getDashboard();

    Result<PageResult<UserVO>> getUserList(Integer page, Integer pageSize, String keyword);
    Result<Void> updateUserStatus(Long userId, Integer status);

    Result<PageResult<ProductVO>> getProductList(Integer page, Integer pageSize, String keyword, Integer status);
    Result<Void> updateProductStatus(Long productId, Integer status);

    Result<PageResult<OrderVO>> getOrders(Integer page, Integer pageSize, Integer status);


}
