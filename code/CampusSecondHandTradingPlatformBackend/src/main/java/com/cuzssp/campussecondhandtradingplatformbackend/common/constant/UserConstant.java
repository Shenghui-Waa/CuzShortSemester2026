package com.cuzssp.campussecondhandtradingplatformbackend.common.constant;

/*
  用户相关常量
 */
public class UserConstant {

    public static class Role {

        public static final int USER = 0;   // 用户
        public static final int ADMIN = 1;  // 管理员

    }

    public static class Status {

        public static final int ACTIVE = 1;     // 正常
        public static final int INACTIVE = 0;   // 封禁 ;

    }

    public static final int CREDIT_SCORE_DEFAULT = 100; // 默认信誉分

}
