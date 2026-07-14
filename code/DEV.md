# 校园二手交易平台 — 开发说明文档 (DEV.md)

> **版本**: v2.0  
> **日期**: 2026-07-14  
> **项目名**: CuzShortSemester2026 — Campus Second-Hand Trading Platform  
> **说明**: 本文档基于项目实际代码结构编写，反映当前代码的真实状态。

---

## 1. 项目概述

本项目是一个面向高校学生的校园二手物品交易 Web 应用，旨在为校内用户提供一个安全、便捷的二手商品发布、浏览、交易与交流平台。系统采用前后端分离架构，分为前台用户端和后台管理端，支持商品分类浏览、关键词搜索、条件筛选、在线聊天（WebSocket）、订单管理、评价系统等核心功能。图片存储使用 Cloudflare R2（S3 兼容 API）。

---

## 2. 技术栈（实际版本）

| 层级       | 技术选择                              | 实际版本       |
|------------|---------------------------------------|----------------|
| 后端框架   | Spring Boot                           | **4.0.7**      |
| 安全框架   | Spring Security + JWT (jjwt)          | **0.12.6**     |
| ORM        | MyBatis-Plus (spring-boot4-starter)   | **3.5.16**     |
| 数据库驱动 | MySQL Connector/J                     | 随 Spring Boot |
| 分页插件   | PageHelper                            | **2.1.1**      |
| 对象存储   | AWS SDK for Java S3 (Cloudflare R2)   | **2.46.21**    |
| 开发工具   | Lombok                                | 随 Spring Boot |
| Java 版本  | Java                                  | **17**         |
| 数据库     | MySQL                                 | 8.0+           |
| 前端框架   | Vue 3 + TypeScript                    | 3.x            |
| 前端路由   | Vue Router                            | 4.x            |
| 状态管理   | Pinia                                 | 2.x            |
| UI 组件库  | Element Plus                          | 2.x            |
| HTTP 客户端| Axios                                  | 1.x            |
| 构建工具   | Vite                                  | 5.x            |
| 包管理     | pnpm                                  | —              |

> **注意**: 实际项目**未使用 Redis**，无缓存层。密码加密采用自定义 Base64 + 盐值方案，**非 BCrypt**。

---

## 3. 需求分析

### 3.1 用户角色

| 角色常量                        | 值 | 描述                                       |
|---------------------------------|----|--------------------------------------------|
| `UserConstant.ROLE_USER`        | 0  | 普通用户：注册登录、浏览商品、发布商品、交易、聊天 |
| `UserConstant.ROLE_ADMIN`       | 1  | 管理员：用户管理、商品审核、订单管理、分类管理   |

用户状态:
- `UserConstant.STATUS_ABLE` (0) — 正常
- `UserConstant.STATUS_DISABLE` (1) — 封禁

### 3.2 功能模块

#### 前台用户端（已实现）

| 模块     | 功能点                                                                           | Controller            |
|----------|----------------------------------------------------------------------------------|-----------------------|
| 用户认证 | 注册、登录、登出、获取当前用户信息 (`/me`)                                        | `AuthController`      |
| 用户管理 | 查看用户信息、编辑个人资料、修改密码、上传头像                                     | `UserController`      |
| 商品浏览 | 分页列表、关键词搜索、分类+校区联合筛选、商品详情（含浏览量计数）、我发布的列表      | `ProductController`   |
| 商品发布 | 发布商品（含图片）、编辑商品、修改商品状态（上架/下架）                              | `ProductController`   |
| 收藏系统 | 收藏/取消收藏、收藏列表（分页）、检查是否已收藏                                    | `FavoriteController`  |
| 购物车   | 加入购物车、移除、查看购物车列表                                                  | `CartController`      |
| 订单系统 | 创建订单、订单列表（分页+状态筛选）、详情、支付/发货/收货/取消                       | `OrderController`     |
| 聊天系统 | 联系人列表、消息记录（分页）、发送消息、标记已读（WebSocket 推送 + HTTP REST）       | `ChatController`      |
| 评价系统 | 创建评价（星级+文字）、查看用户评价记录（分页）、评分影响信誉分                      | `ReviewController`    |
| 文件上传 | 单文件上传、多文件上传（上传至 Cloudflare R2）                                     | `FileController`      |
| 分类浏览 | 获取全部分类（含各分类商品数）                                                     | `CategoryController`  |

