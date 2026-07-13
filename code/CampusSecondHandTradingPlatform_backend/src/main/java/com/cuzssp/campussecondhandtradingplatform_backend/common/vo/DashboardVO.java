package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardVO {
    private Long userCount;
    private Long productCount;
    private Long orderCount;
    private BigDecimal totalAmount;
    private Long todayNewUsers;
    private Long todayNewOrders;
}
