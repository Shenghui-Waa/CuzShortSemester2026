# 校园二手交易平台 — 开发说明文档 (DEV.md)

> **版本**: v1.0  
> **日期**: 2026-07-05  
> **项目名**: CuzShortSemester2026 — Campus Second-Hand Trading Platform

---

## 1. 项目概述

本项目是一个面向高校学生的校园二手物品交易 Web 应用，旨在为校内用户提供一个安全、便捷的二手商品发布、浏览、交易与交流平台。系统分为前台用户端和后台管理端，支持商品分类浏览、搜索筛选、在线聊天、订单管理、评价系统等核心功能。

---

## 2. 技术栈

| 层级   | 技术选择                          | 版本       |
|--------|-----------------------------------|------------|
| 后端   | Spring Boot                       | 4.x        |
| 后端   | Spring Security + JWT             | —          |
| 后端   | MyBatis-Plus                      | 3.5+       |
| 后端   | MySQL Driver (Connector/J)        | 8.x        |
| 数据库 | MySQL                             | 8.0+       |
| 缓存   | Redis                             | 7.x（可选） |
| 前端   | Vue                               | 3.4+       |
| 前端   | Vue Router                        | 4.x        |
| 前端   | Pinia (状态管理)                  | 2.x        |
| 前端   | Element Plus (UI组件库)           | 2.x        |
| 前端   | Axios                             | 1.x        |
| 构建   | Vite                              | 5.x        |
| 包管理 | pnpm / npm                        | 最新       |
| 对象存储 | Cloudflare R2 (S3 Compatible API) | —          |
| 后端   | AWS SDK for Java (S3)            | 2.x        |

---

## 3. 需求分析

### 3.1 用户角色

| 角色         | 描述                                 |
|--------------|--------------------------------------|
| 普通用户     | 注册登录、浏览商品、发布商品、下单购买、聊天 |
| 管理员       | 用户管理、商品审核、订单管理、系统配置     |

### 3.2 功能模块

#### 前台用户端

| 模块       | 功能点                                                                 |
|------------|------------------------------------------------------------------------|
| 用户系统   | 注册、登录、退出、个人信息编辑、头像上传、密码修改、手机/邮箱绑定       |
| 商品浏览   | 首页推荐、分类浏览、关键词搜索、条件筛选（价格/新旧/校区）、商品详情     |
| 商品发布   | 发布商品（标题/描述/图片/价格/分类/新旧程度）、编辑、下架、已发布列表     |
| 收藏系统   | 收藏/取消收藏商品、收藏列表                                             |
| 订单系统   | 下单购买、订单列表（待付款/待发货/待收货/已完成/已取消）、取消订单       |
| 聊天系统   | 买卖双方在线聊天（WebSocket）、消息列表、未读提醒                       |
| 评价系统   | 交易完成后互评（星级 + 文字）、查看用户信誉                             |

#### 后台管理端

| 模块       | 功能点                                                                 |
|------------|------------------------------------------------------------------------|
| 仪表盘     | 用户数、商品数、订单数、交易额统计、图表展示                            |
| 用户管理   | 用户列表、封禁/解封、信息查看                                           |
| 商品管理   | 商品列表、审核通过/驳回、违规下架                                       |
| 订单管理   | 订单列表、退款处理、订单状态查看                                         |
| 分类管理   | 商品分类增删改查                                                       |
| 公告管理   | 系统公告发布、管理                                                     |

### 3.3 非功能性需求

- **安全性**: 密码加密存储（BCrypt），JWT 令牌鉴权，SQL 注入防护，XSS 防护
- **性能**: 首页首屏加载 < 3s，列表分页查询 < 500ms
- **可用性**: 支持 500+ 并发用户
- **可维护性**: 前后端分离，RESTful API，模块化组件
- **图片存储**: Cloudflare R2（S3兼容对象存储，开发与生产统一方案）

---

## 4. 项目目录结构

```
CuzShortSemester2026/
├── code/
│   ├── DEV.md                          # 本开发说明文档
│   ├── backend/                        # Spring Boot 后端
│   │   ├── pom.xml                     # Maven 配置
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/cuzssp/campussecondhandtradingplatform_backend/
│   │   │   │   │   ├── CampusSecondHandTradingPlatformBackendApplication.java        # 启动类
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── AuthController.java          # 认证接口
│   │   │   │   │   │   ├── UserController.java          # 用户接口
│   │   │   │   │   │   ├── ProductController.java       # 商品接口
│   │   │   │   │   │   ├── CategoryController.java      # 分类接口
│   │   │   │   │   │   ├── OrderController.java         # 订单接口
│   │   │   │   │   │   ├── CartController.java          # 购物车接口
│   │   │   │   │   │   ├── FavoriteController.java      # 收藏接口
│   │   │   │   │   │   ├── ChatController.java          # 聊天接口
│   │   │   │   │   │   ├── ReviewController.java        # 评价接口
│   │   │   │   │   │   ├── FileController.java          # 文件上传接口
│   │   │   │   │   │   └── admin/
│   │   │   │   │   │       ├── AdminUserController.java
│   │   │   │   │   │       ├── AdminProductController.java
│   │   │   │   │   │       ├── AdminOrderController.java
│   │   │   │   │   │       └── AdminDashboardController.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── AuthService.java
│   │   │   │   │   │   ├── UserService.java
│   │   │   │   │   │   ├── ProductService.java
│   │   │   │   │   │   ├── CategoryService.java
│   │   │   │   │   │   ├── OrderService.java
│   │   │   │   │   │   ├── CartService.java
│   │   │   │   │   │   ├── FavoriteService.java
│   │   │   │   │   │   ├── ChatService.java
│   │   │   │   │   │   ├── ReviewService.java
│   │   │   │   │   │   ├── FileService.java
│   │   │   │   │   │   └── impl/
│   │   │   │   │   │       ├── AuthServiceImpl.java
│   │   │   │   │   │       ├── UserServiceImpl.java
│   │   │   │   │   │       ├── ProductServiceImpl.java
│   │   │   │   │   │       ├── CategoryServiceImpl.java
│   │   │   │   │   │       ├── OrderServiceImpl.java
│   │   │   │   │   │       ├── CartServiceImpl.java
│   │   │   │   │   │       ├── FavoriteServiceImpl.java
│   │   │   │   │   │       ├── ChatServiceImpl.java
│   │   │   │   │   │       ├── ReviewServiceImpl.java
│   │   │   │   │   │       └── FileServiceImpl.java
│   │   │   │   │   ├── mapper/
│   │   │   │   │   │   ├── UserMapper.java
│   │   │   │   │   │   ├── ProductMapper.java
│   │   │   │   │   │   ├── ProductImageMapper.java
│   │   │   │   │   │   ├── CategoryMapper.java
│   │   │   │   │   │   ├── OrderMapper.java
│   │   │   │   │   │   ├── OrderItemMapper.java
│   │   │   │   │   │   ├── CartMapper.java
│   │   │   │   │   │   ├── FavoriteMapper.java
│   │   │   │   │   │   ├── ChatMessageMapper.java
│   │   │   │   │   │   └── ReviewMapper.java
│   │   │   │   │   └── common/
│   │   │   │   │       ├── pojo/
│   │   │   │   │       │   ├── User.java
│   │   │   │   │       │   ├── Product.java
│   │   │   │   │       │   ├── ProductImage.java
│   │   │   │   │       │   ├── Category.java
│   │   │   │   │       │   ├── Order.java
│   │   │   │   │       │   ├── OrderItem.java
│   │   │   │   │       │   ├── Cart.java
│   │   │   │   │       │   ├── Favorite.java
│   │   │   │   │       │   ├── ChatMessage.java
│   │   │   │   │       │   └── Review.java
│   │   │   │   │       ├── dto/
│   │   │   │   │       │   ├── request/
│   │   │   │   │       │   │   ├── LoginRequest.java
│   │   │   │   │       │   │   ├── RegisterRequest.java
│   │   │   │   │       │   │   ├── ProductRequest.java
│   │   │   │   │       │   │   └── OrderRequest.java
│   │   │   │   │       │   └── response/
│   │   │   │   │       │       ├── ApiResponse.java
│   │   │   │   │       │       ├── PageResponse.java
│   │   │   │   │       │       ├── LoginResponse.java
│   │   │   │   │       │       └── ProductVO.java
│   │   │   │   │       ├── config/
│   │   │   │   │       │   ├── SecurityConfig.java
│   │   │   │   │       │   ├── JwtConfig.java
│   │   │   │   │       │   ├── CorsConfig.java
│   │   │   │   │       │   ├── MyBatisPlusConfig.java
│   │   │   │   │       │   ├── WebSocketConfig.java
│   │   │   │   │       │   ├── FileUploadConfig.java
│   │   │   │   │       │   └── R2Config.java
│   │   │   │   │       ├── security/
│   │   │   │   │       │   ├── JwtTokenProvider.java
│   │   │   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │   │   │       │   └── UserDetailsServiceImpl.java
│   │   │   │   │       ├── exception/
│   │   │   │   │       │   ├── BusinessException.java
│   │   │   │   │       │   └── GlobalExceptionHandler.java
│   │   │   │   │       └── util/
│   │   │   │   │           ├── FileUtil.java
│   │   │   │   │           └── SnowflakeIdUtil.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       ├── application-dev.yml
│   │   │   │       ├── application-prod.yml
│   │   │   │       └── mapper/
│   │   │   └── test/
│   │   └── uploads/
│   │
│   └── frontend/                        # Vue 3 前端
│       ├── package.json
│       ├── vite.config.ts
│       ├── index.html
│       ├── tsconfig.json
│       ├── src/
│       │   ├── main.ts                  # 入口
│       │   ├── App.vue                  # 根组件
│       │   ├── router/
│       │   │   └── index.ts             # 路由配置
│       │   ├── stores/                  # Pinia 状态管理
│       │   │   ├── user.ts              # 用户状态
│       │   │   ├── cart.ts              # 购物车状态
│       │   │   └── chat.ts              # 聊天状态
│       │   ├── api/                     # 接口请求层
│       │   │   ├── request.ts           # Axios 封装 + 拦截器
│       │   │   ├── auth.ts
│       │   │   ├── user.ts
│       │   │   ├── product.ts
│       │   │   ├── category.ts
│       │   │   ├── order.ts
│       │   │   ├── cart.ts
│       │   │   ├── favorite.ts
│       │   │   ├── chat.ts
│       │   │   └── review.ts
│       │   ├── views/                   # 页面视图
│       │   │   ├── HomeView.vue         # 首页
│       │   │   ├── LoginView.vue        # 登录
│       │   │   ├── RegisterView.vue     # 注册
│       │   │   ├── product/
│       │   │   │   ├── ProductList.vue  # 商品列表
│       │   │   │   ├── ProductDetail.vue# 商品详情
│       │   │   │   └── ProductPublish.vue# 发布商品
│       │   │   ├── order/
│       │   │   │   ├── OrderList.vue    # 订单列表
│       │   │   │   └── OrderDetail.vue  # 订单详情
│       │   │   ├── user/
│       │   │   │   ├── ProfileView.vue  # 个人主页
│       │   │   │   └── MyProducts.vue   # 我的发布
│       │   │   ├── chat/
│       │   │   │   └── ChatView.vue     # 聊天页
│       │   │   ├── favorite/
│       │   │   │   └── FavoriteList.vue # 收藏列表
│       │   │   └── admin/              # 后台管理
│       │   │       ├── AdminLayout.vue
│       │   │       ├── DashboardView.vue
│       │   │       ├── UserManage.vue
│       │   │       ├── ProductManage.vue
│       │   │       └── OrderManage.vue
│       │   ├── components/             # 公共组件
│       │   │   ├── layout/
│       │   │   │   ├── AppHeader.vue    # 顶部导航
│       │   │   │   └── AppFooter.vue    # 底部
│       │   │   ├── ProductCard.vue      # 商品卡片
│       │   │   ├── SearchBar.vue        # 搜索栏
│       │   │   ├── ImageUpload.vue      # 图片上传
│       │   │   └── Pagination.vue       # 分页器
│       │   ├── utils/
│       │   │   ├── index.ts             # 通用工具函数
│       │   │   └── validators.ts        # 表单校验
│       │   └── styles/
│       │       ├── variables.scss       # SCSS 变量
│       │       └── global.scss          # 全局样式
│       └── public/
│           └── favicon.ico
│
└── docs/
    └── sql/
        └── init.sql                     # 数据库初始化脚本
```