#### 后台管理端（已实现）

| 模块     | 功能点                                                                 | Controller               |
|----------|------------------------------------------------------------------------|---------------------------|
| 仪表盘   | 用户总数、商品总数、订单总数、交易总额、今日新增用户、今日新增订单        | `AdminDashboardController`|
| 用户管理 | 用户列表（分页+关键词搜索）、封禁/解封                                    | `AdminDashboardController`|
| 商品管理 | 商品列表（分页+关键词+状态筛选）、审核/下架                               | `AdminDashboardController`|
| 订单管理 | 订单列表（分页+状态筛选）                                                | `AdminDashboardController`|

> 分类的增删改查通过 `CategoryController` 实现（未纳入 admin 路径），公告模块实体已定义但前端和后台管理功能尚未接入。

### 3.3 非功能性需求

- **安全性**: 密码自定义编码存储（Base64 + 盐值 `cuzssp...2026webproject`），JWT 令牌鉴权（Spring Security Filter），参数化 SQL 防注入
- **图片存储**: Cloudflare R2（S3 兼容 API），使用 AWS SDK for Java v2
- **前后端分离**: RESTful API 设计，JSON 通信
- **WebSocket**: 聊天消息实时推送（路径 `/ws/chat?userId=`）

---

## 4. 项目目录结构（实际）

