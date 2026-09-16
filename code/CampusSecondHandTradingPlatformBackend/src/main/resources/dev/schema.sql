-- ===================================================
-- 校园二手交易平台 - SQLite 初始化脚本
-- 数据库: cuzssp
-- ===================================================

PRAGMA foreign_keys = ON;

-- ---------------------------------------------------
-- 用户表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS user (
    id            INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT, -- 主键，自增ID
    username      VARCHAR(32)     NOT NULL,               -- 用户名，唯一
    password      VARCHAR(128)    NOT NULL,               -- BCrypt加密密文
    nickname      VARCHAR(32)     DEFAULT NULL,           -- 昵称
    avatar        VARCHAR(255)    DEFAULT NULL,           -- 头像URL
    phone         VARCHAR(16)     DEFAULT NULL,           -- 手机号
    email         VARCHAR(64)     DEFAULT NULL,           -- 邮箱
    school        VARCHAR(64)     DEFAULT NULL,           -- 学校
    campus        VARCHAR(32)     DEFAULT NULL,           -- 校区
    role          TINYINT         NOT NULL DEFAULT 0,     -- 角色：0=用户 1=管理员
    status        TINYINT         NOT NULL DEFAULT 1,     -- 状态：0=封禁 1=正常
    credit_score  INTEGER         NOT NULL DEFAULT 100,   -- 信誉分
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP   -- 更新时间
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_username ON user (username);
CREATE INDEX IF NOT EXISTS idx_user_created_at ON user (created_at);

-- ---------------------------------------------------
-- 商品分类表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS category (
    id            INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT, -- 主键，自增ID
    name          VARCHAR(32)     NOT NULL,               -- 分类名
    icon          VARCHAR(255)    DEFAULT NULL,           -- 分类图标URL
    sort_order    INTEGER         NOT NULL DEFAULT 0,     -- 排序
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP   -- 创建时间
);

-- ---------------------------------------------------
-- 商品表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS product (
    id             INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT, -- 主键，自增ID
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
    is_deleted     INTEGER         NOT NULL DEFAULT 0,    -- 0=未删除 1=已删除
    deleted_at     DATETIME        DEFAULT NULL,          -- 删除时间
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 发布时间
    updated_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 更新时间
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE INDEX IF NOT EXISTS idx_product_user_id ON product (user_id);
CREATE INDEX IF NOT EXISTS idx_product_category_status ON product (category_id, status);
CREATE INDEX IF NOT EXISTS idx_product_created_at ON product (created_at);

-- ---------------------------------------------------
-- 商品图片表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS product_image (
    id            INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT, -- 主键，自增ID
    product_id    BIGINT          NOT NULL,               -- 商品ID
    url           VARCHAR(255)    NOT NULL,               -- 图片URL
    sort_order    INTEGER         NOT NULL DEFAULT 1,     -- 排序（首图=1）
    FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE INDEX IF NOT EXISTS idx_product_image_product_id ON product_image (product_id);

-- ---------------------------------------------------
-- 订单表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS order_info (
    id             INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT, -- 主键，自增ID
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
    refund_status  INTEGER         NOT NULL DEFAULT 0,    -- 0=未退款/无需退款 1=已退款
    refunded_at    DATETIME        DEFAULT NULL,          -- 退款时间
    FOREIGN KEY (buyer_id) REFERENCES user (id),
    FOREIGN KEY (seller_id) REFERENCES user (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_order_info_order_no ON order_info (order_no);
CREATE INDEX IF NOT EXISTS idx_order_info_buyer_id ON order_info (buyer_id);
CREATE INDEX IF NOT EXISTS idx_order_info_seller_id ON order_info (seller_id);

-- ---------------------------------------------------
-- 订单明细表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS order_item (
    id            INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT, -- 主键，自增ID
    order_id      BIGINT          NOT NULL,               -- 订单ID
    product_id    BIGINT          NOT NULL,               -- 商品ID
    price         DECIMAL(10,2)   NOT NULL,               -- 购买时价格
    product_title VARCHAR(128)    NOT NULL,               -- 商品标题快照
    product_image VARCHAR(255)    DEFAULT NULL,           -- 商品首图快照
    product_state TINYINT         NOT NULL,               -- 商品成色快照
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    FOREIGN KEY (order_id) REFERENCES order_info (id),
    FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE INDEX IF NOT EXISTS idx_order_item_order_id ON order_item (order_id);
CREATE INDEX IF NOT EXISTS idx_order_item_product_id ON order_item (product_id);

-- ---------------------------------------------------
-- 购物车表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS cart_item (
    id            INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT, -- 主键，自增ID
    user_id       BIGINT          NOT NULL,               -- 用户ID
    product_id    BIGINT          NOT NULL,               -- 商品ID
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 添加时间
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE INDEX IF NOT EXISTS idx_cart_item_user_id ON cart_item (user_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_cart_item_user_product ON cart_item (user_id, product_id);
CREATE INDEX IF NOT EXISTS idx_cart_item_product_id ON cart_item (product_id);

-- ---------------------------------------------------
-- 收藏表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS favorite (
    id            INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT, -- 主键，自增ID
    user_id       BIGINT          NOT NULL,               -- 用户ID
    product_id    BIGINT          NOT NULL,               -- 商品ID
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 收藏时间
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE INDEX IF NOT EXISTS idx_favorite_user_id ON favorite (user_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_favorite_user_product ON favorite (user_id, product_id);
CREATE INDEX IF NOT EXISTS idx_favorite_product_id ON favorite (product_id);

-- ---------------------------------------------------
-- 聊天消息表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS chat_message (
    id            INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT, -- 主键，自增ID
    sender_id     BIGINT          NOT NULL,               -- 发送者ID
    receiver_id   BIGINT          NOT NULL,               -- 接收者ID
    product_id    BIGINT          DEFAULT NULL,           -- 关联商品ID
    content       TEXT            NOT NULL,               -- 消息内容（用户密钥加密）
    is_read       TINYINT         NOT NULL DEFAULT 0,     -- 0=未读 1=已读
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 发送时间
    FOREIGN KEY (sender_id) REFERENCES user (id),
    FOREIGN KEY (receiver_id) REFERENCES user (id),
    FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE INDEX IF NOT EXISTS idx_chat_sender_receiver ON chat_message (sender_id, receiver_id);
CREATE INDEX IF NOT EXISTS idx_chat_receiver_read ON chat_message (receiver_id, is_read);
CREATE INDEX IF NOT EXISTS idx_chat_product_id ON chat_message (product_id);

-- ---------------------------------------------------
-- 评价表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS review (
    id            INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT, -- 主键，自增ID
    order_id      BIGINT          NOT NULL,               -- 订单ID
    reviewer_id   BIGINT          NOT NULL,               -- 评价者ID
    target_id     BIGINT          NOT NULL,               -- 被评价者ID
    rating        TINYINT         NOT NULL,               -- 评分 1-5
    content       VARCHAR(500)    DEFAULT NULL,           -- 评价内容
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 评价时间
    FOREIGN KEY (order_id) REFERENCES order_info (id),
    FOREIGN KEY (reviewer_id) REFERENCES user (id),
    FOREIGN KEY (target_id) REFERENCES user (id)
);

CREATE INDEX IF NOT EXISTS idx_review_target_id ON review (target_id);
CREATE INDEX IF NOT EXISTS idx_review_order_reviewer ON review (order_id, reviewer_id);

-- ---------------------------------------------------
-- 系统公告表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS announcement (
    id            INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT, -- 主键，自增ID
    title         VARCHAR(128)    NOT NULL,               -- 标题
    content       TEXT            NOT NULL,               -- 内容
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP   -- 更新时间
);

-- ---------------------------------------------------
-- JWT 撤销表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS revoked_token (
    jti            VARCHAR(64)     NOT NULL PRIMARY KEY,  -- JWT唯一标识
    expires_at     DATETIME        NOT NULL,              -- Token过期时间
    revoked_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP -- 撤销时间
);

CREATE INDEX IF NOT EXISTS idx_revoked_token_expires_at
    ON revoked_token (expires_at);