---

## 5. 数据库设计

### 5.1 E-R 概要

```
User 1───N Product    (用户发布多个商品)
User 1───N Order      (用户有多个订单)
User 1───N Review     (用户发表多条评价)
Product 1───N OrderItem   (一件商品可被多次购买)
Product 1───N ProductImage (一件商品多张图片)
Product 1───N Favorite    (一件商品可被多人收藏)
Product N───1 Category    (商品属于一个分类)
Order 1───N OrderItem    (一个订单包含多个商品项)
```

### 5.2 表结构设计

#### 用户表 `user`

| 字段          | 类型          | 说明                       |
|---------------|---------------|----------------------------|
| id            | BIGINT        | 主键，雪花ID                |
| username      | VARCHAR(32)   | 用户名，唯一                |
| password      | VARCHAR(128)  | BCrypt加密密文             |
| nickname      | VARCHAR(32)   | 昵称                       |
| avatar        | VARCHAR(255)  | 头像URL                    |
| phone         | VARCHAR(16)   | 手机号                     |
| email         | VARCHAR(64)   | 邮箱                       |
| school        | VARCHAR(64)   | 学校                       |
| campus        | VARCHAR(32)   | 校区                       |
| role          | TINYINT       | 角色：0=用户 1=管理员      |
| status        | TINYINT       | 状态：0=正常 1=封禁        |
| credit_score  | INT           | 信誉分，默认100             |
| created_at    | DATETIME      | 创建时间                   |
| updated_at    | DATETIME      | 更新时间                   |

#### 商品分类表 `category`

| 字段      | 类型         | 说明               |
|-----------|--------------|--------------------|
| id        | BIGINT       | 主键               |
| name      | VARCHAR(32)  | 分类名（教材/数码/衣物...）|
| icon      | VARCHAR(255) | 分类图标URL         |
| sort_order| INT          | 排序               |
| created_at| DATETIME     | 创建时间            |

#### 商品表 `product`

| 字段         | 类型          | 说明                                   |
|--------------|---------------|----------------------------------------|
| id           | BIGINT        | 主键                                   |
| user_id      | BIGINT        | 发布者ID                               |
| category_id  | BIGINT        | 分类ID                                 |
| title        | VARCHAR(128)  | 标题                                   |
| description  | TEXT          | 描述                                   |
| price        | DECIMAL(10,2) | 价格                                   |
| original_price| DECIMAL(10,2)| 原价（选填）                            |
| condition    | TINYINT       | 新旧：1=全新 2=几乎全新 3=有使用痕迹   |
| campus       | VARCHAR(32)   | 交易校区                               |
| status       | TINYINT       | 0=待审核 1=在售 2=已售出 3=已下架      |
| view_count   | INT           | 浏览量，默认0                          |
| created_at   | DATETIME      | 发布时间                               |
| updated_at   | DATETIME      | 更新时间                               |

#### 商品图片表 `product_image`

| 字段       | 类型         | 说明          |
|------------|--------------|---------------|
| id         | BIGINT       | 主键          |
| product_id | BIGINT       | 商品ID        |
| url        | VARCHAR(255) | 图片URL       |
| sort_order | INT          | 排序（首图=1） |

#### 订单表 `order`

| 字段          | 类型          | 说明                                              |
|---------------|---------------|---------------------------------------------------|
| id            | BIGINT        | 主键                                              |
| order_no      | VARCHAR(32)   | 订单编号（业务唯一）                               |
| buyer_id      | BIGINT        | 买家ID                                            |
| seller_id     | BIGINT        | 卖家ID                                            |
| total_amount  | DECIMAL(10,2) | 总金额                                            |
| status        | TINYINT       | 0=待付款 1=待发货 2=待收货 3=已完成 4=已取消      |
| remark        | VARCHAR(255)  | 备注                                              |
| created_at    | DATETIME      | 下单时间                                          |
| paid_at       | DATETIME      | 付款时间                                          |
| shipped_at    | DATETIME      | 发货时间                                          |
| completed_at  | DATETIME      | 完成时间                                          |

#### 订单明细表 `order_item`

| 字段       | 类型          | 说明       |
|------------|---------------|------------|
| id         | BIGINT        | 主键       |
| order_id   | BIGINT        | 订单ID     |
| product_id | BIGINT        | 商品ID     |
| price      | DECIMAL(10,2) | 成交单价   |
| created_at | DATETIME      | 创建时间   |

#### 购物车表 `cart`

| 字段       | 类型     | 说明           |
|------------|----------|----------------|
| id         | BIGINT   | 主键           |
| user_id    | BIGINT   | 用户ID         |
| product_id | BIGINT   | 商品ID         |
| created_at | DATETIME | 添加时间       |

#### 收藏表 `favorite`

| 字段       | 类型     | 说明     |
|------------|----------|----------|
| id         | BIGINT   | 主键     |
| user_id    | BIGINT   | 用户ID   |
| product_id | BIGINT   | 商品ID   |
| created_at | DATETIME | 收藏时间 |

#### 聊天消息表 `chat_message`

| 字段          | 类型         | 说明                             |
|---------------|--------------|----------------------------------|
| id            | BIGINT       | 主键                             |
| sender_id     | BIGINT       | 发送者ID                         |
| receiver_id   | BIGINT       | 接收者ID                         |
| product_id    | BIGINT       | 关联商品ID（可为空）              |
| content       | TEXT         | 消息内容                         |
| is_read       | TINYINT      | 0=未读 1=已读                    |
| created_at    | DATETIME     | 发送时间                         |

#### 评价表 `review`

| 字段       | 类型         | 说明                         |
|------------|--------------|------------------------------|
| id         | BIGINT       | 主键                         |
| order_id   | BIGINT       | 订单ID                       |
| reviewer_id| BIGINT       | 评价者ID                     |
| target_id  | BIGINT       | 被评价者ID                   |
| rating     | TINYINT      | 评分 1-5                     |
| content    | VARCHAR(500) | 评价内容                     |
| created_at | DATETIME     | 评价时间                     |

> 索引建议：
> - `user(username) UNIQUE`、`product(category_id, status)`、`product(created_at)`
> - `order(buyer_id, status)`、`order(seller_id, status)`
> - `chat_message(sender_id, receiver_id)`、`chat_message(receiver_id, is_read)`
> - 所有外键字段加普通索引

---

## 6. API 接口设计

### 6.1 通用约定