```
CuzShortSemester2026/
└── code/
    ├── DEV.md                                              # 本开发说明文档
    ├── CampusSecondHandTradingPlatform_backend/            # Spring Boot 后端（实际目录名）
    │   ├── pom.xml                                         # Maven 配置 (Spring Boot 4.0.7)
    │   ├── mvnw / mvnw.cmd                                 # Maven Wrapper
    │   ├── HELP.md                                         # Spring Boot 官方参考
    │   └── src/
    │       ├── main/
    │       │   ├── java/com/cuzssp/campussecondhandtradingplatform_backend/
    │       │   │   ├── CampusSecondHandTradingPlatformBackendApplication.java  # 启动类
    │       │   │   ├── controller/
    │       │   │   │   ├── AdminDashboardController.java   # 后台管理：仪表盘/用户/商品/订单管理
    │       │   │   │   ├── AuthController.java             # 认证：注册/登录/登出/me
    │       │   │   │   ├── CartController.java             # 购物车：增删查
    │       │   │   │   ├── CategoryController.java         # 分类：CRUD
    │       │   │   │   ├── ChatController.java             # 聊天：联系人/消息/发送/已读
    │       │   │   │   ├── FavoriteController.java         # 收藏：增删查/校验
    │       │   │   │   ├── FileController.java             # 文件：单/多文件上传
    │       │   │   │   ├── OrderController.java            # 订单：创建/列表/详情/支付/发货/收货/取消
    │       │   │   │   ├── ProductController.java          # 商品：列表/详情/发布/编辑/状态/我的
    │       │   │   │   ├── ReviewController.java           # 评价：创建/查看
    │       │   │   │   └── UserController.java             # 用户：信息/资料/密码/头像
    │       │   │   ├── service/
    │       │   │   │   ├── AdminService.java
    │       │   │   │   ├── AuthService.java
    │       │   │   │   ├── CartService.java
    │       │   │   │   ├── CategoryService.java
    │       │   │   │   ├── ChatService.java
    │       │   │   │   ├── FavoriteService.java
    │       │   │   │   ├── FileService.java
    │       │   │   │   ├── OrderService.java
    │       │   │   │   ├── ProductService.java
    │       │   │   │   ├── ReviewService.java
    │       │   │   │   └── UserService.java
    │       │   │   ├── service/impl/                       # 以上接口的实现类
    │       │   │   ├── mapper/                             # MyBatis 注解式 Mapper（无 XML）
    │       │   │   │   ├── AnnouncementMapper.java
    │       │   │   │   ├── CartMapper.java
    │       │   │   │   ├── CategoryMapper.java
    │       │   │   │   ├── ChatMessageMapper.java
    │       │   │   │   ├── FavoriteMapper.java
    │       │   │   │   ├── OrderItemMapper.java
    │       │   │   │   ├── OrderMapper.java
    │       │   │   │   ├── ProductImageMapper.java
    │       │   │   │   ├── ProductMapper.java
    │       │   │   │   ├── ReviewMapper.java
    │       │   │   │   └── UserMapper.java
    │       │   │   └── common/
    │       │   │       ├── config/
    │       │   │       │   ├── ChatWebSocketHandler.java   # WebSocket 消息处理（基于 userId 的会话管理）
    │       │   │       │   ├── CorsConfig.java             # CORS 配置（当前为空类）
    │       │   │       │   ├── MyBatisPlusConfig.java      # MyBatis-Plus 配置（当前为空类）
    │       │   │       │   ├── MyMetaObjectHandler.java    # 自动填充 createdAt/updatedAt
    │       │   │       │   ├── S3Config.java               # R2/S3 配置属性绑定
    │       │   │       │   └── WebSocketConfig.java        # WebSocket 注册 (/ws/chat)
    │       │   │       ├── constant/
    │       │   │       │   ├── OrderInfoConstant.java      # 订单状态常量
    │       │   │       │   ├── ProductConstant.java        # 商品状态/新旧常量
    │       │   │       │   └── UserConstant.java           # 用户角色/状态/信誉常量
    │       │   │       ├── dto/                            # 数据传输对象（请求体）
    │       │   │       │   ├── ChangePasswordRequest.java
    │       │   │       │   ├── CreateOrderRequest.java
    │       │   │       │   ├── LoginRequest.java
    │       │   │       │   ├── ProductQueryDTO.java
    │       │   │       │   ├── RegisterRequest.java
    │       │   │       │   ├── ReviewRequest.java
    │       │   │       │   ├── SendMessageRequest.java
    │       │   │       │   └── UpdateProfileRequest.java
    │       │   │       ├── entity/                         # 数据库实体（11 张表）
    │       │   │       │   ├── Announcement.java           # 公告表 announcement
    │       │   │       │   ├── CartItem.java               # 购物车表 cart
    │       │   │       │   ├── Category.java               # 分类表 category
    │       │   │       │   ├── ChatMessage.java            # 聊天消息表 chat_message
    │       │   │       │   ├── Favorite.java               # 收藏表 favorite
    │       │   │       │   ├── OrderInfo.java              # 订单表 order
    │       │   │       │   ├── OrderItem.java              # 订单项表 order_item
    │       │   │       │   ├── Product.java                # 商品表 product
    │       │   │       │   ├── ProductImage.java           # 商品图片表 product_image
    │       │   │       │   ├── Review.java                 # 评价表 review
    │       │   │       │   └── User.java                   # 用户表 user
    │       │   │       ├── exception/
    │       │   │       │   ├── BusinessException.java      # 业务异常
    │       │   │       │   └── GlobalExceptionHandler.java # 全局异常处理 (@RestControllerAdvice)
    │       │   │       ├── security/
    │       │   │       │   ├── Base64Provider.java         # 密码编码/匹配（Base64 + 盐值）
    │       │   │       │   ├── JwtAuthenticationFilter.java# JWT 认证过滤器
    │       │   │       │   ├── JwtTokenProvider.java       # JWT 生成/解析/验证
    │       │   │       │   └── UserDetailsServiceImpl.java # Spring Security UserDetailsService
    │       │   │       ├── util/
    │       │   │       │   ├── CloudflareR2Client.java     # R2 S3 客户端封装
    │       │   │       │   ├── FileUtil.java               # 文件上传工具（上传至 R2）
    │       │   │       │   ├── ToEntityUtil.java           # DTO → Entity 转换工具
    │       │   │       │   └── ToVOUtil.java               # Entity → VO 转换工具
    │       │   │       └── vo/                             # 视图对象（响应体）
    │       │   │           ├── CartItemVO.java
    │       │   │           ├── CategoryVO.java
    │       │   │           ├── ChatContactVO.java
    │       │   │           ├── ChatMessageVO.java
    │       │   │           ├── DashboardVO.java
    │       │   │           ├── OrderItemVO.java
    │       │   │           ├── OrderVO.java
    │       │   │           ├── PageResult.java             # 通用分页响应
    │       │   │           ├── ProductVO.java
    │       │   │           ├── Result.java                 # 统一响应格式
    │       │   │           ├── ReviewVO.java
    │       │   │           └── UserVO.java
    │       │   └── resources/
    │       │       ├── application.yml                    # 主配置（环境变量占位符）
    │       │       ├── .env                                # 环境变量配置（敏感信息，Git 忽略）
    │       │       └── .env.example                        # 环境变量模板
    │       └── test/                                       # 测试目录（当前为空）
    └── campus-second-hand-trading-platform-frontend/      # Vue 3 前端（实际目录名）
        ├── package.json
        ├── pnpm-lock.yaml
        ├── vite.config.ts
        ├── tsconfig.json / tsconfig.app.json / tsconfig.node.json
        ├── index.html
        ├── README.md
        └── src/                                           # 前端源码
```

