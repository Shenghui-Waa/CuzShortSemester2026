package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.OrderInfo;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.OrderMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AdminService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.DashboardVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired private UserMapper userMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private OrderMapper orderMapper;

    @Override
    public Result<DashboardVO> getDashboard() {
        DashboardVO vo = new DashboardVO();
        vo.setUserCount((long) userMapper.countAll());
        vo.setProductCount((long) productMapper.countAll());
        vo.setOrderCount((long) orderMapper.countAll());
        BigDecimal revenue = orderMapper.selectAll().stream()
                .filter(o -> o.getStatus() >= 2).map(OrderInfo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalAmount(revenue);
        vo.setTodayNewUsers((long) userMapper.countTodayNew());
        vo.setTodayNewOrders((long) orderMapper.countTodayNew());
        return Result.success(vo);
    }

    @Override
    public Result<PageResult<UserVO>> getUserList(Integer page, Integer pageSize, String keyword) {
        PageHelper.startPage(page, pageSize);
        List<User> users = (keyword != null && !keyword.isEmpty())
                ? userMapper.selectAll().stream().filter(u ->
                    u.getUsername().contains(keyword) || u.getNickname().contains(keyword))
                    .collect(Collectors.toList())
                : userMapper.selectAll();
        PageInfo<User> pgInfo = new PageInfo<>(users);
        List<UserVO> vos = users.stream().map(u -> {
            UserVO vo = new UserVO();
            vo.setId(u.getId()); vo.setUsername(u.getUsername()); vo.setNickname(u.getNickname());
            vo.setAvatar(u.getAvatar()); vo.setPhone(u.getPhone()); vo.setEmail(u.getEmail());
            vo.setSchool(u.getSchool()); vo.setCampus(u.getCampus()); vo.setRole(u.getRole());
            vo.setStatus(u.getStatus()); vo.setCreditScore(u.getCreditScore()); vo.setCreatedAt(u.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
        return Result.success(new PageResult<>(vos, pgInfo.getTotal(), pgInfo.getPageNum(), pgInfo.getPageSize()));
    }

    @Override
    public Result<Void> updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user != null) { user.setStatus(status); userMapper.updateById(user); }
        return Result.success();
    }

    @Override
    public Result<Void> auditProduct(Long productId, Integer status) {
        Product product = productMapper.selectById(productId);
        if (product != null) { product.setStatus(status); productMapper.updateById(product); }
        return Result.success();
    }
}
