-- ===================================================
-- 校园二手交易平台 - SQLite 初始化脚本
-- 数据库: cuzssp
-- ===================================================

PRAGMA foreign_keys = ON;

-- ---------------------------------------------------
-- 用户表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS user (
    id            BIGINT          NOT NULL,               -- 主键，雪花ID
    username      VARCHAR(32)     NOT NULL,               -- 用户名，唯一
    password      VARCHAR(128)    NOT NULL,               -- BCrypt加密密文
    nickname      VARCHAR(32)     DEFAULT NULL,           -- 昵称
    avatar        VARCHAR(255)    DEFAULT NULL,           -- 头像URL
    phone         VARCHAR(16)     DEFAULT NULL,           -- 手机号
    email         VARCHAR(64)     DEFAULT NULL,           -- 邮箱
    school        VARCHAR(64)     DEFAULT NULL,           -- 学校
    campus        VARCHAR(32)     DEFAULT NULL,           -- 校区
    role          TINYINT         NOT NULL DEFAULT 0,     -- 角色：0=用户 1=管理员
    status        TINYINT         NOT NULL DEFAULT 0,     -- 状态：0=正常 1=封禁
    credit_score  INTEGER         NOT NULL DEFAULT 100,   -- 信誉分
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 更新时间
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_username ON user (username);
CREATE INDEX IF NOT EXISTS idx_user_created_at ON user (created_at);

-- ---------------------------------------------------
-- 商品分类表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS category (
    id            BIGINT          NOT NULL,               -- 主键
    name          VARCHAR(32)     NOT NULL,               -- 分类名
    icon          VARCHAR(255)    DEFAULT NULL,           -- 分类图标URL
    sort_order    INTEGER         NOT NULL DEFAULT 0,     -- 排序
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    PRIMARY KEY (id)
);

-- ---------------------------------------------------
-- 商品表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS product (
    id             BIGINT          NOT NULL,              -- 主键
    user_id        BIGINT          NOT NULL,              -- 发布者ID
    category_id    BIGINT          NOT NULL,              -- 分类ID
    title          VARCHAR(128)    NOT NULL,              -- 标题
    description    TEXT            DEFAULT NULL,          -- 描述
    price          DECIMAL(10,2)   NOT NULL,              -- 价格
    original_price DECIMAL(10,2)   DEFAULT NULL,          -- 原价
    state          TINYINT         NOT NULL,              -- 新旧：1=全新 2=几乎全新 3=有使用痕迹
    campus         VARCHAR(32)     DEFAULT NULL,          -- 交易校区
    status         TINYINT         NOT NULL DEFAULT 0,    -- 0=待审核 1=在售 2=已售出 3=已下架
    view_count     INTEGER         NOT NULL DEFAULT 0,    -- 浏览量
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 发布时间
    updated_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 更新时间
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_product_user_id ON product (user_id);
CREATE INDEX IF NOT EXISTS idx_product_category_status ON product (category_id, status);
CREATE INDEX IF NOT EXISTS idx_product_created_at ON product (created_at);

-- ---------------------------------------------------
-- 商品图片表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS product_image (
    id            BIGINT          NOT NULL,               -- 主键
    product_id    BIGINT          NOT NULL,               -- 商品ID
    url           VARCHAR(255)    NOT NULL,               -- 图片URL
    sort_order    INTEGER         NOT NULL DEFAULT 1,     -- 排序（首图=1）
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_product_image_product_id ON product_image (product_id);

-- ---------------------------------------------------
-- 订单表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS order_info (
    id             BIGINT          NOT NULL,              -- 主键
    order_no       VARCHAR(32)     NOT NULL,              -- 订单编号
    buyer_id       BIGINT          NOT NULL,              -- 买家ID
    seller_id      BIGINT          NOT NULL,              -- 卖家ID
    total_amount   DECIMAL(10,2)   NOT NULL,              -- 总金额
    status         TINYINT         NOT NULL DEFAULT 0,    -- 0=待付款 1=待发货 2=待收货 3=已完成 4=已取消
    remark         VARCHAR(255)    DEFAULT NULL,          -- 备注
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 更新时间
    paid_at        DATETIME        DEFAULT NULL,          -- 付款时间
    shipped_at     DATETIME        DEFAULT NULL,          -- 发货时间
    completed_at   DATETIME        DEFAULT NULL,          -- 完成时间
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_order_info_order_no ON order_info (order_no);
CREATE INDEX IF NOT EXISTS idx_order_info_buyer_id ON order_info (buyer_id);
CREATE INDEX IF NOT EXISTS idx_order_info_seller_id ON order_info (seller_id);

-- ---------------------------------------------------
-- 订单明细表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS order_item (
    id            BIGINT          NOT NULL,               -- 主键
    order_id      BIGINT          NOT NULL,               -- 订单ID
    product_id    BIGINT          NOT NULL,               -- 商品ID
    price         DECIMAL(10,2)   NOT NULL,               -- 购买时价格
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_order_item_order_id ON order_item (order_id);

-- ---------------------------------------------------
-- 购物车表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS cart_item (
    id            BIGINT          NOT NULL,               -- 主键
    user_id       BIGINT          NOT NULL,               -- 用户ID
    product_id    BIGINT          NOT NULL,               -- 商品ID
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 添加时间
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_cart_item_user_id ON cart_item (user_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_cart_item_user_product ON cart_item (user_id, product_id);

-- ---------------------------------------------------
-- 收藏表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS favorite (
    id            BIGINT          NOT NULL,               -- 主键
    user_id       BIGINT          NOT NULL,               -- 用户ID
    product_id    BIGINT          NOT NULL,               -- 商品ID
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 收藏时间
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_favorite_user_id ON favorite (user_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_favorite_user_product ON favorite (user_id, product_id);

-- ---------------------------------------------------
-- 聊天消息表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS chat_message (
    id            BIGINT          NOT NULL,               -- 主键
    sender_id     BIGINT          NOT NULL,               -- 发送者ID
    receiver_id   BIGINT          NOT NULL,               -- 接收者ID
    product_id    BIGINT          DEFAULT NULL,           -- 关联商品ID
    content       TEXT            NOT NULL,               -- 消息内容（用户密钥加密）
    is_read       TINYINT         NOT NULL DEFAULT 0,     -- 0=未读 1=已读
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 发送时间
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_chat_sender_receiver ON chat_message (sender_id, receiver_id);
CREATE INDEX IF NOT EXISTS idx_chat_receiver_read ON chat_message (receiver_id, is_read);

-- ---------------------------------------------------
-- 评价表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS review (
    id            BIGINT          NOT NULL,               -- 主键
    order_id      BIGINT          NOT NULL,               -- 订单ID
    reviewer_id   BIGINT          NOT NULL,               -- 评价者ID
    target_id     BIGINT          NOT NULL,               -- 被评价者ID
    rating        TINYINT         NOT NULL,               -- 评分 1-5
    content       VARCHAR(500)    DEFAULT NULL,           -- 评价内容
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 评价时间
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_review_target_id ON review (target_id);
CREATE INDEX IF NOT EXISTS idx_review_order_reviewer ON review (order_id, reviewer_id);

-- ---------------------------------------------------
-- 系统公告表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS announcement (
    id            BIGINT          NOT NULL,               -- 主键
    title         VARCHAR(128)    NOT NULL,               -- 标题
    content       TEXT            NOT NULL,               -- 内容
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 更新时间
    PRIMARY KEY (id)
);

-- ===================================================
-- 可选：用触发器模拟 MySQL 的 ON UPDATE CURRENT_TIMESTAMP
-- ===================================================
CREATE TRIGGER IF NOT EXISTS trg_user_updated_at
AFTER UPDATE ON user
    FOR EACH ROW
    WHEN NEW.updated_at = OLD.updated_at
BEGIN
UPDATE user SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;

CREATE TRIGGER IF NOT EXISTS trg_product_updated_at
AFTER UPDATE ON product
    FOR EACH ROW
    WHEN NEW.updated_at = OLD.updated_at
BEGIN
UPDATE product SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;

CREATE TRIGGER IF NOT EXISTS trg_order_info_updated_at
AFTER UPDATE ON order_info
    FOR EACH ROW
    WHEN NEW.updated_at = OLD.updated_at
BEGIN
UPDATE order_info SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;

CREATE TRIGGER IF NOT EXISTS trg_announcement_updated_at
AFTER UPDATE ON announcement
    FOR EACH ROW
    WHEN NEW.updated_at = OLD.updated_at
BEGIN
UPDATE announcement SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;