---

## 5. 数据库设计（实际表结构）

### 5.1 数据库信息

- **数据库名**: cuzssp
- **字符集**: utf8mb4 / utf8mb4_unicode_ci
- **ORM 方式**: MyBatis-Plus 注解式 Mapper（`@Select` / `@Insert` / `@Update`），**无 XML 映射文件**
- **主键策略**: `IdType.ASSIGN_ID`（雪花算法）

### 5.2 核心表结构

#### 5.2.1 用户表 `user`

| 字段         | 类型          | 说明                          |
|--------------|---------------|-------------------------------|
| id           | BIGINT (PK)   | 雪花ID                        |
| username     | VARCHAR       | 用户名（唯一）                 |
| password     | VARCHAR       | 密码（Base64+盐值编码存储）    |
| nickname     | VARCHAR       | 昵称                          |
| avatar       | VARCHAR       | 头像 URL                      |
| phone        | VARCHAR       | 手机号                        |
| email        | VARCHAR       | 邮箱                          |
| school       | VARCHAR       | 学校                          |
| campus       | VARCHAR       | 校区                          |
| role         | INT           | 0=用户 1=管理员               |
| status       | INT           | 0=正常 1=封禁                 |
| credit_score | INT           | 信誉分，默认 100               |
| created_at   | DATETIME      | 创建时间                      |
| updated_at   | DATETIME      | 更新时间                      |

#### 5.2.2 商品表 `product`

| 字段          | 类型            | 说明                                    |
|---------------|-----------------|-----------------------------------------|
| id            | BIGINT (PK)     | 雪花ID                                  |
| user_id       | BIGINT (FK)     | 发布者（卖家）                           |
| category_id   | BIGINT (FK)     | 分类ID                                  |
| title         | VARCHAR         | 标题                                    |
| description   | TEXT            | 描述                                    |
| price         | DECIMAL(10,2)   | 售价                                    |
| original_price| DECIMAL(10,2)   | 原价                                    |
| condition     | INT             | 0=全新 1=几乎全新 2=有使用痕迹           |
| campus        | VARCHAR         | 所在校区                                 |
| status        | INT             | 0=待审核 1=在售 2=已售出 3=已下架         |
| view_count    | INT             | 浏览量，默认 0                           |
| created_at    | DATETIME        | 创建时间                                 |
| updated_at    | DATETIME        | 更新时间                                 |