- 基础路径: `http://localhost:8080/api`
- 请求格式: `application/json`
- 认证方式: Header `Authorization: Bearer <token>`
- 统一响应格式:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- 分页响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "pageSize": 10
  }
}
```

### 6.2 接口列表

#### 认证模块 `/api/auth`

| 方法   | 路径              | 说明       | 认证 |
|--------|-------------------|------------|------|
| POST   | /auth/register    | 注册       | 否   |
| POST   | /auth/login       | 登录       | 否   |
| POST   | /auth/logout      | 退出       | 是   |
| GET    | /auth/me          | 当前用户信息| 是   |

#### 用户模块 `/api/user`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| GET    | /user/{id}        | 用户详情       | 否   |
| PUT    | /user/profile     | 编辑个人资料    | 是   |
| PUT    | /user/password    | 修改密码       | 是   |
| POST   | /user/avatar      | 上传头像       | 是   |

#### 商品模块 `/api/products`

| 方法   | 路径                     | 说明            | 认证 |
|--------|--------------------------|-----------------|------|
| GET    | /products                | 商品列表（搜索+筛选+分页）| 否   |
| GET    | /products/{id}           | 商品详情        | 否   |
| POST   | /products                | 发布商品        | 是   |
| PUT    | /products/{id}           | 编辑商品        | 是   |
| PUT    | /products/{id}/status    | 下架商品        | 是   |
| GET    | /products/my             | 我的发布列表     | 是   |

#### 分类模块 `/api/categories`

| 方法   | 路径              | 说明       | 认证 |
|--------|-------------------|------------|------|
| GET    | /categories       | 全部分类   | 否   |

#### 订单模块 `/api/orders`

| 方法   | 路径                     | 说明            | 认证 |
|--------|--------------------------|-----------------|------|
| POST   | /orders                  | 创建订单        | 是   |
| GET    | /orders                  | 订单列表（支持状态筛选）| 是   |
| GET    | /orders/{id}             | 订单详情        | 是   |
| PUT    | /orders/{id}/pay         | 模拟付款        | 是   |
| PUT    | /orders/{id}/ship        | 卖家发货        | 是   |
| PUT    | /orders/{id}/confirm     | 确认收货        | 是   |
| PUT    | /orders/{id}/cancel      | 取消订单        | 是   |

#### 购物车模块 `/api/cart`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| GET    | /cart             | 购物车列表     | 是   |
| POST   | /cart             | 加入购物车     | 是   |
| DELETE | /cart/{id}        | 移除商品       | 是   |

#### 收藏模块 `/api/favorites`

| 方法   | 路径                     | 说明            | 认证 |
|--------|--------------------------|-----------------|------|
| GET    | /favorites               | 收藏列表        | 是   |
| POST   | /favorites               | 添加收藏        | 是   |
| DELETE | /favorites/{productId}   | 取消收藏        | 是   |
| GET    | /favorites/check/{productId} | 是否已收藏  | 是   |

#### 聊天模块 `/api/chat`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| GET    | /chat/contacts    | 会话联系人列表 | 是   |
| GET    | /chat/{contactId} | 历史消息       | 是   |
| POST   | /chat/send        | 发送消息       | 是   |
| PUT    | /chat/read/{contactId} | 标记已读  | 是   |
| WS     | /ws/chat          | WebSocket连接  | 是   |

#### 评价模块 `/api/reviews`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| POST   | /reviews          | 发表评价       | 是   |
| GET    | /reviews/user/{userId} | 用户评价列表| 否   |

#### 文件上传 `/api/files`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| POST   | /files/upload     | 单文件上传     | 是   |
| POST   | /files/uploads    | 多文件上传     | 是   |

#### 后台管理 `/api/admin/...`

| 方法   | 路径                       | 说明           | 认证 |
|--------|----------------------------|----------------|------|
| GET    | /admin/dashboard           | 仪表盘数据     | 是(管理员) |
| GET    | /admin/users               | 用户列表       | 是(管理员) |
| PUT    | /admin/users/{id}/status   | 封禁/解封用户  | 是(管理员) |
| GET    | /admin/products            | 商品列表       | 是(管理员) |
| PUT    | /admin/products/{id}/audit | 审核商品       | 是(管理员) |
| GET    | /admin/orders              | 订单列表       | 是(管理员) |
| POST   | /admin/categories          | 新增分类       | 是(管理员) |
| PUT    | /admin/categories/{id}     | 编辑分类       | 是(管理员) |
| DELETE | /admin/categories/{id}     | 删除分类       | 是(管理员) |

---

## 7. 开发环境搭建

### 7.1 环境要求

| 工具      | 版本要求    | 说明               |
|-----------|------------|--------------------|
| JDK       | 21+        | Spring Boot 4 需要 |
| Maven     | 3.9+       | 后端构建            |
| Node.js   | 18+ / 20+  | 前端构建            |
| pnpm      | 8+         | 推荐包管理器         |
| MySQL     | 8.0+       | 数据库              |
| Redis     | 7.x        | 缓存（可选）         |
| IDE       | IntelliJ IDEA / VS Code | 开发工具   |

### 7.2 后端启动

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE cuzssp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 导入初始数据
mysql -u root -p cuzssp < docs/sql/init.sql

# 3. 修改 application-dev.yml 中的数据库连接信息

# 4. 启动后端
cd CuzShortSemester2026/code/backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 7.3 前端启动

```bash
cd CuzShortSemester2026/code/frontend
pnpm install
pnpm dev
# 访问 http://localhost:5173
```

### 7.4 `application-dev.yml` 关键配置

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cuzssp?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 开发阶段打印SQL
  global-config:
    db-config:
      id-type: assign_id   # 雪花算法ID

jwt:
  secret: your-jwt-secret-key-at-least-256-bits-long
  expiration: 86400000    # 24小时, 毫秒

r2:
  endpoint: https://<your-account-id>.r2.cloudflarestorage.com
  access-key-id: your_r2_access_key_id
  secret-access-key: your_r2_secret_access_key
  bucket-name: cuz-trade-images
  region: auto
  public-url: https://your-domain.com  # 自定义域名或 R2 公共 URL
```

---

## 8. 开发规范

### 8.1 Git 分支策略

```
main        — 生产分支
├── dev     — 开发主分支
│   ├── feature/user-login     — 功能分支
│   ├── feature/product-list
│   └── ...
└── release/v1.0 — 发布分支
```

### 8.2 命名规范

| 类别       | 规范                     | 示例                         |
|------------|--------------------------|------------------------------|
| Java 类名  | UpperCamelCase           | `ProductController`          |
| Java 方法  | lowerCamelCase           | `getProductList()`           |
| 数据库表   | snake_case               | `product_image`              |
| 数据库字段 | snake_case               | `created_at`                 |
| RESTful URL| 短横线小写+资源名复数     | `/api/products/{id}`         |
| Vue 组件   | PascalCase               | `ProductCard.vue`            |
| Vue 文件   | kebab-case 或 PascalCase | `product-list.vue`           |
| CSS 类名   | kebab-case               | `.product-card`              |

### 8.3 代码规范

- **后端**: 遵循阿里巴巴Java开发手册, Controller → Service → Mapper 三层架构
- **前端**: ESLint + Prettier 统一格式化, Composition API (`<script setup>`) 风格
- **注释**: JavaDoc 注释 public 方法, 复杂逻辑加行内注释
- **异常处理**: 统一用 `@RestControllerAdvice` 全局异常处理, 业务异常抛 `BusinessException`

### 8.4 提交规范

遵循 Conventional Commits:

```
feat: 新增用户注册功能
fix: 修复商品列表分页Bug
docs: 更新API文档
style: 格式化代码
refactor: 重构订单Service
test: 添加用户模块单元测试
```

---

## 9. 开发顺序建议

| 阶段 | 内容                                           | 预计周期 |
|------|------------------------------------------------|----------|
| P1   | 项目初始化 + 数据库建表 + 用户认证模块          | 3 天     |
| P2   | 商品模块（CRUD + 搜索 + 分类）                  | 4 天     |
| P3   | 订单模块 + 购物车 + 收藏                        | 4 天     |
| P4   | 聊天模块（WebSocket）                           | 3 天     |
| P5   | 评价系统 + 用户信誉                             | 2 天     |
| P6   | 后台管理端全部功能                              | 4 天     |
| P7   | 文件上传 + 图片处理 + 前端联调 + 测试           | 3 天     |

---

## 10. 部署说明

