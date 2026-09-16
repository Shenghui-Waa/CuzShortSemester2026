package com.cuzssp.campussecondhandtradingplatformbackend.common.constant;

/*
  订单状态信息常量
 */
public class OrderInfoConstant {

    public static class Status {

        public static final int WAIT_PAY = 0;    // 待支付
        public static final int WAIT_DELIVER = 1;// 待发货
        public static final int WAIT_RECEIVE = 2;// 待收货
        public static final int COMPLETED = 3;   // 已完成
        public static final int CANCELLED = 4;   // 已取消

    }

    public static class RefundStatus {

        public static final int NONE = 0;      // 未退款或无需退款
        public static final int REFUNDED = 1;  // 已退款

    }

}
