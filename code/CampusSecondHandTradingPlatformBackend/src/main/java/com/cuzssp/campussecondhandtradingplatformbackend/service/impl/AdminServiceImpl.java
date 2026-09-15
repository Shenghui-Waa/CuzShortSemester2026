package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;


import com.cuzssp.campussecondhandtradingplatformbackend.mapper.OrderInfoMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.DashboardVO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final OrderInfoMapper orderInfoMapper;

    @Override
    public DashboardVO getDashboard() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        Long todayUsers = userMapper.countCreatedBetween(start, end);
        Long todayOrders = orderInfoMapper.countCreatedBetween(start, end);
        BigDecimal amount = orderInfoMapper.sumFulfilledAmount();
        return ToVOUtil.toDashboardVO(userMapper.selectCount(null), todayUsers,
                productMapper.selectCount(null), orderInfoMapper.selectCount(null),
                todayOrders, amount);
    }
}