### 后端部署

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar -Dspring.profiles.active=prod backend/target/cuz-trade-1.0.0.jar
```

### 前端部署

```bash
pnpm build
# dist/ 目录部署到 Nginx
```

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name trade.example.com;

    # 前端
    location / {
        root /var/www/cuz-trade/dist;
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # WebSocket 代理
    location /ws/ {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

---

> **文档维护**: 本 DEV.md 随项目迭代持续更新，每次功能变更需同步修改对应章节。

│   ├── backend/                        # Spring Boot 后端
│   │   ├── pom.xml                     # Maven 配置
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/cuzssp/campussecondhandtradingplatform_backend/
│   │   │   │   │   ├── CampusSecondHandTradingPlatformBackendApplication.java        # 启动类
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── AuthController.java          # 认证接口
│   │   │   │   │   │   ├── UserController.java          # 用户接口
│   │   │   │   │   │   ├── ProductController.java       # 商品接口
│   │   │   │   │   │   ├── CategoryController.java      # 分类接口
│   │   │   │   │   │   ├── OrderController.java         # 订单接口
│   │   │   │   │   │   ├── CartController.java          # 购物车接口
│   │   │   │   │   │   ├── FavoriteController.java      # 收藏接口
│   │   │   │   │   │   ├── ChatController.java          # 聊天接口
│   │   │   │   │   │   ├── ReviewController.java        # 评价接口
│   │   │   │   │   │   ├── FileController.java          # 文件上传接口
│   │   │   │   │   │   └── admin/
│   │   │   │   │   │       ├── AdminUserController.java
│   │   │   │   │   │       ├── AdminProductController.java
│   │   │   │   │   │       ├── AdminOrderController.java
│   │   │   │   │   │       └── AdminDashboardController.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── AuthService.java
│   │   │   │   │   │   ├── UserService.java
│   │   │   │   │   │   ├── ProductService.java
│   │   │   │   │   │   ├── CategoryService.java
│   │   │   │   │   │   ├── OrderService.java
│   │   │   │   │   │   ├── CartService.java
│   │   │   │   │   │   ├── FavoriteService.java
│   │   │   │   │   │   ├── ChatService.java
│   │   │   │   │   │   ├── ReviewService.java
│   │   │   │   │   │   ├── FileService.java
│   │   │   │   │   │   └── impl/
│   │   │   │   │   │       ├── AuthServiceImpl.java
│   │   │   │   │   │       ├── UserServiceImpl.java
│   │   │   │   │   │       ├── ProductServiceImpl.java
│   │   │   │   │   │       ├── CategoryServiceImpl.java
│   │   │   │   │   │       ├── OrderServiceImpl.java
│   │   │   │   │   │       ├── CartServiceImpl.java
│   │   │   │   │   │       ├── FavoriteServiceImpl.java
│   │   │   │   │   │       ├── ChatServiceImpl.java
│   │   │   │   │   │       ├── ReviewServiceImpl.java
│   │   │   │   │   │       └── FileServiceImpl.java
│   │   │   │   │   ├── mapper/
│   │   │   │   │   │   ├── UserMapper.java
│   │   │   │   │   │   ├── ProductMapper.java
│   │   │   │   │   │   ├── ProductImageMapper.java
│   │   │   │   │   │   ├── CategoryMapper.java
│   │   │   │   │   │   ├── OrderMapper.java
│   │   │   │   │   │   ├── OrderItemMapper.java
│   │   │   │   │   │   ├── CartMapper.java
│   │   │   │   │   │   ├── FavoriteMapper.java
│   │   │   │   │   │   ├── ChatMessageMapper.java
│   │   │   │   │   │   └── ReviewMapper.java
│   │   │   │   │   └── common/
│   │   │   │   │       ├── pojo/
│   │   │   │   │       │   ├── User.java
│   │   │   │   │       │   ├── Product.java
│   │   │   │   │       │   ├── ProductImage.java
│   │   │   │   │       │   ├── Category.java
│   │   │   │   │       │   ├── Order.java
│   │   │   │   │       │   ├── OrderItem.java
│   │   │   │   │       │   ├── Cart.java
│   │   │   │   │       │   ├── Favorite.java
│   │   │   │   │       │   ├── ChatMessage.java
│   │   │   │   │       │   └── Review.java
│   │   │   │   │       ├── dto/
│   │   │   │   │       │   ├── request/
│   │   │   │   │       │   │   ├── LoginRequest.java
│   │   │   │   │       │   │   ├── RegisterRequest.java
│   │   │   │   │       │   │   ├── ProductRequest.java
│   │   │   │   │       │   │   └── OrderRequest.java
│   │   │   │   │       │   └── response/
│   │   │   │   │       │       ├── ApiResponse.java
│   │   │   │   │       │       ├── PageResponse.java
│   │   │   │   │       │       ├── LoginResponse.java
│   │   │   │   │       │       └── ProductVO.java
│   │   │   │   │       ├── config/
│   │   │   │   │       │   ├── SecurityConfig.java
│   │   │   │   │       │   ├── JwtConfig.java
│   │   │   │   │       │   ├── CorsConfig.java
│   │   │   │   │       │   ├── MyBatisPlusConfig.java
│   │   │   │   │       │   ├── WebSocketConfig.java
│   │   │   │   │       │   ├── FileUploadConfig.java
│   │   │   │   │       │   └── R2Config.java
│   │   │   │   │       ├── security/
│   │   │   │   │       │   ├── JwtTokenProvider.java
│   │   │   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │   │   │       │   └── UserDetailsServiceImpl.java
│   │   │   │   │       ├── exception/
│   │   │   │   │       │   ├── BusinessException.java
│   │   │   │   │       │   └── GlobalExceptionHandler.java
│   │   │   │   │       └── util/
│   │   │   │   │           ├── FileUtil.java
│   │   │   │   │           └── SnowflakeIdUtil.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       ├── application-dev.yml
│   │   │   │       ├── application-prod.yml
│   │   │   │       └── mapper/
│   │   │   └── test/
│   │   └── uploads/
│   │   │   │   │   │   └── R2Config.java               # Cloudflare R2 配置
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── AuthController.java          # 认证接口
│   │   │   │   │   │   ├── UserController.java          # 用户接口
│   │   │   │   │   │   ├── ProductController.java       # 商品接口
│   │   │   │   │   │   ├── CategoryController.java      # 分类接口
│   │   │   │   │   │   ├── OrderController.java         # 订单接口
│   │   │   │   │   │   ├── CartController.java          # 购物车接口
│   │   │   │   │   │   ├── FavoriteController.java      # 收藏接口
│   │   │   │   │   │   ├── ChatController.java          # 聊天接口
│   │   │   │   │   │   ├── ReviewController.java        # 评价接口
│   │   │   │   │   │   ├── FileController.java          # 文件上传接口
│   │   │   │   │   │   └── admin/
│   │   │   │   │   │       ├── AdminUserController.java  # 管理-用户
│   │   │   │   │   │       ├── AdminProductController.java
│   │   │   │   │   │       ├── AdminOrderController.java
│   │   │   │   │   │       └── AdminDashboardController.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── AuthService.java
│   │   │   │   │   │   ├── UserService.java
│   │   │   │   │   │   ├── ProductService.java
│   │   │   │   │   │   ├── CategoryService.java
│   │   │   │   │   │   ├── OrderService.java
│   │   │   │   │   │   ├── CartService.java
│   │   │   │   │   │   ├── FavoriteService.java
│   │   │   │   │   │   ├── ChatService.java
│   │   │   │   │   │   ├── ReviewService.java
│   │   │   │   │   │   └── FileService.java
│   │   │   │   │   │   └── impl/                        # Service 实现类
│   │   │   │   │   ├── mapper/                          # MyBatis-Plus Mapper
│   │   │   │   │   │   ├── UserMapper.java
│   │   │   │   │   │   ├── ProductMapper.java
│   │   │   │   │   │   ├── ProductImageMapper.java
│   │   │   │   │   │   ├── CategoryMapper.java
│   │   │   │   │   │   ├── OrderMapper.java
│   │   │   │   │   │   ├── CartMapper.java
│   │   │   │   │   │   ├── FavoriteMapper.java
│   │   │   │   │   │   ├── ChatMessageMapper.java
│   │   │   │   │   │   └── ReviewMapper.java
│   │   │   │   │   ├── entity/                          # 数据库实体
│   │   │   │   │   │   ├── User.java
│   │   │   │   │   │   ├── Product.java
│   │   │   │   │   │   ├── ProductImage.java
│   │   │   │   │   │   ├── Category.java
│   │   │   │   │   │   ├── Order.java
│   │   │   │   │   │   ├── OrderItem.java
│   │   │   │   │   │   ├── Cart.java
│   │   │   │   │   │   ├── Favorite.java
│   │   │   │   │   │   ├── ChatMessage.java
│   │   │   │   │   │   └── Review.java
│   │   │   │   │   ├── dto/                             # 数据传输对象
│   │   │   │   │   │   ├── request/
│   │   │   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   │   │   ├── ProductRequest.java
│   │   │   │   │   │   │   ├── OrderRequest.java
│   │   │   │   │   │   │   └── ...
│   │   │   │   │   │   └── response/
│   │   │   │   │   │       ├── ApiResponse.java         # 统一响应体
│   │   │   │   │   │       ├── LoginResponse.java
│   │   │   │   │   │       ├── ProductVO.java
│   │   │   │   │   │       └── ...
│   │   │   │   │   ├── security/
│   │   │   │   │   │   ├── JwtTokenProvider.java        # JWT 工具类
│   │   │   │   │   │   ├── JwtAuthenticationFilter.java # JWT 过滤器
│   │   │   │   │   │   └── UserDetailsServiceImpl.java  # 用户详情加载
│   │   │   │   │   ├── common/
│   │   │   │   │   │   ├── Result.java                  # 统一返回类
│   │   │   │   │   │   ├── PageResult.java              # 分页返回
│   │   │   │   │   │   ├── BusinessException.java       # 业务异常
│   │   │   │   │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   │   │   │   │   └── util/
│   │   │   │   │       ├── FileUtil.java
│   │   │   │   │       └── SnowflakeIdUtil.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml                  # 主配置
│   │   │   │       ├── application-dev.yml              # 开发环境
│   │   │   │       ├── application-prod.yml             # 生产环境
│   │   │   │       └── mapper/                          # MyBatis XML（如需）
│   │   │   └── test/
│   │   └── uploads/                                     # 本地上传目录
│   │
│   └── frontend/                        # Vue 3 前端
│       ├── package.json
│       ├── vite.config.ts
│       ├── index.html
│       ├── tsconfig.json
│       ├── src/
│       │   ├── main.ts                  # 入口
│       │   ├── App.vue                  # 根组件
│       │   ├── router/
│       │   │   └── index.ts             # 路由配置
│       │   ├── stores/                  # Pinia 状态管理
│       │   │   ├── user.ts              # 用户状态
│       │   │   ├── cart.ts              # 购物车状态
│       │   │   └── chat.ts              # 聊天状态
│       │   ├── api/                     # 接口请求层
│       │   │   ├── request.ts           # Axios 封装 + 拦截器
│       │   │   ├── auth.ts
│       │   │   ├── user.ts
│       │   │   ├── product.ts
│       │   │   ├── category.ts
│       │   │   ├── order.ts
│       │   │   ├── cart.ts
│       │   │   ├── favorite.ts
│       │   │   ├── chat.ts
│       │   │   └── review.ts
│       │   ├── views/                   # 页面视图
│       │   │   ├── HomeView.vue         # 首页
│       │   │   ├── LoginView.vue        # 登录
│       │   │   ├── RegisterView.vue     # 注册
│       │   │   ├── product/
│       │   │   │   ├── ProductList.vue  # 商品列表
│       │   │   │   ├── ProductDetail.vue# 商品详情
│       │   │   │   └── ProductPublish.vue# 发布商品
│       │   │   ├── order/
│       │   │   │   ├── OrderList.vue    # 订单列表
│       │   │   │   └── OrderDetail.vue  # 订单详情
│       │   │   ├── user/
│       │   │   │   ├── ProfileView.vue  # 个人主页
│       │   │   │   └── MyProducts.vue   # 我的发布
│       │   │   ├── chat/
│       │   │   │   └── ChatView.vue     # 聊天页
│       │   │   ├── favorite/
│       │   │   │   └── FavoriteList.vue # 收藏列表
│       │   │   └── admin/              # 后台管理
│       │   │       ├── AdminLayout.vue
│       │   │       ├── DashboardView.vue
│       │   │       ├── UserManage.vue
│       │   │       ├── ProductManage.vue
│       │   │       └── OrderManage.vue
│       │   ├── components/             # 公共组件
│       │   │   ├── layout/
│       │   │   │   ├── AppHeader.vue    # 顶部导航
│       │   │   │   └── AppFooter.vue    # 底部
│       │   │   ├── ProductCard.vue      # 商品卡片
│       │   │   ├── SearchBar.vue        # 搜索栏
│       │   │   ├── ImageUpload.vue      # 图片上传
│       │   │   └── Pagination.vue       # 分页器
│       │   ├── utils/
│       │   │   ├── index.ts             # 通用工具函数
│       │   │   └── validators.ts        # 表单校验
│       │   └── styles/
│       │       ├── variables.scss       # SCSS 变量
│       │       └── global.scss          # 全局样式
│       └── public/
│           └── favicon.ico
│
└── docs/
    └── sql/
        └── init.sql                     # 数据库初始化脚本
```

---

## 5. 数据库设计

### 5.1 E-R 概要

```
User 1───N Product    (用户发布多个商品)
User 1───N Order      (用户有多个订单)
User 1───N Review     (用户发表多条评价)
Product 1───N OrderItem   (一件商品可被多次购买)
Product 1───N ProductImage (一件商品多张图片)
Product 1───N Favorite    (一件商品可被多人收藏)
Product N───1 Category    (商品属于一个分类)
Order 1───N OrderItem    (一个订单包含多个商品项)
```

### 5.2 表结构设计

#### 用户表 `user`