#### 5.2.3 订单表 `order`（SQL 关键字，代码中以 `OrderInfo` 实体映射）

| 字段         | 类型            | 说明                                                    |
|--------------|-----------------|---------------------------------------------------------|
| id           | BIGINT (PK)     | 雪花ID                                                  |
| order_no     | VARCHAR         | 订单编号（UUID 前20位）                                   |
| buyer_id     | BIGINT (FK)     | 买家                                                    |
| seller_id    | BIGINT (FK)     | 卖家                                                    |
| total_amount | DECIMAL(10,2)   | 总金额                                                   |
| status       | INT             | 0=待付款 1=待发货 2=待收货 3=已完成 4=已取消               |
| remark       | VARCHAR         | 备注                                                    |
| created_at   | DATETIME        | 创建时间                                                 |
| paid_at      | DATETIME        | 支付时间                                                 |
| shipped_at   | DATETIME        | 发货时间                                                 |
| completed_at | DATETIME        | 完成时间                                                 |

#### 5.2.4 其他表

| 表名             | 对应实体          | 关键字段                                |
|------------------|-------------------|-----------------------------------------|
| order_item       | OrderItem         | order_id, product_id, price             |
| product_image    | ProductImage      | product_id, url, sort_order             |
| category         | Category          | name, icon, sort_order                  |
| cart             | CartItem          | user_id, product_id                     |
| favorite         | Favorite          | user_id, product_id                     |
| chat_message     | ChatMessage       | sender_id, receiver_id, product_id, content, is_read |
| review           | Review            | order_id, reviewer_id, target_id, rating, content |
| announcement     | Announcement      | title, content                          |

---

## 6. API 接口文档（基于实际 Controller）

### 6.1 统一响应格式 `Result<T>`

```json
{ "code": 200, "message": "success", "data": ... }
```

分页响应 `PageResult<T>`:
```json
{ "code": 200, "message": "success", "data": { "records": [...], "total": 100, "page": 1, "pageSize": 12 } }
```

### 6.2 认证模块 — `/api/auth`

| 方法   | 路径              | 认证 | 说明               | 请求体/参数                        |
|--------|-------------------|------|--------------------|------------------------------------|
| POST   | /api/auth/register| 否   | 用户注册           | `RegisterRequest` (JSON)           |
| POST   | /api/auth/login   | 否   | 用户登录，返回 JWT | `LoginRequest` (JSON)              |
| POST   | /api/auth/logout  | 是   | 登出               | Header: `Authorization: Bearer {}` |
| GET    | /api/auth/me      | 是   | 获取当前用户信息   | Header: `Authorization: Bearer {}` |

### 6.3 用户模块 — `/api/user`

| 方法 | 路径                | 认证 | 说明         | 请求体/参数                       |
|------|---------------------|------|--------------|-----------------------------------|
| GET  | /api/user/{id}      | 否   | 查看用户信息 | —                                 |
| PUT  | /api/user/profile   | 是   | 修改个人资料 | `UpdateProfileRequest` (JSON)     |
| PUT  | /api/user/password  | 是   | 修改密码     | `ChangePasswordRequest` (JSON)    |
| POST | /api/user/avatar    | 是   | 修改头像     | `?image=URL`                      |

### 6.4 商品模块 — `/api/products`

| 方法 | 路径                     | 认证   | 说明                     | 参数                                                          |
|------|--------------------------|--------|--------------------------|---------------------------------------------------------------|
| GET  | /api/products            | 可选   | 商品列表（分页+筛选）    | `?keyword=&categoryId=&campus=&page=&pageSize=`               |
| GET  | /api/products/{id}       | 可选   | 商品详情（浏览量+1）     | —                                                             |
| POST | /api/products            | 是     | 发布商品（待审核）       | Body: Product JSON + `?images=url1&images=url2`               |
| PUT  | /api/products/{id}       | 是     | 修改商品（回退待审核）   | Body: Product JSON + `?images=...`                            |
| PUT  | /api/products/{id}/status| 是     | 修改商品状态             | `?status=0/1/2/3`                                             |
| GET  | /api/products/my         | 是     | 我发布的商品列表         | `?page=&pageSize=`                                            |

