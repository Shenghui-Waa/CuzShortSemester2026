-- ===================================================
-- 校园二手交易平台 - 数据库初始化脚本
-- 数据库: cuzssp
-- 字符集: utf8mb4
-- ===================================================

CREATE DATABASE IF NOT EXISTS cuzssp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cuzssp;

-- ---------------------------------------------------
-- 用户表
-- ---------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`            BIGINT          NOT NULL COMMENT '主键，雪花ID',
    `username`      VARCHAR(32)     NOT NULL COMMENT '用户名，唯一',
    `password`      VARCHAR(128)    NOT NULL COMMENT 'BCrypt加密密文',
    `nickname`      VARCHAR(32)     DEFAULT NULL COMMENT '昵称',
    `avatar`        VARCHAR(255)    DEFAULT NULL COMMENT '头像URL',
    `phone`         VARCHAR(16)     DEFAULT NULL COMMENT '手机号',
    `email`         VARCHAR(64)     DEFAULT NULL COMMENT '邮箱',
    `school`        VARCHAR(64)     DEFAULT NULL COMMENT '学校',
    `campus`        VARCHAR(32)     DEFAULT NULL COMMENT '校区',
    `role`          TINYINT         NOT NULL DEFAULT 0 COMMENT '角色：0=用户 1=管理员',
    `status`        TINYINT         NOT NULL DEFAULT 0 COMMENT '状态：0=正常 1=封禁',
    `credit_score`  INT             NOT NULL DEFAULT 100 COMMENT '信誉分',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ---------------------------------------------------
-- 商品分类表
-- ---------------------------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`            BIGINT          NOT NULL COMMENT '主键',
    `name`          VARCHAR(32)     NOT NULL COMMENT '分类名',
    `icon`          VARCHAR(255)    DEFAULT NULL COMMENT '分类图标URL',
    `sort_order`    INT             NOT NULL DEFAULT 0 COMMENT '排序',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- ---------------------------------------------------