| 字段          | 类型          | 说明                       |
|---------------|---------------|----------------------------|
| id            | BIGINT        | 主键，雪花ID                |
| username      | VARCHAR(32)   | 用户名，唯一                |
| password      | VARCHAR(128)  | BCrypt加密密文             |
| nickname      | VARCHAR(32)   | 昵称                       |
| avatar        | VARCHAR(255)  | 头像URL                    |
| phone         | VARCHAR(16)   | 手机号                     |
| email         | VARCHAR(64)   | 邮箱                       |
| school        | VARCHAR(64)   | 学校                       |
| campus        | VARCHAR(32)   | 校区                       |
| role          | TINYINT       | 角色：0=用户 1=管理员      |
| status        | TINYINT       | 状态：0=正常 1=封禁        |
| credit_score  | INT           | 信誉分，默认100             |
| created_at    | DATETIME      | 创建时间                   |
| updated_at    | DATETIME      | 更新时间                   |

#### 商品分类表 `category`

| 字段      | 类型         | 说明               |
|-----------|--------------|--------------------|
| id        | BIGINT       | 主键               |
| name      | VARCHAR(32)  | 分类名（教材/数码/衣物...）|
| icon      | VARCHAR(255) | 分类图标URL         |
| sort_order| INT          | 排序               |
| created_at| DATETIME     | 创建时间            |

#### 商品表 `product`

| 字段         | 类型          | 说明                                   |
|--------------|---------------|----------------------------------------|
| id           | BIGINT        | 主键                                   |
| user_id      | BIGINT        | 发布者ID                               |
| category_id  | BIGINT        | 分类ID                                 |
| title        | VARCHAR(128)  | 标题                                   |
| description  | TEXT          | 描述                                   |
| price        | DECIMAL(10,2) | 价格                                   |
| original_price| DECIMAL(10,2)| 原价（选填）                            |
| condition    | TINYINT       | 新旧：1=全新 2=几乎全新 3=有使用痕迹   |
| campus       | VARCHAR(32)   | 交易校区                               |
| status       | TINYINT       | 0=待审核 1=在售 2=已售出 3=已下架      |
| view_count   | INT           | 浏览量，默认0                          |
| created_at   | DATETIME      | 发布时间                               |
| updated_at   | DATETIME      | 更新时间                               |

#### 商品图片表 `product_image`

| 字段       | 类型         | 说明          |
|------------|--------------|---------------|
| id         | BIGINT       | 主键          |
| product_id | BIGINT       | 商品ID        |
| url        | VARCHAR(255) | 图片URL       |
| sort_order | INT          | 排序（首图=1） |

#### 订单表 `order`

| 字段          | 类型          | 说明                                              |
|---------------|---------------|---------------------------------------------------|
| id            | BIGINT        | 主键                                              |
| order_no      | VARCHAR(32)   | 订单编号（业务唯一）                               |
| buyer_id      | BIGINT        | 买家ID                                            |
| seller_id     | BIGINT        | 卖家ID                                            |
| total_amount  | DECIMAL(10,2) | 总金额                                            |
| status        | TINYINT       | 0=待付款 1=待发货 2=待收货 3=已完成 4=已取消      |
| remark        | VARCHAR(255)  | 备注                                              |
| created_at    | DATETIME      | 下单时间                                          |
| paid_at       | DATETIME      | 付款时间                                          |
| shipped_at    | DATETIME      | 发货时间                                          |
| completed_at  | DATETIME      | 完成时间                                          |

#### 订单明细表 `order_item`

| 字段       | 类型          | 说明       |
|------------|---------------|------------|
| id         | BIGINT        | 主键       |
| order_id   | BIGINT        | 订单ID     |
| product_id | BIGINT        | 商品ID     |
| price      | DECIMAL(10,2) | 成交单价   |
| created_at | DATETIME      | 创建时间   |

#### 购物车表 `cart`

| 字段       | 类型     | 说明           |
|------------|----------|----------------|
| id         | BIGINT   | 主键           |
| user_id    | BIGINT   | 用户ID         |
| product_id | BIGINT   | 商品ID         |
| created_at | DATETIME | 添加时间       |

#### 收藏表 `favorite`

| 字段       | 类型     | 说明     |
|------------|----------|----------|
| id         | BIGINT   | 主键     |
| user_id    | BIGINT   | 用户ID   |
| product_id | BIGINT   | 商品ID   |
| created_at | DATETIME | 收藏时间 |

#### 聊天消息表 `chat_message`

| 字段          | 类型         | 说明                             |
|---------------|--------------|----------------------------------|
| id            | BIGINT       | 主键                             |
| sender_id     | BIGINT       | 发送者ID                         |
| receiver_id   | BIGINT       | 接收者ID                         |
| product_id    | BIGINT       | 关联商品ID（可为空）              |
| content       | TEXT         | 消息内容                         |
| is_read       | TINYINT      | 0=未读 1=已读                    |
| created_at    | DATETIME     | 发送时间                         |

#### 评价表 `review`

| 字段       | 类型         | 说明                         |
|------------|--------------|------------------------------|
| id         | BIGINT       | 主键                         |
| order_id   | BIGINT       | 订单ID                       |
| reviewer_id| BIGINT       | 评价者ID                     |
| target_id  | BIGINT       | 被评价者ID                   |
| rating     | TINYINT      | 评分 1-5                     |
| content    | VARCHAR(500) | 评价内容                     |
| created_at | DATETIME     | 评价时间                     |

> 索引建议：
> - `user(username) UNIQUE`、`product(category_id, status)`、`product(created_at)`
> - `order(buyer_id, status)`、`order(seller_id, status)`
> - `chat_message(sender_id, receiver_id)`、`chat_message(receiver_id, is_read)`
> - 所有外键字段加普通索引

---

## 6. API 接口设计

### 6.1 通用约定

- 基础路径: `http://localhost:8080/api`
- 请求格式: `application/json`
- 认证方式: Header `Authorization: Bearer <token>`
- 统一响应格式:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- 分页响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "pageSize": 10
  }
}
```

### 6.2 接口列表

#### 认证模块 `/api/auth`

| 方法   | 路径              | 说明       | 认证 |
|--------|-------------------|------------|------|
| POST   | /auth/register    | 注册       | 否   |
| POST   | /auth/login       | 登录       | 否   |
| POST   | /auth/logout      | 退出       | 是   |
| GET    | /auth/me          | 当前用户信息| 是   |

#### 用户模块 `/api/user`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| GET    | /user/{id}        | 用户详情       | 否   |
| PUT    | /user/profile     | 编辑个人资料    | 是   |
| PUT    | /user/password    | 修改密码       | 是   |
| POST   | /user/avatar      | 上传头像       | 是   |

#### 商品模块 `/api/products`

| 方法   | 路径                     | 说明            | 认证 |
|--------|--------------------------|-----------------|------|
| GET    | /products                | 商品列表（搜索+筛选+分页）| 否   |
| GET    | /products/{id}           | 商品详情        | 否   |
| POST   | /products                | 发布商品        | 是   |
| PUT    | /products/{id}           | 编辑商品        | 是   |
| PUT    | /products/{id}/status    | 下架商品        | 是   |
| GET    | /products/my             | 我的发布列表     | 是   |

#### 分类模块 `/api/categories`

| 方法   | 路径              | 说明       | 认证 |
|--------|-------------------|------------|------|
| GET    | /categories       | 全部分类   | 否   |

#### 订单模块 `/api/orders`

| 方法   | 路径                     | 说明            | 认证 |
|--------|--------------------------|-----------------|------|
| POST   | /orders                  | 创建订单        | 是   |
| GET    | /orders                  | 订单列表（支持状态筛选）| 是   |
| GET    | /orders/{id}             | 订单详情        | 是   |
| PUT    | /orders/{id}/pay         | 模拟付款        | 是   |
| PUT    | /orders/{id}/ship        | 卖家发货        | 是   |
| PUT    | /orders/{id}/confirm     | 确认收货        | 是   |
| PUT    | /orders/{id}/cancel      | 取消订单        | 是   |

#### 购物车模块 `/api/cart`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| GET    | /cart             | 购物车列表     | 是   |
| POST   | /cart             | 加入购物车     | 是   |
| DELETE | /cart/{id}        | 移除商品       | 是   |

#### 收藏模块 `/api/favorites`

| 方法   | 路径                     | 说明            | 认证 |
|--------|--------------------------|-----------------|------|
| GET    | /favorites               | 收藏列表        | 是   |
| POST   | /favorites               | 添加收藏        | 是   |
| DELETE | /favorites/{productId}   | 取消收藏        | 是   |
| GET    | /favorites/check/{productId} | 是否已收藏  | 是   |

#### 聊天模块 `/api/chat`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| GET    | /chat/contacts    | 会话联系人列表 | 是   |
| GET    | /chat/{contactId} | 历史消息       | 是   |
| POST   | /chat/send        | 发送消息       | 是   |
| PUT    | /chat/read/{contactId} | 标记已读  | 是   |
| WS     | /ws/chat          | WebSocket连接  | 是   |

#### 评价模块 `/api/reviews`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| POST   | /reviews          | 发表评价       | 是   |
| GET    | /reviews/user/{userId} | 用户评价列表| 否   |

#### 文件上传 `/api/files`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| POST   | /files/upload     | 单文件上传     | 是   |
| POST   | /files/uploads    | 多文件上传     | 是   |

#### 后台管理 `/api/admin/...`

| 方法   | 路径                       | 说明           | 认证 |
|--------|----------------------------|----------------|------|
| GET    | /admin/dashboard           | 仪表盘数据     | 是(管理员) |
| GET    | /admin/users               | 用户列表       | 是(管理员) |
| PUT    | /admin/users/{id}/status   | 封禁/解封用户  | 是(管理员) |
| GET    | /admin/products            | 商品列表       | 是(管理员) |
| PUT    | /admin/products/{id}/audit | 审核商品       | 是(管理员) |
| GET    | /admin/orders              | 订单列表       | 是(管理员) |
| POST   | /admin/categories          | 新增分类       | 是(管理员) |
| PUT    | /admin/categories/{id}     | 编辑分类       | 是(管理员) |
| DELETE | /admin/categories/{id}     | 删除分类       | 是(管理员) |

---

## 7. 开发环境搭建

### 7.1 环境要求

| 工具      | 版本要求    | 说明               |
|-----------|------------|--------------------|
| JDK       | 21+        | Spring Boot 4 需要 |
| Maven     | 3.9+       | 后端构建            |
| Node.js   | 18+ / 20+  | 前端构建            |
| pnpm      | 8+         | 推荐包管理器         |
| MySQL     | 8.0+       | 数据库              |
| Redis     | 7.x        | 缓存（可选）         |
| IDE       | IntelliJ IDEA / VS Code | 开发工具   |

### 7.2 后端启动

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE cuzssp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 导入初始数据
mysql -u root -p cuzssp < docs/sql/init.sql

# 3. 修改 application-dev.yml 中的数据库连接信息

# 4. 启动后端
cd CuzShortSemester2026/code/backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 7.3 前端启动

```bash
cd CuzShortSemester2026/code/frontend
pnpm install
pnpm dev
# 访问 http://localhost:5173
```

### 7.4 `application-dev.yml` 关键配置

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cuzssp?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 开发阶段打印SQL
  global-config:
    db-config:
      id-type: assign_id   # 雪花算法ID

jwt:
  secret: your-jwt-secret-key-at-least-256-bits-long
  expiration: 86400000    # 24小时, 毫秒

r2:
  endpoint: https://<your-account-id>.r2.cloudflarestorage.com
  access-key-id: your_r2_access_key_id
  secret-access-key: your_r2_secret_access_key
  bucket-name: cuz-trade-images
  region: auto
  public-url: https://your-domain.com  # 自定义域名或 R2 公共 URL
```

