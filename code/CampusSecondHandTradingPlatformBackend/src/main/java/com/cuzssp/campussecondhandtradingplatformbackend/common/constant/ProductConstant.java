package com.cuzssp.campussecondhandtradingplatformbackend.common.constant;

/*
  商品相关常量
 */
public class ProductConstant {

    public static class State {

        public static final int S_NEW = 1;  // 全新
        public static final int A_NEW = 2;  // 几乎全新
        public static final int B_NEW = 3;  // 有使用痕迹

    }

    public static class Status {

        public static final int NEED_CHECK = 0; // 待审核
        public static final int ON_SALE = 1;    // 已上架、在售
        public static final int SOLD_OUT = 2;   // 已售出、售罄
        public static final int DISABLE = 3;    // 已下架

    }

    public static final int VIEW_COUNT_DEFAULT = 0; // 初始浏览人数
    public static final int DEFAULT_DELETED = 0;    // 默认未删除
    public static final int DELETED = 1;            // 已删除

}