-- 商品表
-- ---------------------------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`             BIGINT          NOT NULL COMMENT '主键',
    `user_id`        BIGINT          NOT NULL COMMENT '发布者ID',
    `category_id`    BIGINT          NOT NULL COMMENT '分类ID',
    `title`          VARCHAR(128)    NOT NULL COMMENT '标题',
    `description`    TEXT            DEFAULT NULL COMMENT '描述',
    `price`          DECIMAL(10,2)   NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10,2)   DEFAULT NULL COMMENT '原价',
    `condition`      TINYINT         NOT NULL COMMENT '新旧：1=全新 2=几乎全新 3=有使用痕迹',
    `campus`         VARCHAR(32)     DEFAULT NULL COMMENT '交易校区',
    `status`         TINYINT         NOT NULL DEFAULT 0 COMMENT '0=待审核 1=在售 2=已售出 3=已下架',
    `view_count`     INT             NOT NULL DEFAULT 0 COMMENT '浏览量',
    `created_at`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `updated_at`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_status` (`category_id`, `status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ---------------------------------------------------
-- 商品图片表
-- ---------------------------------------------------
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image` (
    `id`            BIGINT          NOT NULL COMMENT '主键',
    `product_id`    BIGINT          NOT NULL COMMENT '商品ID',
    `url`           VARCHAR(255)    NOT NULL COMMENT '图片URL',
    `sort_order`    INT             NOT NULL DEFAULT 1 COMMENT '排序（首图=1）',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- ---------------------------------------------------
-- 订单表
-- ---------------------------------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id`             BIGINT          NOT NULL COMMENT '主键',
    `order_no`       VARCHAR(32)     NOT NULL COMMENT '订单编号',
    `buyer_id`       BIGINT          NOT NULL COMMENT '买家ID',
    `seller_id`      BIGINT          NOT NULL COMMENT '卖家ID',
    `total_amount`   DECIMAL(10,2)   NOT NULL COMMENT '总金额',
    `status`         TINYINT         NOT NULL DEFAULT 0 COMMENT '0=待付款 1=待发货 2=待收货 3=已完成 4=已取消',
    `remark`         VARCHAR(255)    DEFAULT NULL COMMENT '备注',
    `created_at`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `paid_at`        DATETIME        DEFAULT NULL COMMENT '付款时间',
    `shipped_at`     DATETIME        DEFAULT NULL COMMENT '发货时间',
    `completed_at`   DATETIME        DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer_status` (`buyer_id`, `status`),
    KEY `idx_seller_status` (`seller_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ---------------------------------------------------
-- 订单明细表
-- ---------------------------------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id`            BIGINT          NOT NULL COMMENT '主键',
    `order_id`      BIGINT          NOT NULL COMMENT '订单ID',
    `product_id`    BIGINT          NOT NULL COMMENT '商品ID',
    `price`         DECIMAL(10,2)   NOT NULL COMMENT '成交单价',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- ---------------------------------------------------
-- 购物车表
-- ---------------------------------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id`            BIGINT          NOT NULL COMMENT '主键',
    `user_id`       BIGINT          NOT NULL COMMENT '用户ID',
    `product_id`    BIGINT          NOT NULL COMMENT '商品ID',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- ---------------------------------------------------
-- 收藏表
-- ---------------------------------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id`            BIGINT          NOT NULL COMMENT '主键',
    `user_id`       BIGINT          NOT NULL COMMENT '用户ID',
    `product_id`    BIGINT          NOT NULL COMMENT '商品ID',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ---------------------------------------------------
-- 聊天消息表
-- ---------------------------------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
    `id`            BIGINT          NOT NULL COMMENT '主键',
    `sender_id`     BIGINT          NOT NULL COMMENT '发送者ID',
    `receiver_id`   BIGINT          NOT NULL COMMENT '接收者ID',
    `product_id`    BIGINT          DEFAULT NULL COMMENT '关联商品ID',
    `content`       TEXT            NOT NULL COMMENT '消息内容',
    `is_read`       TINYINT         NOT NULL DEFAULT 0 COMMENT '0=未读 1=已读',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    KEY `idx_sender_receiver` (`sender_id`, `receiver_id`),
    KEY `idx_receiver_read` (`receiver_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- ---------------------------------------------------
-- 评价表
-- ---------------------------------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
    `id`            BIGINT          NOT NULL COMMENT '主键',
    `order_id`      BIGINT          NOT NULL COMMENT '订单ID',
    `reviewer_id`   BIGINT          NOT NULL COMMENT '评价者ID',
    `target_id`     BIGINT          NOT NULL COMMENT '被评价者ID',
    `rating`        TINYINT         NOT NULL COMMENT '评分 1-5',
    `content`       VARCHAR(500)    DEFAULT NULL COMMENT '评价内容',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    PRIMARY KEY (`id`),
    KEY `idx_target_id` (`target_id`),
    KEY `idx_order_reviewer` (`order_id`, `reviewer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- ---------------------------------------------------
-- 系统公告表
-- ---------------------------------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
    `id`            BIGINT          NOT NULL COMMENT '主键',
    `title`         VARCHAR(128)    NOT NULL COMMENT '标题',
    `content`       TEXT            NOT NULL COMMENT '内容',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统公告表';

-- ---------------------------------------------------
-- 初始数据：商品分类
-- ---------------------------------------------------
INSERT INTO `category` (`id`, `name`, `icon`, `sort_order`) VALUES
(1, '教材教辅', '', 1),
(2, '数码产品', '', 2),
(3, '生活用品', '', 3),
(4, '服饰鞋包', '', 4),
(5, '运动户外', '', 5),
(6, '美妆护肤', '', 6),
(7, '图书文具', '', 7),
(8, '其他', '', 8);

-- ---------------------------------------------------
-- 初始数据：管理员账号
-- 默认密码: admin123（BCrypt加密）
-- 首次部署后请立即修改密码！
-- ---------------------------------------------------
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `role`, `status`) VALUES
(1, 'admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '系统管理员', 1, 0);