---

## 8. 开发规范

### 8.1 Git 分支策略

```
main        — 生产分支
├── dev     — 开发主分支
│   ├── feature/user-login     — 功能分支
│   ├── feature/product-list
│   └── ...
└── release/v1.0 — 发布分支
```

### 8.2 命名规范

| 类别       | 规范                     | 示例                         |
|------------|--------------------------|------------------------------|
| Java 类名  | UpperCamelCase           | `ProductController`          |
| Java 方法  | lowerCamelCase           | `getProductList()`           |
| 数据库表   | snake_case               | `product_image`              |
| 数据库字段 | snake_case               | `created_at`                 |
| RESTful URL| 短横线小写+资源名复数     | `/api/products/{id}`         |
| Vue 组件   | PascalCase               | `ProductCard.vue`            |
| Vue 文件   | kebab-case 或 PascalCase | `product-list.vue`           |
| CSS 类名   | kebab-case               | `.product-card`              |

### 8.3 代码规范

- **后端**: 遵循阿里巴巴Java开发手册, Controller → Service → Mapper 三层架构
- **前端**: ESLint + Prettier 统一格式化, Composition API (`<script setup>`) 风格
- **注释**: JavaDoc 注释 public 方法, 复杂逻辑加行内注释
- **异常处理**: 统一用 `@RestControllerAdvice` 全局异常处理, 业务异常抛 `BusinessException`

### 8.4 提交规范

遵循 Conventional Commits:

```
feat: 新增用户注册功能
fix: 修复商品列表分页Bug
docs: 更新API文档
style: 格式化代码
refactor: 重构订单Service
test: 添加用户模块单元测试
```

---

## 9. 开发顺序建议

| 阶段 | 内容                                           | 预计周期 |
|------|------------------------------------------------|----------|
| P1   | 项目初始化 + 数据库建表 + 用户认证模块          | 3 天     |
| P2   | 商品模块（CRUD + 搜索 + 分类）                  | 4 天     |
| P3   | 订单模块 + 购物车 + 收藏                        | 4 天     |
| P4   | 聊天模块（WebSocket）                           | 3 天     |
| P5   | 评价系统 + 用户信誉                             | 2 天     |
| P6   | 后台管理端全部功能                              | 4 天     |
| P7   | 文件上传 + 图片处理 + 前端联调 + 测试           | 3 天     |

---

## 10. 部署说明

### 后端部署

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar -Dspring.profiles.active=prod backend/target/cuz-trade-1.0.0.jar
```

### 前端部署

```bash
pnpm build
# dist/ 目录部署到 Nginx
```

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name trade.example.com;

    # 前端
    location / {
        root /var/www/cuz-trade/dist;
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # WebSocket 代理
    location /ws/ {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

---

> **文档维护**: 本 DEV.md 随项目迭代持续更新，每次功能变更需同步修改对应章节。

# 校园二手交易平台 — 开发说明文档 (DEV.md)

> **版本**: v1.0  
> **日期**: 2026-07-05  
> **项目名**: CuzShortSemester2026 — Campus Second-Hand Trading Platform

---

## 1. 项目概述

本项目是一个面向高校学生的校园二手物品交易 Web 应用，旨在为校内用户提供一个安全、便捷的二手商品发布、浏览、交易与交流平台。系统分为前台用户端和后台管理端，支持商品分类浏览、搜索筛选、在线聊天、订单管理、评价系统等核心功能。

---

## 2. 技术栈

| 层级   | 技术选择                          | 版本       |
|--------|-----------------------------------|------------|
| 后端   | Spring Boot                       | 4.x        |
| 后端   | Spring Security + JWT             | —          |
| 后端   | MyBatis-Plus                      | 3.5+       |
| 后端   | MySQL Driver (Connector/J)        | 8.x        |
| 数据库 | MySQL                             | 8.0+       |
| 缓存   | Redis                             | 7.x（可选） |
| 前端   | Vue                               | 3.4+       |
| 前端   | Vue Router                        | 4.x        |
| 前端   | Pinia (状态管理)                  | 2.x        |
| 前端   | Element Plus (UI组件库)           | 2.x        |
| 前端   | Axios                             | 1.x        |
| 构建   | Vite                              | 5.x        |
| 包管理 | pnpm / npm                        | 最新       |
| 对象存储 | Cloudflare R2 (S3 Compatible API) | —          |
| 后端   | AWS SDK for Java (S3)            | 2.x        |

---

## 3. 需求分析

### 3.1 用户角色

| 角色         | 描述                                 |
|--------------|--------------------------------------|
| 普通用户     | 注册登录、浏览商品、发布商品、下单购买、聊天 |
| 管理员       | 用户管理、商品审核、订单管理、系统配置     |

### 3.2 功能模块

#### 前台用户端

| 模块       | 功能点                                                                 |
|------------|------------------------------------------------------------------------|
| 用户系统   | 注册、登录、退出、个人信息编辑、头像上传、密码修改、手机/邮箱绑定       |
| 商品浏览   | 首页推荐、分类浏览、关键词搜索、条件筛选（价格/新旧/校区）、商品详情     |
| 商品发布   | 发布商品（标题/描述/图片/价格/分类/新旧程度）、编辑、下架、已发布列表     |
| 收藏系统   | 收藏/取消收藏商品、收藏列表                                             |
| 订单系统   | 下单购买、订单列表（待付款/待发货/待收货/已完成/已取消）、取消订单       |
| 聊天系统   | 买卖双方在线聊天（WebSocket）、消息列表、未读提醒                       |
| 评价系统   | 交易完成后互评（星级 + 文字）、查看用户信誉                             |

#### 后台管理端

| 模块       | 功能点                                                                 |
|------------|------------------------------------------------------------------------|
| 仪表盘     | 用户数、商品数、订单数、交易额统计、图表展示                            |
| 用户管理   | 用户列表、封禁/解封、信息查看                                           |
| 商品管理   | 商品列表、审核通过/驳回、违规下架                                       |
| 订单管理   | 订单列表、退款处理、订单状态查看                                         |
| 分类管理   | 商品分类增删改查                                                       |
| 公告管理   | 系统公告发布、管理                                                     |

### 3.3 非功能性需求

- **安全性**: 密码加密存储（BCrypt），JWT 令牌鉴权，SQL 注入防护，XSS 防护
- **性能**: 首页首屏加载 < 3s，列表分页查询 < 500ms
- **可用性**: 支持 500+ 并发用户
- **可维护性**: 前后端分离，RESTful API，模块化组件
- **图片存储**: Cloudflare R2（S3兼容对象存储，开发与生产统一方案）

---

## 4. 项目目录结构

```
CuzShortSemester2026/
├── code/
│   ├── DEV.md                          # 本开发说明文档
│   ├── backend/                        # Spring Boot 后端
│   │   ├── pom.xml                     # Maven 配置
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/cuzssp/campussecondhandtradingplatform_backend/
│   │   │   │   │   ├── CampusSecondHandTradingPlatformBackendApplication.java        # 启动类
│   │   │   │   │   ├── config/
│   │   │   │   │   │   ├── SecurityConfig.java          # Spring Security 配置
│   │   │   │   │   │   ├── JwtConfig.java               # JWT 配置
│   │   │   │   │   │   ├── CorsConfig.java              # 跨域配置
│   │   │   │   │   │   ├── MyBatisPlusConfig.java       # MyBatis-Plus 配置
│   │   │   │   │   │   ├── WebSocketConfig.java         # WebSocket 配置
│   │   │   │   │   │   ├── FileUploadConfig.java        # 文件上传配置
│   │   │   │   │   │   └── R2Config.java               # Cloudflare R2 配置
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── AuthController.java          # 认证接口
│   │   │   │   │   │   ├── UserController.java          # 用户接口
│   │   │   │   │   │   ├── ProductController.java       # 商品接口
│   │   │   │   │   │   ├── CategoryController.java      # 分类接口
│   │   │   │   │   │   ├── OrderController.java         # 订单接口
│   │   │   │   │   │   ├── CartController.java          # 购物车接口
│   │   │   │   │   │   ├── FavoriteController.java      # 收藏接口
│   │   │   │   │   │   ├── ChatController.java          # 聊天接口
│   │   │   │   │   │   ├── ReviewController.java        # 评价接口
│   │   │   │   │   │   ├── FileController.java          # 文件上传接口
│   │   │   │   │   │   └── admin/
│   │   │   │   │   │       ├── AdminUserController.java  # 管理-用户
│   │   │   │   │   │       ├── AdminProductController.java
│   │   │   │   │   │       ├── AdminOrderController.java
│   │   │   │   │   │       └── AdminDashboardController.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── AuthService.java
│   │   │   │   │   │   ├── UserService.java
│   │   │   │   │   │   ├── ProductService.java
│   │   │   │   │   │   ├── CategoryService.java
│   │   │   │   │   │   ├── OrderService.java
│   │   │   │   │   │   ├── CartService.java
│   │   │   │   │   │   ├── FavoriteService.java
│   │   │   │   │   │   ├── ChatService.java
│   │   │   │   │   │   ├── ReviewService.java
│   │   │   │   │   │   └── FileService.java
│   │   │   │   │   │   └── impl/                        # Service 实现类
│   │   │   │   │   ├── mapper/                          # MyBatis-Plus Mapper
│   │   │   │   │   │   ├── UserMapper.java
│   │   │   │   │   │   ├── ProductMapper.java
│   │   │   │   │   │   ├── ProductImageMapper.java
│   │   │   │   │   │   ├── CategoryMapper.java
│   │   │   │   │   │   ├── OrderMapper.java
│   │   │   │   │   │   ├── CartMapper.java
│   │   │   │   │   │   ├── FavoriteMapper.java
│   │   │   │   │   │   ├── ChatMessageMapper.java
│   │   │   │   │   │   └── ReviewMapper.java
│   │   │   │   │   ├── entity/                          # 数据库实体
│   │   │   │   │   │   ├── User.java
│   │   │   │   │   │   ├── Product.java
│   │   │   │   │   │   ├── ProductImage.java
│   │   │   │   │   │   ├── Category.java
│   │   │   │   │   │   ├── Order.java
│   │   │   │   │   │   ├── OrderItem.java
│   │   │   │   │   │   ├── Cart.java
│   │   │   │   │   │   ├── Favorite.java
│   │   │   │   │   │   ├── ChatMessage.java
│   │   │   │   │   │   └── Review.java
│   │   │   │   │   ├── dto/                             # 数据传输对象
│   │   │   │   │   │   ├── request/
│   │   │   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   │   │   ├── ProductRequest.java
│   │   │   │   │   │   │   ├── OrderRequest.java
│   │   │   │   │   │   │   └── ...
│   │   │   │   │   │   └── response/
│   │   │   │   │   │       ├── ApiResponse.java         # 统一响应体
│   │   │   │   │   │       ├── LoginResponse.java
│   │   │   │   │   │       ├── ProductVO.java
│   │   │   │   │   │       └── ...
│   │   │   │   │   ├── security/
│   │   │   │   │   │   ├── JwtTokenProvider.java        # JWT 工具类
│   │   │   │   │   │   ├── JwtAuthenticationFilter.java # JWT 过滤器
│   │   │   │   │   │   └── UserDetailsServiceImpl.java  # 用户详情加载
│   │   │   │   │   ├── common/
│   │   │   │   │   │   ├── Result.java                  # 统一返回类
│   │   │   │   │   │   ├── PageResult.java              # 分页返回
│   │   │   │   │   │   ├── BusinessException.java       # 业务异常
│   │   │   │   │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   │   │   │   │   └── util/
│   │   │   │   │       ├── FileUtil.java
│   │   │   │   │       └── SnowflakeIdUtil.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml                  # 主配置
│   │   │   │       ├── application-dev.yml              # 开发环境
│   │   │   │       ├── application-prod.yml             # 生产环境
│   │   │   │       └── mapper/                          # MyBatis XML（如需）
│   │   │   └── test/
│   │   └── uploads/                                     # 本地上传目录
│   │
│   └── frontend/                        # Vue 3 前端
│       ├── package.json
│       ├── vite.config.ts
│       ├── index.html
│       ├── tsconfig.json
│       ├── src/
│       │   ├── main.ts                  # 入口
│       │   ├── App.vue                  # 根组件
│       │   ├── router/
│       │   │   └── index.ts             # 路由配置
│       │   ├── stores/                  # Pinia 状态管理
│       │   │   ├── user.ts              # 用户状态
│       │   │   ├── cart.ts              # 购物车状态
│       │   │   └── chat.ts              # 聊天状态
│       │   ├── api/                     # 接口请求层
│       │   │   ├── request.ts           # Axios 封装 + 拦截器
│       │   │   ├── auth.ts
│       │   │   ├── user.ts
│       │   │   ├── product.ts
│       │   │   ├── category.ts
│       │   │   ├── order.ts
│       │   │   ├── cart.ts
│       │   │   ├── favorite.ts
│       │   │   ├── chat.ts
│       │   │   └── review.ts
│       │   ├── views/                   # 页面视图
│       │   │   ├── HomeView.vue         # 首页
│       │   │   ├── LoginView.vue        # 登录
│       │   │   ├── RegisterView.vue     # 注册
│       │   │   ├── product/
│       │   │   │   ├── ProductList.vue  # 商品列表
│       │   │   │   ├── ProductDetail.vue# 商品详情
│       │   │   │   └── ProductPublish.vue# 发布商品
│       │   │   ├── order/
│       │   │   │   ├── OrderList.vue    # 订单列表
│       │   │   │   └── OrderDetail.vue  # 订单详情
│       │   │   ├── user/
│       │   │   │   ├── ProfileView.vue  # 个人主页
│       │   │   │   └── MyProducts.vue   # 我的发布
│       │   │   ├── chat/
│       │   │   │   └── ChatView.vue     # 聊天页
│       │   │   ├── favorite/
│       │   │   │   └── FavoriteList.vue # 收藏列表
│       │   │   └── admin/              # 后台管理
│       │   │       ├── AdminLayout.vue
│       │   │       ├── DashboardView.vue
│       │   │       ├── UserManage.vue
│       │   │       ├── ProductManage.vue
│       │   │       └── OrderManage.vue
│       │   ├── components/             # 公共组件
│       │   │   ├── layout/
│       │   │   │   ├── AppHeader.vue    # 顶部导航
│       │   │   │   └── AppFooter.vue    # 底部
│       │   │   ├── ProductCard.vue      # 商品卡片
│       │   │   ├── SearchBar.vue        # 搜索栏
│       │   │   ├── ImageUpload.vue      # 图片上传
│       │   │   └── Pagination.vue       # 分页器
│       │   ├── utils/
│       │   │   ├── index.ts             # 通用工具函数
│       │   │   └── validators.ts        # 表单校验
│       │   └── styles/
│       │       ├── variables.scss       # SCSS 变量
│       │       └── global.scss          # 全局样式
│       └── public/
│           └── favicon.ico
│
└── docs/
    └── sql/
        └── init.sql                     # 数据库初始化脚本