### 6.5 收藏模块 — `/api/favorites`

| 方法   | 路径                          | 认证 | 说明           |
|--------|-------------------------------|------|----------------|
| GET    | /api/favorites                | 是   | 收藏列表（分页）|
| POST   | /api/favorites                | 是   | 添加收藏       |
| DELETE | /api/favorites/{productId}    | 是   | 取消收藏       |
| GET    | /api/favorites/check/{productId}| 是 | 是否已收藏    |

### 6.6 购物车模块 — `/api/cart`

| 方法   | 路径               | 认证 | 说明       |
|--------|--------------------|------|------------|
| GET    | /api/cart          | 是   | 购物车列表 |
| POST   | /api/cart          | 是   | 加入购物车 (`?productId=`) |
| DELETE | /api/cart/{productId}| 是 | 移除商品  |

### 6.7 订单模块 — `/api/orders`

| 方法 | 路径                  | 认证 | 说明                          |
|------|-----------------------|------|-------------------------------|
| POST | /api/orders           | 是   | 创建订单（商品变已售出）      |
| GET  | /api/orders           | 是   | 订单列表 `?status=&page=&pageSize=` |
| GET  | /api/orders/{id}      | 是   | 订单详情                      |
| PUT  | /api/orders/{id}/pay  | 是   | 支付（无实际支付逻辑）        |
| PUT  | /api/orders/{id}/ship | 是   | 卖家发货                      |
| PUT  | /api/orders/{id}/confirm| 是 | 买家确认收货                  |
| PUT  | /api/orders/{id}/cancel| 是  | 取消订单（恢复商品在售）      |

> 订单状态流转: 待付款(0) → 待发货(1) → 待收货(2) → 已完成(3)；任意状态 → 已取消(4)

### 6.8 聊天模块 — `/api/chat`

| 方法 | 路径                   | 认证 | 说明                 |
|------|------------------------|------|----------------------|
| GET  | /api/chat/contacts     | 是   | 联系人列表（含未读数）|
| GET  | /api/chat/{contactId}  | 是   | 消息记录 `?page=&pageSize=` |
| POST | /api/chat/send         | 是   | 发送消息 `SendMessageRequest` |
| PUT  | /api/chat/read/{contactId}| 是| 标记已读             |

WebSocket 连接: `ws://host:port/ws/chat?userId={userId}`（用于接收实时推送）

### 6.9 评价模块 — `/api/reviews`

| 方法 | 路径                    | 认证 | 说明                        |
|------|-------------------------|------|-----------------------------|
| POST | /api/reviews            | 是   | 创建评价（评分影响信誉分）  |
| GET  | /api/reviews/user/{userId}| 否 | 查看用户评价记录（分页）    |

### 6.10 文件上传 — `/api/files`

| 方法 | 路径               | 认证 | 说明                  |
|------|--------------------|------|-----------------------|
| POST | /api/files/upload  | 否   | 单文件上传 `multipart` |
| POST | /api/files/uploads | 否   | 多文件上传 `multipart` |

### 6.11 分类管理 — `/api/categories`

| 方法   | 路径                  | 认证 | 说明                    |
|--------|-----------------------|------|-------------------------|
| GET    | /api/categories       | 否   | 获取所有分类（含商品数）|
| POST   | /api/categories       | 否   | 新增分类                |
| PUT    | /api/categories/{id}  | 否   | 修改分类                |
| DELETE | /api/categories/{id}  | 否   | 删除分类（有商品则拒绝）|

### 6.12 后台管理 — `/api/admin`

