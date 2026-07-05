package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardVO {
    private Long userCount;
    private Long productCount;
    private Long orderCount;
    private BigDecimal totalAmount;
    private Long todayNewUsers;
    private Long todayNewOrders;
}