```

---

## 5. 数据库设计

### 5.1 E-R 概要

```
User 1───N Product    (用户发布多个商品)
User 1───N Order      (用户有多个订单)
User 1───N Review     (用户发表多条评价)
Product 1───N OrderItem   (一件商品可被多次购买)
Product 1───N ProductImage (一件商品多张图片)
Product 1───N Favorite    (一件商品可被多人收藏)
Product N───1 Category    (商品属于一个分类)
Order 1───N OrderItem    (一个订单包含多个商品项)
```

### 5.2 表结构设计

#### 用户表 `user`

| 字段          | 类型          | 说明                       |
|---------------|---------------|----------------------------|
| id            | BIGINT        | 主键，雪花ID                |
| username      | VARCHAR(32)   | 用户名，唯一                |
| password      | VARCHAR(128)  | BCrypt加密密文             |
| nickname      | VARCHAR(32)   | 昵称                       |
| avatar        | VARCHAR(255)  | 头像URL                    |
| phone         | VARCHAR(16)   | 手机号                     |
| email         | VARCHAR(64)   | 邮箱                       |
| school        | VARCHAR(64)   | 学校                       |
| campus        | VARCHAR(32)   | 校区                       |
| role          | TINYINT       | 角色：0=用户 1=管理员      |
| status        | TINYINT       | 状态：0=正常 1=封禁        |
| credit_score  | INT           | 信誉分，默认100             |
| created_at    | DATETIME      | 创建时间                   |
| updated_at    | DATETIME      | 更新时间                   |

#### 商品分类表 `category`

| 字段      | 类型         | 说明               |
|-----------|--------------|--------------------|
| id        | BIGINT       | 主键               |
| name      | VARCHAR(32)  | 分类名（教材/数码/衣物...）|
| icon      | VARCHAR(255) | 分类图标URL         |
| sort_order| INT          | 排序               |
| created_at| DATETIME     | 创建时间            |

#### 商品表 `product`

| 字段         | 类型          | 说明                                   |
|--------------|---------------|----------------------------------------|
| id           | BIGINT        | 主键                                   |
| user_id      | BIGINT        | 发布者ID                               |
| category_id  | BIGINT        | 分类ID                                 |
| title        | VARCHAR(128)  | 标题                                   |
| description  | TEXT          | 描述                                   |
| price        | DECIMAL(10,2) | 价格                                   |
| original_price| DECIMAL(10,2)| 原价（选填）                            |
| condition    | TINYINT       | 新旧：1=全新 2=几乎全新 3=有使用痕迹   |
| campus       | VARCHAR(32)   | 交易校区                               |
| status       | TINYINT       | 0=待审核 1=在售 2=已售出 3=已下架      |
| view_count   | INT           | 浏览量，默认0                          |
| created_at   | DATETIME      | 发布时间                               |
| updated_at   | DATETIME      | 更新时间                               |

#### 商品图片表 `product_image`

| 字段       | 类型         | 说明          |
|------------|--------------|---------------|
| id         | BIGINT       | 主键          |
| product_id | BIGINT       | 商品ID        |
| url        | VARCHAR(255) | 图片URL       |
| sort_order | INT          | 排序（首图=1） |

#### 订单表 `order`

| 字段          | 类型          | 说明                                              |
|---------------|---------------|---------------------------------------------------|
| id            | BIGINT        | 主键                                              |
| order_no      | VARCHAR(32)   | 订单编号（业务唯一）                               |
| buyer_id      | BIGINT        | 买家ID                                            |
| seller_id     | BIGINT        | 卖家ID                                            |
| total_amount  | DECIMAL(10,2) | 总金额                                            |
| status        | TINYINT       | 0=待付款 1=待发货 2=待收货 3=已完成 4=已取消      |
| remark        | VARCHAR(255)  | 备注                                              |
| created_at    | DATETIME      | 下单时间                                          |
| paid_at       | DATETIME      | 付款时间                                          |
| shipped_at    | DATETIME      | 发货时间                                          |
| completed_at  | DATETIME      | 完成时间                                          |

#### 订单明细表 `order_item`

| 字段       | 类型          | 说明       |
|------------|---------------|------------|
| id         | BIGINT        | 主键       |
| order_id   | BIGINT        | 订单ID     |
| product_id | BIGINT        | 商品ID     |
| price      | DECIMAL(10,2) | 成交单价   |
| created_at | DATETIME      | 创建时间   |

#### 购物车表 `cart`

| 字段       | 类型     | 说明           |
|------------|----------|----------------|
| id         | BIGINT   | 主键           |
| user_id    | BIGINT   | 用户ID         |
| product_id | BIGINT   | 商品ID         |
| created_at | DATETIME | 添加时间       |

#### 收藏表 `favorite`

| 字段       | 类型     | 说明     |
|------------|----------|----------|
| id         | BIGINT   | 主键     |
| user_id    | BIGINT   | 用户ID   |
| product_id | BIGINT   | 商品ID   |
| created_at | DATETIME | 收藏时间 |

#### 聊天消息表 `chat_message`

| 字段          | 类型         | 说明                             |
|---------------|--------------|----------------------------------|
| id            | BIGINT       | 主键                             |
| sender_id     | BIGINT       | 发送者ID                         |
| receiver_id   | BIGINT       | 接收者ID                         |
| product_id    | BIGINT       | 关联商品ID（可为空）              |
| content       | TEXT         | 消息内容                         |
| is_read       | TINYINT      | 0=未读 1=已读                    |
| created_at    | DATETIME     | 发送时间                         |

#### 评价表 `review`

| 字段       | 类型         | 说明                         |
|------------|--------------|------------------------------|
| id         | BIGINT       | 主键                         |
| order_id   | BIGINT       | 订单ID                       |
| reviewer_id| BIGINT       | 评价者ID                     |
| target_id  | BIGINT       | 被评价者ID                   |
| rating     | TINYINT      | 评分 1-5                     |
| content    | VARCHAR(500) | 评价内容                     |
| created_at | DATETIME     | 评价时间                     |

> 索引建议：
> - `user(username) UNIQUE`、`product(category_id, status)`、`product(created_at)`
> - `order(buyer_id, status)`、`order(seller_id, status)`
> - `chat_message(sender_id, receiver_id)`、`chat_message(receiver_id, is_read)`
> - 所有外键字段加普通索引

---

## 6. API 接口设计

### 6.1 通用约定

- 基础路径: `http://localhost:8080/api`
- 请求格式: `application/json`
- 认证方式: Header `Authorization: Bearer <token>`
- 统一响应格式:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- 分页响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "pageSize": 10
  }
}
```

### 6.2 接口列表

#### 认证模块 `/api/auth`

| 方法   | 路径              | 说明       | 认证 |
|--------|-------------------|------------|------|
| POST   | /auth/register    | 注册       | 否   |
| POST   | /auth/login       | 登录       | 否   |
| POST   | /auth/logout      | 退出       | 是   |
| GET    | /auth/me          | 当前用户信息| 是   |

#### 用户模块 `/api/user`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| GET    | /user/{id}        | 用户详情       | 否   |
| PUT    | /user/profile     | 编辑个人资料    | 是   |
| PUT    | /user/password    | 修改密码       | 是   |
| POST   | /user/avatar      | 上传头像       | 是   |

#### 商品模块 `/api/products`

| 方法   | 路径                     | 说明            | 认证 |
|--------|--------------------------|-----------------|------|
| GET    | /products                | 商品列表（搜索+筛选+分页）| 否   |
| GET    | /products/{id}           | 商品详情        | 否   |
| POST   | /products                | 发布商品        | 是   |
| PUT    | /products/{id}           | 编辑商品        | 是   |
| PUT    | /products/{id}/status    | 下架商品        | 是   |
| GET    | /products/my             | 我的发布列表     | 是   |

#### 分类模块 `/api/categories`

| 方法   | 路径              | 说明       | 认证 |
|--------|-------------------|------------|------|
| GET    | /categories       | 全部分类   | 否   |

#### 订单模块 `/api/orders`

| 方法   | 路径                     | 说明            | 认证 |
|--------|--------------------------|-----------------|------|
| POST   | /orders                  | 创建订单        | 是   |
| GET    | /orders                  | 订单列表（支持状态筛选）| 是   |
| GET    | /orders/{id}             | 订单详情        | 是   |
| PUT    | /orders/{id}/pay         | 模拟付款        | 是   |
| PUT    | /orders/{id}/ship        | 卖家发货        | 是   |
| PUT    | /orders/{id}/confirm     | 确认收货        | 是   |
| PUT    | /orders/{id}/cancel      | 取消订单        | 是   |

#### 购物车模块 `/api/cart`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| GET    | /cart             | 购物车列表     | 是   |
| POST   | /cart             | 加入购物车     | 是   |
| DELETE | /cart/{id}        | 移除商品       | 是   |

#### 收藏模块 `/api/favorites`

| 方法   | 路径                     | 说明            | 认证 |
|--------|--------------------------|-----------------|------|
| GET    | /favorites               | 收藏列表        | 是   |
| POST   | /favorites               | 添加收藏        | 是   |
| DELETE | /favorites/{productId}   | 取消收藏        | 是   |
| GET    | /favorites/check/{productId} | 是否已收藏  | 是   |

#### 聊天模块 `/api/chat`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| GET    | /chat/contacts    | 会话联系人列表 | 是   |
| GET    | /chat/{contactId} | 历史消息       | 是   |
| POST   | /chat/send        | 发送消息       | 是   |
| PUT    | /chat/read/{contactId} | 标记已读  | 是   |
| WS     | /ws/chat          | WebSocket连接  | 是   |

#### 评价模块 `/api/reviews`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| POST   | /reviews          | 发表评价       | 是   |
| GET    | /reviews/user/{userId} | 用户评价列表| 否   |

#### 文件上传 `/api/files`

| 方法   | 路径              | 说明           | 认证 |
|--------|-------------------|----------------|------|
| POST   | /files/upload     | 单文件上传     | 是   |
| POST   | /files/uploads    | 多文件上传     | 是   |

#### 后台管理 `/api/admin/...`

| 方法   | 路径                       | 说明           | 认证 |
|--------|----------------------------|----------------|------|
| GET    | /admin/dashboard           | 仪表盘数据     | 是(管理员) |
| GET    | /admin/users               | 用户列表       | 是(管理员) |
| PUT    | /admin/users/{id}/status   | 封禁/解封用户  | 是(管理员) |
| GET    | /admin/products            | 商品列表       | 是(管理员) |
| PUT    | /admin/products/{id}/audit | 审核商品       | 是(管理员) |
| GET    | /admin/orders              | 订单列表       | 是(管理员) |
| POST   | /admin/categories          | 新增分类       | 是(管理员) |
| PUT    | /admin/categories/{id}     | 编辑分类       | 是(管理员) |
| DELETE | /admin/categories/{id}     | 删除分类       | 是(管理员) |

---

## 7. 开发环境搭建

### 7.1 环境要求

| 工具      | 版本要求    | 说明               |
|-----------|------------|--------------------|
| JDK       | 21+        | Spring Boot 4 需要 |
| Maven     | 3.9+       | 后端构建            |
| Node.js   | 18+ / 20+  | 前端构建            |
| pnpm      | 8+         | 推荐包管理器         |
| MySQL     | 8.0+       | 数据库              |
| Redis     | 7.x        | 缓存（可选）         |
| IDE       | IntelliJ IDEA / VS Code | 开发工具   |

### 7.2 后端启动

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE cuzssp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 导入初始数据
mysql -u root -p cuzssp < docs/sql/init.sql

# 3. 修改 application-dev.yml 中的数据库连接信息

# 4. 启动后端
cd CuzShortSemester2026/code/backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 7.3 前端启动

```bash
cd CuzShortSemester2026/code/frontend
pnpm install
pnpm dev
# 访问 http://localhost:5173
```

### 7.4 `application-dev.yml` 关键配置

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cuzssp?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 开发阶段打印SQL
  global-config:
    db-config:
      id-type: assign_id   # 雪花算法ID

jwt:
  secret: your-jwt-secret-key-at-least-256-bits-long
  expiration: 86400000    # 24小时, 毫秒

r2:
  endpoint: https://<your-account-id>.r2.cloudflarestorage.com
  access-key-id: your_r2_access_key_id
  secret-access-key: your_r2_secret_access_key
  bucket-name: cuz-trade-images
  region: auto
  public-url: https://your-domain.com  # 自定义域名或 R2 公共 URL
```