| 方法 | 路径                           | 说明                              |
|------|--------------------------------|-----------------------------------|
| GET  | /api/admin/dashboard           | 仪表盘统计数据                    |
| GET  | /api/admin/users               | 用户列表 `?page=&pageSize=&keyword=`|
| PUT  | /api/admin/users/{id}/status   | 封禁/解封 `?status=0/1`           |
| GET  | /api/admin/products            | 商品列表 `?page=&pageSize=&keyword=&status=`|
| PUT  | /api/admin/products/{id}/status| 审核/下架 `?status=0/1/2/3`       |
| GET  | /api/admin/orders              | 订单列表 `?page=&pageSize=&status=`|

---

## 7. 安全机制（实际实现）

### 7.1 密码加密

使用 `Base64Provider`（`common/security/Base64Provider.java`），采用 **Base64 + 固定盐值** 方案：

```java
// 伪代码
encode(password) = Base64.encode("cuzssp" + password + "2026webproject")
matches(raw, encoded) = encode(raw).equals(encoded)
```

> **注意**: 这不是 BCrypt，不是生产级密码哈希方案。盐值 `cuzssp` / `2026webproject` 硬编码在源码中。

### 7.2 JWT 认证

- `JwtTokenProvider`: 基于 `io.jsonwebtoken` (jjwt 0.12.6)，HMAC-SHA 签名
- Token 包含: `sub`(userId), `username`, `role`
- 过期时间: 配置中的毫秒值（注意 `JwtTokenProvider` 构造器对 `expiration` 做了 `* 1000` 转换）
- `JwtAuthenticationFilter`: 从 `Authorization: Bearer xxx` 头解析，设置 Spring Security Context

### 7.3 鉴权方式

控制器层采用**手动解析 Token** 模式：各 Controller 注入 `JwtTokenProvider`，在方法内调用 `getCurrentUserId(token)` 解析用户 ID，而非完全依赖 Spring Security 注解（如 `@PreAuthorize`）。

---

## 8. 配置说明

### 8.1 `application.yml` 关键配置项

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:cuzssp}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:123456}
  servlet:
    multipart:
      max-file-size: ${UPLOAD_MAX_FILE_SIZE:10MB}
      max-request-size: ${UPLOAD_MAX_REQUEST_SIZE:50MB}

server:
  port: ${SERVER_PORT:8080}

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

jwt:
  secret: ${JWT_SECRET:cuzssp-jwt-secret-key-2026-256-bits-default}
  expiration: ${JWT_EXPIRATION:86400000}