---

## 8. 开发规范

### 8.1 Git 分支策略

```
main        — 生产分支
├── dev     — 开发主分支
│   ├── feature/user-login     — 功能分支
│   ├── feature/product-list
│   └── ...
└── release/v1.0 — 发布分支
```

### 8.2 命名规范

| 类别       | 规范                     | 示例                         |
|------------|--------------------------|------------------------------|
| Java 类名  | UpperCamelCase           | `ProductController`          |
| Java 方法  | lowerCamelCase           | `getProductList()`           |
| 数据库表   | snake_case               | `product_image`              |
| 数据库字段 | snake_case               | `created_at`                 |
| RESTful URL| 短横线小写+资源名复数     | `/api/products/{id}`         |
| Vue 组件   | PascalCase               | `ProductCard.vue`            |
| Vue 文件   | kebab-case 或 PascalCase | `product-list.vue`           |
| CSS 类名   | kebab-case               | `.product-card`              |

### 8.3 代码规范

- **后端**: 遵循阿里巴巴Java开发手册, Controller → Service → Mapper 三层架构
- **前端**: ESLint + Prettier 统一格式化, Composition API (`<script setup>`) 风格
- **注释**: JavaDoc 注释 public 方法, 复杂逻辑加行内注释
- **异常处理**: 统一用 `@RestControllerAdvice` 全局异常处理, 业务异常抛 `BusinessException`

### 8.4 提交规范

遵循 Conventional Commits:

```
feat: 新增用户注册功能
fix: 修复商品列表分页Bug
docs: 更新API文档
style: 格式化代码
refactor: 重构订单Service
test: 添加用户模块单元测试
```

---

## 9. 开发顺序建议

| 阶段 | 内容                                           | 预计周期 |
|------|------------------------------------------------|----------|
| P1   | 项目初始化 + 数据库建表 + 用户认证模块          | 3 天     |
| P2   | 商品模块（CRUD + 搜索 + 分类）                  | 4 天     |
| P3   | 订单模块 + 购物车 + 收藏                        | 4 天     |
| P4   | 聊天模块（WebSocket）                           | 3 天     |
| P5   | 评价系统 + 用户信誉                             | 2 天     |
| P6   | 后台管理端全部功能                              | 4 天     |
| P7   | 文件上传 + 图片处理 + 前端联调 + 测试           | 3 天     |

---

## 10. 部署说明

### 后端部署

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar -Dspring.profiles.active=prod backend/target/cuz-trade-1.0.0.jar
```

### 前端部署

```bash
pnpm build
# dist/ 目录部署到 Nginx
```

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name trade.example.com;

    # 前端
    location / {
        root /var/www/cuz-trade/dist;
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # WebSocket 代理
    location /ws/ {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

---

> **文档维护**: 本 DEV.md 随项目迭代持续更新，每次功能变更需同步修改对应章节。