r2:
  s3:
    account-id: ${R2_ACCOUNT_ID:...}
    access-key: ${R2_ACCESS_KEY_ID:...}
    secret-key: ${R2_SECRET_ACCESS_KEY:...}
    endpoint: ${R2_ENDPOINT:https://...r2.cloudflarestorage.com}
    bucket-name: ${R2_BUCKET_NAME:demo}
    child-folder: ${R2_CHILD_FOLDER:demo/2026/}
    cdn-domain: ${R2_PUBLIC_URL:https://...}
    region: ${R2_REGION:auto}
```

### 8.2 环境变量（`.env` / `.env.example`）

所有敏感值和环境相关配置通过环境变量注入，详见 `src/main/resources/.env.example`。主要变量：

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` — 数据库连接
- `JWT_SECRET`, `JWT_EXPIRATION` — JWT 配置
- `R2_ACCOUNT_ID`, `R2_ACCESS_KEY_ID`, `R2_SECRET_ACCESS_KEY`, `R2_BUCKET_NAME`, `R2_ENDPOINT`, `R2_PUBLIC_URL`, `R2_REGION` — Cloudflare R2
- `SERVER_PORT` — 服务端口
- `UPLOAD_MAX_FILE_SIZE`, `UPLOAD_MAX_REQUEST_SIZE` — 上传限制（默认 10MB / 50MB）

---

## 9. 启动与部署

### 9.1 后端启动

```bash
# 1. 复制环境变量模板并填写实际值
cp src/main/resources/.env.example src/main/resources/.env

# 2. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS cuzssp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. 启动（使用 Maven Wrapper）
cd CampusSecondHandTradingPlatform_backend
./mvnw spring-boot:run
```

### 9.2 前端启动

```bash
cd campus-second-hand-trading-platform-frontend
pnpm install
pnpm dev
# 访问 http://localhost:5173
```

### 9.3 后端打包部署

```bash
./mvnw clean package -DskipTests
java -jar target/CampusSecondHandTradingPlatform_backend-0.0.1-SNAPSHOT.jar
```

---

## 10. 开发规范

### 10.1 代码架构

- **后端分层**: Controller → Service(接口) → ServiceImpl(实现) → Mapper(注解式 SQL)
- **Mapper 风格**: MyBatis 注解（`@Select`/`@Insert`/`@Update`），无 XML 映射文件
- **包结构**: `com.cuzssp.campussecondhandtradingplatform_backend`
- **实体转换**: DTO(请求) → `ToEntityUtil` → Entity → `ToVOUtil` → VO(响应)
- **异常处理**: 统一 `@RestControllerAdvice` (`GlobalExceptionHandler`)，业务异常 `BusinessException`
- **时间填充**: `MyMetaObjectHandler` 自动填充 `createdAt` / `updatedAt`

### 10.2 命名规范

| 类别         | 规范              | 示例                             |
|--------------|-------------------|----------------------------------|
| Java 类名    | UpperCamelCase    | `ProductController`              |
| Java 方法    | lowerCamelCase    | `getProductList()`               |
| 数据库表     | snake_case        | `product_image`, `chat_message`  |
| RESTful URL  | 短横线+资源名复数  | `/api/products/{id}`             |
| 包名         | 小写无下划线       | `campussecondhandtradingplatform_backend` |

### 10.3 常量管理

- `UserConstant`: 角色(`ROLE_USER=0`/`ROLE_ADMIN=1`)、状态(`STATUS_ABLE=0`/`STATUS_DISABLE=1`)、信誉分(`CREDIT_SCORE_DEFAULT=100`)
- `ProductConstant`: 新旧程度(`CONDITION_100_NEW=0`/`CONDITION_95_NEW=1`/`CONDITION_85_NEW=2`)、状态(`STATUS_NEED_CHECK=0`→`STATUS_ON_SALE=1`→`STATUS_SOLD_OUT=2`→`STATUS_DISABLE=3`)
- `OrderInfoConstant`: 状态(`STATUS_WAIT_PAY=0`→`STATUS_WAIT_DELIVER=1`→`STATUS_WAIT_RECEIVE=2`→`STATUS_COMPLETED=3` / `STATUS_CANCELLED=4`)

### 10.4 提交规范

遵循 Conventional Commits:
- `feat:` 新增功能 | `fix:` 修复 Bug | `docs:` 文档更新
- `refactor:` 重构 | `style:` 格式化 | `test:` 测试

---

## 11. 待完善项 (TODO) 与已知问题

根据代码中标注的 TODO 和实际分析：

1. **商品图片上传**: `ProductServiceImpl.createProduct()` 中图片插入逻辑存在但标注"图片未能插入，前端是否上传？"
2. **商品编辑功能**: `ProductController.updateProduct()` 标注"前端未做，无法验证是否可用"
3. **密码安全**: 当前使用 Base64 + 硬编码盐值，建议升级为 BCrypt 或 Argon2
4. **CORS 配置**: `CorsConfig` 和 `MyBatisPlusConfig` 当前为空类
5. **无缓存层**: 项目未集成 Redis
6. **公告模块**: `Announcement` 实体和 Mapper 已定义，但未暴露 API
7. **分类 API 权限**: `/api/categories` 的增删改操作未做管理员鉴权
8. **订单分页实现**: `OrderServiceImpl.getOrders()` 先查全部再内存过滤，数据量大时性能差
9. **管理后台权限**: 用户/商品/订单管理 API 未做管理员鉴权
10. **支付逻辑**: `payOrder` 直接变更状态，未对接真实支付

---

> **文档维护**: 本 DEV.md 随项目迭代持续更新。基于 2026-07-14 代码快照编写，反映实际 `CampusSecondHandTradingPlatform_backend` 代码状态。
