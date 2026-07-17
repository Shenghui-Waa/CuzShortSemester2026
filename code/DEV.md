# 校园二手交易平台 — 开发说明文档 (DEV.md)

> **版本**: v2.6.0  
> **日期**: 2026-07-17  
> **项目名**: CuzShortSemester2026 — Campus Second-Hand Trading Platform  
> **说明**: 基于项目实际代码结构 v2.5.5 快照编写，反映当前前后端代码的真实状态。本文档涵盖后端 (Spring Boot 4.0.7) 和前端 (Vue 3 + TypeScript) 完整架构。

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
| 数据库驱动 | MySQL Connector/J                     | 随 Spring Boot 4.x |
| 分页插件   | PageHelper                            | **2.1.1**      |
| 对象存储   | AWS SDK for Java S3 (Cloudflare R2)   | **2.46.21**    |
| 开发工具   | Lombok                                | 随 Spring Boot |
| Java 版本  | Java                                  | **17**         |
| 数据库     | MySQL                                 | 8.0+           |
| 前端框架   | Vue 3 + TypeScript                    | **3.5.39**     |
| 前端路由   | Vue Router                            | **4.6.4**      |
| 状态管理   | Pinia                                 | **2.3.1**      |
| UI 组件库  | Element Plus                          | **2.14.2**     |
| HTTP 客户端| Axios                                  | **1.18.1**     |
| 构建工具   | Vite                                  | **8.1.1**      |
| CSS 预处理 | Sass                                  | **1.101.0**    |
| 包管理     | pnpm                                  | —              |

> **注意**: 
> - 实际项目**未使用 Redis**，无缓存层。
> - 密码加密采用自定义 Base64 + 盐值方案 (`"cuzssp" + rawPassword + "2026webproject"`)，**非 BCrypt**（虽然 SecurityConfig 中注入了 BCryptPasswordEncoder Bean，但实际业务未使用）。

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

#### 前台用户端

| 模块     | 功能点                                                                            | Controller            |
|----------|-----------------------------------------------------------------------------------|-----------------------|
| 用户认证 | 注册、登录、登出、获取当前用户信息 (`/me`)                                           | `AuthController`      |
| 用户管理 | 查看用户信息、编辑个人资料（昵称/手机/邮箱/学校/校区）、修改密码、上传头像             | `UserController`      |
| 商品浏览 | 分页列表、关键词搜索、分类+校区联合筛选、价格区间筛选、条件筛选、排序、商品详情（含浏览量自增） | `ProductController`   |
| 商品发布 | 发布商品（先上传图片至R2，再传URL数组参数+JSON body）、编辑商品、修改商品状态（上架/下架） | `ProductController`   |
| 收藏系统 | 收藏/取消收藏、收藏列表（分页）、检查是否已收藏                                    | `FavoriteController`  |
| 购物车   | 加入购物车、移除、查看购物车列表                                                  | `CartController`      |
| 订单系统 | 创建订单、订单列表（分页+状态筛选）、详情、支付/发货/收货/取消                       | `OrderController`     |
| 聊天系统 | 联系人列表、消息记录（分页）、发送消息、标记已读（WebSocket 推送 + HTTP REST）       | `ChatController`      |
| 评价系统 | 创建评价（星级+文字）、查看用户评价记录（分页）、评分影响信誉分（>=3星+1, <3星-1）    | `ReviewController`    |
| 文件上传 | 单文件上传、多文件上传（上传至 Cloudflare R2）                                     | `FileController`      |
| 分类浏览 | 获取全部分类（含各分类商品数）                                                     | `CategoryController`  |

#### 后台管理端

| 模块     | 功能点                                                                 | Controller               |
|----------|------------------------------------------------------------------------|---------------------------|
| 仪表盘   | 用户数、商品数、订单数、今日新增用户、今日新增订单（`DashboardVO`）        | `AdminDashboardController` |
| 分类管理 | 新增/编辑/删除分类（通过管理端路由 `/api/admin/category/**`）              | `AdminDashboardController` (委托 `CategoryService`) |
| 用户管理 | 用户列表（分页+关键字）、修改用户状态（启用/封禁）、新增管理员              | `AdminDashboardController` |
| 商品管理 | 商品列表（分页+关键字+状态筛选）、修改商品状态（审核通过/下架）            | `AdminDashboardController` |
| 订单管理 | 订单列表（分页+状态筛选），仅查看                                        | `AdminDashboardController` |

### 3.3 前端页面路由表

| 路径 | 页面组件 | 需认证 | 需管理员 |
|------|----------|--------|----------|
| `/` | HomeView (首页) | | |
| `/login` | LoginView (登录) | | |
| `/register` | RegisterView (注册) | | |
| `/products` | ProductList (商品列表) | | |
| `/products/:id` | ProductDetail (商品详情) | | |
| `/publish` | ProductPublish (发布商品) | yes | |
| `/cart` | CartView (购物车) | yes | |
| `/orders` | OrderList (订单列表) | yes | |
| `/orders/:id` | OrderDetail (订单详情) | yes | |
| `/profile` | ProfileView (个人中心) | yes | |
| `/my-products` | MyProducts (我的商品) | yes | |
| `/favorites` | FavoriteList (收藏) | yes | |
| `/chat` | ChatView (聊天) | yes | |
| `/admin` | DashboardView (仪表盘) | yes | yes |
| `/admin/users` | UserManage (用户管理) | yes | yes |
| `/admin/products` | ProductManage (商品管理) | yes | yes |
| `/admin/orders` | OrderManage (订单管理) | yes | yes |
| `/admin/categories` | CategoryManage (分类管理) | yes | yes |

---

## 4. 项目结构

### 4.1 后端完整包结构

```
CampusSecondHandTradingPlatform_backend/
├── pom.xml                                          # Maven (Spring Boot 4.0.7)
├── mvnw / mvnw.cmd                                 # Maven Wrapper
├── HELP.md                                         # Spring Boot 参考文档
├── src/main/java/com/cuzssp/campussecondhandtradingplatform_backend/
│   ├── CampusSecondHandTradingPlatformBackendApplication.java  # 启动类
│   ├── controller/                                 # 控制层 (11 个)
│   │   ├── AdminDashboardController.java           # 后台管理 (仪表盘+CRUD)
│   │   ├── AuthController.java                     # 用户认证 (register/login/me)
│   │   ├── CartController.java                     # 购物车 (list/add/remove)
│   │   ├── CategoryController.java                 # 前台分类浏览 (GET only)
│   │   ├── ChatController.java                     # 聊天 (contacts/messages/send/read)
│   │   ├── FavoriteController.java                 # 收藏 (list/add/remove/check)
│   │   ├── FileController.java                     # 文件上传 (R2 single/multi)
│   │   ├── OrderController.java                    # 订单 (create/list/detail/pay/ship/confirm/cancel)
│   │   ├── ProductController.java                  # 商品 (list/detail/create/update/status/my)
│   │   ├── ReviewController.java                   # 评价 (create/userReviews)
│   │   ├── SpaController.java                      # SPA 前端路由回退 (forward:/index.html)
│   │   └── UserController.java                     # 用户 (profile/password/avatar)
│   ├── service/                                    # 服务接口 (11 个)
│   │   ├── AdminService / AuthService / CartService / CategoryService
│   │   ├── ChatService / FavoriteService / FileService / OrderService
│   │   ├── ProductService / ReviewService / UserService
│   ├── service/impl/                               # 服务实现 (11 个, 一一对应)
│   ├── mapper/                                     # MyBatis Mapper 接口 (11 个, 纯注解风格)
│   │   ├── AnnouncementMapper.java                 # 公告 (实体/Mapper已定义, Service/API未暴露)
│   │   ├── CartMapper / CategoryMapper / ChatMessageMapper
│   │   ├── FavoriteMapper / OrderMapper / OrderItemMapper
│   │   ├── ProductMapper / ProductImageMapper
│   │   ├── ReviewMapper / UserMapper
│   ├── common/
│   │   ├── config/
│   │   │   ├── CorsConfig.java                     # CORS 配置 (独立管理跨域规则)
│   │   │   ├── MyBatisPlusConfig.java              # MyBatis-Plus 配置 (空壳类)
│   │   │   ├── MyMetaObjectHandler.java            # 自动填充 createdAt/updatedAt
│   │   │   ├── S3Config.java                       # R2 配置属性绑定 (@ConfigurationProperties)
│   │   │   ├── SecurityConfig.java                 # Spring Security (URL权限+JWT Filter+BCrypt Bean; CORS委托给CorsConfig)
│   │   │   ├── ChatWebSocketHandler.java           # WebSocket 连接管理 (并发Map, 仅推送)
│   │   │   └── WebSocketConfig.java                # WebSocket 注册 (/ws/chat, 允许所有来源)
│   │   ├── constant/
│   │   │   ├── UserConstant.java                   # 角色(0/1) + 状态(0/1) + 信誉分默认值(100)
│   │   │   ├── ProductConstant.java                # 新旧程度(0/1/2) + 状态(0/1/2/3) + 浏览量默认(0)
│   │   │   └── OrderInfoConstant.java              # 订单状态(0/1/2/3/4)
│   │   ├── dto/                                    # 请求 DTO (8 个)
│   │   │   ├── ChangePasswordRequest.java
│   │   │   ├── CreateOrderRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── ProductQueryDTO.java                # 商品多条件查询 (keyword/categoryId/campus/price/condition/sort)
│   │   │   ├── RegisterRequest.java
│   │   │   ├── ReviewRequest.java
│   │   │   ├── SendMessageRequest.java
│   │   │   └── UpdateProfileRequest.java
│   │   ├── entity/                                 # 数据库实体 (10 个, 含 Announcement)
│   │   │   ├── Announcement.java                   # @TableName("announcement")
│   │   │   ├── CartItem.java                       # @TableName("cart")
│   │   │   ├── Category.java                       # @TableName("category")
│   │   │   ├── ChatMessage.java                    # @TableName("chat_message")
│   │   │   ├── Favorite.java                       # @TableName("favorite")
│   │   │   ├── OrderInfo.java                      # @TableName("`order`") — 注意反引号包裹 SQL 关键字
│   │   │   ├── OrderItem.java                      # @TableName("order_item")
│   │   │   ├── Product.java                        # @TableName("product")
│   │   │   ├── ProductImage.java                   # @TableName("product_image")
│   │   │   ├── Review.java                         # @TableName("review")
│   │   │   └── User.java                           # @TableName("user")
│   │   ├── exception/
│   │   │   ├── BusinessException.java              # 业务异常 (code+message)
│   │   │   └── GlobalExceptionHandler.java         # 全局异常处理 (Business/MaxUpload/Exception)
│   │   ├── security/
│   │   │   ├── Base64Provider.java                 # 密码加密 (Base64 + 硬编码盐值)
│   │   │   ├── JwtAuthenticationFilter.java        # JWT 认证过滤器
│   │   │   ├── JwtTokenProvider.java               # JWT 生成/解析/验证
│   │   │   └── UserDetailsServiceImpl.java         # Spring Security UserDetailsService
│   │   ├── util/
│   │   │   ├── CloudflareR2Client.java             # R2 S3Client 构建 + CRUD 操作
│   │   │   ├── FileUtil.java                       # 文件上传至 R2 (MultipartFile -> URL)
│   │   │   ├── ToEntityUtil.java                   # DTO -> Entity 转换工具
│   │   │   └── ToVOUtil.java                       # Entity -> VO 转换工具
│   │   └── vo/                                     # 响应 VO (10 个)
│   │       ├── CartItemVO / CategoryVO / ChatContactVO / ChatMessageVO
│   │       ├── DashboardVO / OrderItemVO / OrderVO / PageResult
│   │       ├── ProductVO / Result / ReviewVO / UserVO
│   └── resources/
│       ├── application.yml                         # 主配置 (数据源/上传限制/JWT/R2/MyBatis-Plus)
│       ├── .env / .env.example                     # 环境变量
│       └── static/                                 # 前端构建产物部署目录
├── src/test/                                       # 测试代码 (待完善)
└── docs/
    └── sql/
        └── init.sql                                # 数据库初始化脚本 (含示例数据)
```

> **注意**: 后端使用 MyBatis 注解方式 (`@Select`/`@Insert`/`@Update`)，无 XML 映射文件，且继承 MyBatis-Plus `BaseMapper` 以获得内置 CRUD 方法 (`selectById`/`insert`/`updateById`/`selectAll` 等)。

---

## 5. 核心业务流程（后端）

### 5.1 用户认证与会话管理 (AuthController)

| 端点                       | 方法 | 鉴权   | 说明                                                     |
|----------------------------|------|--------|----------------------------------------------------------|
| `POST /api/auth/register`  | 公开 | —      | 注册：密码经 Base64Provider 编码（盐值拼装格式: `cuzssp`+password+`2026webproject`） |
| `POST /api/auth/login`     | 公开 | —      | 登录：返回 JWT Token，payload 含 `sub`(userId), `username`, `role` |
| `POST /api/auth/logout`    | 需认证 | —     | 登出（无状态 JWT，客户端清除 Token 即可）                    |
| `GET /api/auth/me`         | 需认证 | —     | 从 JWT 解析 userId 后查询用户信息                           |

### 5.2 API 权限矩阵 (SecurityConfig)

| URL 模式                        | 方法         | 权限            |
|---------------------------------|--------------|-----------------|
| `/api/auth/register`, `/api/auth/login` | POST | 公开 (permitAll) |
| `/api/categories/**`           | GET          | 公开            |
| `/api/categories/**`           | POST/PUT/DELETE | ROLE_ADMIN  |
| `/api/products/**`             | ALL          | 公开 (permitAll) |
| `/api/user/**`                 | ALL          | 公开 (permitAll) — 内部通过 JWT 手动鉴权 |
| `/api/reviews/user/**`         | ALL          | 公开 (permitAll) |
| `/api/admin/**`                | ALL          | ROLE_ADMIN     |
| `/ws/**`                       | ALL          | 公开            |
| `/`, `/index.html`, `/assets/**`, `/favicon.ico` | GET | 公开 |
| 其他所有                       | ALL          | 需认证 (authenticated) |

> **注意**: CORS 配置已从 `SecurityConfig` 迁移至 `CorsConfig`（独立 `@Configuration` 类），`SecurityConfig` 通过 `@Autowired CorsConfigurationSource` 注入引用。允许所有来源、允许凭据、允许 GET/POST/PUT/DELETE/OPTIONS。

### 5.3 商品管理 (ProductController)

商品生命周期：`待审核(0)` -> `在售(1)` -> `已售出(2)` 或 `已下架(3)`。

**图片上传流程**: 前端先调用 `POST /api/files/upload` 将图片上传至 Cloudflare R2，获取 URL 数组；再调用 `POST /api/products?images=url1&images=url2` (URL 作为查询参数) + JSON Body 创建商品，后端将 URL 写入 `product_image` 表。

**商品查询 (ProductQueryDTO)** 支持以下筛选条件:
- `keyword` — 关键词搜索（模糊匹配 title）
- `categoryId` — 分类筛选
- `campus` — 校区筛选
- `minPrice` / `maxPrice` — 价格区间
- `condition` — 新旧程度筛选
- `sortBy` — 排序字段（默认 `created_at`）
- `sortOrder` — 排序方向（默认 `desc`）

### 5.4 订单管理 (OrderController)

状态流转：`待付款(0)` -> `待发货(1)` -> `待收货(2)` -> `已完成(3)`；任意非终态可 -> `已取消(4)`。

| 端点                         | 方法 | 说明                          |
|------------------------------|------|-------------------------------|
| `POST /api/orders`          | 需认证 | 创建订单（`CreateOrderRequest`: productId, remark） |
| `GET /api/orders`           | 需认证 | 订单列表（分页 + status 筛选，先查全部再内存过滤） |
| `GET /api/orders/{id}`      | 需认证 | 订单详情（含 `OrderItemVO` 列表） |
| `PUT /api/orders/{id}/pay`  | 需认证 | 支付（直接变更状态为"待发货"）  |
| `PUT /api/orders/{id}/ship` | 需认证 | 发货（仅卖家可操作）           |
| `PUT /api/orders/{id}/confirm` | 需认证 | 确认收货（仅买家可操作）       |
| `PUT /api/orders/{id}/cancel` | 需认证 | 取消订单                      |

- **订单编号** (`orderNo`): 通过 `UUID.randomUUID().toString().replace("-","").substring(0,20)` 生成 20 位唯一字符串。
- **支付逻辑**: `payOrder()` 直接变更状态为"待发货"，未对接真实支付。

### 5.5 聊天系统

| 端点                              | 方法      | 说明                                                                  |
|-----------------------------------|-----------|-----------------------------------------------------------------------|
| `GET /api/chat/contacts`          | 需认证    | 联系人列表 (`ChatContactVO`: contactName, lastMessage, unreadCount)    |
| `GET /api/chat/{contactId}`       | 需认证    | 聊天记录（分页）                                                        |
| `POST /api/chat/send`             | 需认证    | 发送消息 -> 持久化到 DB + WebSocket 推送给接收者                          |
| `PUT /api/chat/read/{contactId}`  | 需认证    | 标记已读（将该联系人的消息 `is_read` 设为 1）                            |
| `WS /ws/chat?userId={id}`         | WebSocket | 仅用于**接收**实时推送通知；消息**发送**走 HTTP 接口                      |

WebSocket 连接通过 URL 参数 `userId` 标识用户，`ChatWebSocketHandler` 内部以 `ConcurrentHashMap<Long, WebSocketSession>` 管理连接。消息通过 HTTP 接口持久化后，调用 `ChatWebSocketHandler.sendMessageToUser()` 进行推送。

### 5.6 文件上传 (FileController)

| 端点                          | 方法 | 说明              |
|-------------------------------|------|-------------------|
| `POST /api/files/upload`     | 需认证 | 单文件上传 (`@RequestPart("file")`) |
| `POST /api/files/upload/batch` | 需认证 | 多文件上传 (`@RequestPart("files")`) |

上传路径：`{childFolder}/cuzssp-{UUID}.{ext}`，返回 CDN 完整 URL（使用 `R2_PUBLIC_URL` 配置）。

### 5.7 统一响应格式

返回数据格式：`Result<T>` 封装体，包含 `code`(Integer), `message`(String), `data`(T)。
- 成功: `Result.success(data)` -> `{code:200, message:"success", data:...}`
- 业务异常: `Result.error(code, message)` -> HTTP 状态码跟随业务 code
- 系统异常: `Result.error(500, "Internal server error")`

### 5.8 仪表盘 (AdminDashboardController)

`GET /api/admin/dashboard` 返回 `DashboardVO`:

| 字段 | 说明 | 来源 |
|------|------|------|
| `userCount` | 总用户数 | `userMapper.countAll()` |
| `productCount` | 总商品数 | `productMapper.countAll()` |
| `orderCount` | 总订单数 | `orderMapper.countAll()` |
| `totalAmount` | 交易总额 | `orderMapper` 聚合 |
| `todayNewUsers` | 今日新增用户 | `userMapper.countTodayNew()` |
| `todayNewOrders` | 今日新增订单 | `orderMapper` 聚合 |

### 5.9 评价系统

- `POST /api/reviews` — 创建评价 (`ReviewRequest`: orderId, targetId, rating 1-5, content)
- `GET /api/reviews/user/{userId}` — 用户评价记录（分页）
- 评分影响: >=3 星 -> 被评者信誉分 +1，<3 星 -> 被评者信誉分 -1

### 5.10 SPA 路由回退

`SpaController` 将所有非静态资源路径 (`/{path:[^\\.]*}`) 转发到 `forward:/index.html`，支持 Vue Router History 模式下的前端路由。

---

## 6. 数据库设计

### 6.1 数据表概览

| 表名           | 说明     | 关键字段 |
|----------------|----------|----------|
| `user`         | 用户     | `id`(PK), `username`(UNIQUE), `password`, `nickname`, `avatar`, `phone`, `email`, `school`, `campus`, `role`, `status`, `credit_score`, `created_at`, `updated_at` |
| `category`     | 商品分类 | `id`(PK), `name`, `icon`, `sort_order`, `created_at` |
| `product`      | 商品     | `id`(PK), `user_id`, `category_id`, `title`, `description`, `price`, `original_price`, `condition`, `campus`, `status`, `view_count`, `created_at`, `updated_at` |
| `product_image`| 商品图片 | `id`(PK), `product_id`, `url`, `sort_order` |
| `` `order` `` / `order_info` | 订单 | `id`(PK), `order_no`, `buyer_id`, `seller_id`, `total_amount`, `status`, `remark`, `created_at`, `paid_at`, `shipped_at`, `completed_at` |
| `order_item`   | 订单明细 | `id`(PK), `order_id`, `product_id`, `price`, `created_at` |
| `cart`         | 购物车   | `id`(PK), `user_id`, `product_id`, `created_at`, UNIQUE(`user_id`,`product_id`) |
| `favorite`     | 收藏     | `id`(PK), `user_id`, `product_id`, `created_at`, UNIQUE(`user_id`,`product_id`) |
| `chat_message` | 聊天消息 | `id`(PK), `sender_id`, `receiver_id`, `product_id`, `content`, `is_read`, `created_at` |
| `review`       | 评价     | `id`(PK), `order_id`, `reviewer_id`, `target_id`, `rating`, `content`, `created_at` |
| `announcement` | 系统公告 | `id`(PK), `title`, `content`, `created_at`, `updated_at` |

> **主键策略**: 所有表使用雪花算法 `IdType.ASSIGN_ID`（MyBatis-Plus 自动生成）
> **注意**: `OrderInfo` 实体映射 `@TableName("`order`")`，而 `init.sql` 末尾有 `RENAME TABLE order TO order_info` 语句。部署时需确认实际表名。

### 6.2 初始化数据

`init.sql` 预置：
- 8 个商品分类：教材教辅、数码产品、生活用品、服饰鞋包、运动户外、美妆护肤、图书文具、其他
- 1 个管理员账号：`admin / password`（密码为 Base64 编码后的值）

### 6.3 关键字段规则

| 字段              | 枚举值                                      | 说明                          |
|-------------------|---------------------------------------------|-------------------------------|
| `user.role`       | 0=用户, 1=管理员                             | `UserConstant`               |
| `user.status`     | 0=正常, 1=封禁                               | `UserConstant`               |
| `user.credit_score` | 默认100; 好评(>=3星)+1, 差评(<3星)-1          |                              |
| `product.condition` | 0=全新, 1=95新, 2=85新                     | `ProductConstant` 常量值; 实体字段注释误写为 1/2/3，实际代码使用常量 0/1/2 |
| `product.status`  | 0=待审核, 1=在售, 2=已售出, 3=已下架         | `ProductConstant`            |
| `order.status`    | 0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消 | `OrderInfoConstant`       |
| `chat_message.is_read` | 0=未读, 1=已读                          |                              |

---

## 7. 前端架构说明

### 7.1 目录结构

```
campus-second-hand-trading-platform-frontend/
├── index.html
├── package.json / pnpm-lock.yaml / pnpm-workspace.yaml
├── vite.config.ts / tsconfig.json / tsconfig.app.json / tsconfig.node.json
├── public/
└── src/
    ├── main.ts                    # 应用入口
    ├── App.vue                    # 根组件 (含主题切换)
    ├── router/index.ts            # Vue Router (18 条路由 + beforeEach 守卫)
    ├── stores/
    │   ├── user.ts                # 用户状态: token/userInfo/role/isLogin/isAdmin/ensureUserInfo
    │   └── theme.ts               # 暗色主题: isDark/toggle
    ├── api/
    │   ├── request.ts             # Axios 封装 (baseURL /api, 自动 Bearer, 401 跳转)
    │   ├── auth.ts                # 认证 API
    │   ├── product.ts             # 商品 API
    │   ├── order.ts               # 订单 API
    │   ├── user.ts                # 用户 API
    │   └── index.ts               # 其他 API (cart/favorite/chat/review/file/category/admin)
    ├── styles/
    │   ├── global.scss            # 全局样式 + 暗色模式 CSS 变量
    │   └── variables.scss         # SCSS 变量
    ├── utils/
    │   ├── icons.ts               # Element Plus 图标注册
    │   └── index.ts               # 工具函数
    ├── components/
    │   ├── ImageUpload.vue        # 多图上传组件 (调用 R2)
    │   ├── Pagination.vue         # 统一分页器
    │   ├── ProductCard.vue        # 商品卡片
    │   ├── SearchBar.vue          # 搜索栏
    │   └── layout/
    │       ├── AppHeader.vue      # 前台顶部导航 (Logo + 导航 + 搜索 + 用户菜单 + 暗色切换)
    │       └── AppFooter.vue      # 底部版权信息
    └── views/
        ├── HomeView.vue           # 首页 (分类展示 + 最新/推荐商品)
        ├── LoginView.vue          # 登录
        ├── RegisterView.vue       # 注册 (学校/校区选择)
        ├── product/
        │   ├── ProductList.vue    # 商品列表 (筛选 + 排序)
        │   ├── ProductDetail.vue  # 商品详情 (图片轮播 + 卖家信息 + 收藏/聊天入口)
        │   └── ProductPublish.vue # 商品发布/编辑
        ├── cart/CartView.vue      # 购物车
        ├── order/
        │   ├── OrderList.vue      # 订单列表 (按状态 Tab 筛选)
        │   └── OrderDetail.vue    # 订单详情 + 状态操作
        ├── favorite/FavoriteList.vue # 收藏列表
        ├── chat/ChatView.vue      # 聊天 (联系人列表 + 消息气泡)
        ├── user/
        │   ├── ProfileView.vue    # 个人中心 (头像/资料编辑/信誉分)
        │   └── MyProducts.vue     # 我的商品
        └── admin/
            ├── AdminLayout.vue    # 后台布局 (侧边导航 + 折叠 + 暗色模式 + 菜单高亮指示器)
            ├── DashboardView.vue  # 仪表盘统计卡片
            ├── UserManage.vue     # 用户管理 (列表 + 封禁/启用 + 新增管理员弹窗)
            ├── ProductManage.vue  # 商品管理 (审核通过/下架)
            ├── OrderManage.vue    # 订单管理 (查看)
            └── CategoryManage.vue # 分类管理 (增删改)
```

### 7.2 状态管理 (Pinia)

- **`useUserStore`** (`stores/user.ts`): Token 管理（localStorage 持久化）、用户信息缓存、角色判断、`ensureUserInfo()` 懒加载方法（用于需要 userId 的场景如订单/聊天）
- **`useThemeStore`** (`stores/theme.ts`): 白天/黑夜主题切换，通过切换 `html.dark` CSS 类 + CSS 变量控制全局配色

### 7.3 HTTP 请求 (Axios)

`api/request.ts` 统一封装：
- `baseURL: "/api"`（开发时通过 Vite 代理转发至 `http://localhost:8080`）
- 请求拦截器：自动附加 `Authorization: Bearer {token}`
- 响应拦截器：自动解包 `res.data`；401 时清除 token 并跳转登录页；错误时 `ElMessage.error` 提示

---

## 8. 配置与环境变量

### 8.1 application.yml 核心配置

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
  config:
    import: optional:classpath:.env[.properties]     # 环境变量注入 (无外部依赖)

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: assign_id                            # 雪花 ID

jwt:
  secret: ${JWT_SECRET:cuzssp-jwt-secret-key-2026-256-bits-default}
  expiration: ${JWT_EXPIRATION:86400000}             # 单位: 秒 (代码内 *1000 转毫秒使用)

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

所有敏感值通过 `spring.config.import` 机制从 `.env` 文件注入（`spring-dotenv` 依赖已在 pom.xml 注释掉，使用 Spring Boot 原生 `.env[.properties]` 解析）。主要变量：

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` — 数据库连接
- `JWT_SECRET`, `JWT_EXPIRATION` — JWT 配置 (**expiration 单位为秒**)
- `R2_ACCOUNT_ID`, `R2_ACCESS_KEY_ID`, `R2_SECRET_ACCESS_KEY`, `R2_BUCKET_NAME`, `R2_ENDPOINT`, `R2_PUBLIC_URL`, `R2_REGION` — Cloudflare R2 配置
- `SERVER_PORT` — 服务端口（默认 8080）
- `UPLOAD_MAX_FILE_SIZE`, `UPLOAD_MAX_REQUEST_SIZE` — 上传限制 (`.env` 实际设置为 2MB/10MB)

---

## 9. 启动与部署

### 9.1 后端启动

```bash
# 1. 复制环境变量模板并填写实际值
cp src/main/resources/.env.example src/main/resources/.env

# 2. 创建数据库并初始化
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS cuzssp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p cuzssp < ../docs/sql/init.sql

# 3. 启动（使用 Maven Wrapper）
cd CampusSecondHandTradingPlatform_backend
./mvnw spring-boot:run
# 后端启动在 http://localhost:8080
```

### 9.2 前端启动（开发模式）

```bash
cd campus-second-hand-trading-platform-frontend
pnpm install
pnpm dev
# 访问 http://localhost:5173 (Vite 代理 /api -> localhost:8080)
```

### 9.3 生产部署

```bash
# 后端打包
cd CampusSecondHandTradingPlatform_backend
./mvnw clean package -DskipTests
java -jar target/CampusSecondHandTradingPlatform_backend-0.0.1-SNAPSHOT.jar

# 前端构建
cd campus-second-hand-trading-platform-frontend
pnpm build
# 将 dist/ 部署至后端 static/ 目录或使用 Nginx 反向代理
```

---

## 10. 开发规范

### 10.1 代码架构

- **后端分层**: Controller -> Service(接口) -> ServiceImpl(实现) -> Mapper(注解式 SQL)
- **Mapper 风格**: MyBatis 注解（`@Select`/`@Insert`/`@Update`），无 XML 映射文件；同时继承 MyBatis-Plus `BaseMapper` 获得内置 CRUD
- **包结构**: `com.cuzssp.campussecondhandtradingplatform_backend`
- **实体转换**: DTO(请求) -> `ToEntityUtil` -> Entity -> `ToVOUtil` -> VO(响应)
- **异常处理**: 统一 `@RestControllerAdvice` (`GlobalExceptionHandler`)，业务异常 `BusinessException(code, message)`
- **时间填充**: `MyMetaObjectHandler` 自动填充 `createdAt` / `updatedAt`
- **Spring Security**: `SecurityConfig` 集中配置 URL 权限规则和 JWT Filter；CORS 独立于 `CorsConfig` (注入 `CorsConfigurationSource` Bean)；`MyBatisPlusConfig` 为空壳保留类
- **WebSocket**: 原生 Spring WebSocket，仅作服务端->客户端推送（消息发送走 HTTP），通过 URL 参数 `?userId=` 标识连接
- **主键生成**: MyBatis-Plus `IdType.ASSIGN_ID` 雪花算法

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
- `ProductConstant`: 新旧程度(`CONDITION_100_NEW=0`/`CONDITION_95_NEW=1`/`CONDITION_85_NEW=2`)、状态(`STATUS_NEED_CHECK=0`->`STATUS_ON_SALE=1`->`STATUS_SOLD_OUT=2`->`STATUS_DISABLE=3`)、默认浏览量(`VIEW_COUNT_DEFAULT=0`)
- `OrderInfoConstant`: 状态(`STATUS_WAIT_PAY=0`->`STATUS_WAIT_DELIVER=1`->`STATUS_WAIT_RECEIVE=2`->`STATUS_COMPLETED=3` / `STATUS_CANCELLED=4`)

### 10.4 提交规范

遵循 Conventional Commits:
- `feat:` 新增功能 | `fix:` 修复 Bug | `docs:` 文档更新
- `refactor:` 重构 | `style:` 格式化 | `test:` 测试

---

## 11. 待完善项 (TODO) 与已知问题

根据代码中标注的 TODO 和实际分析：

1. **Product condition 值不一致**: 代码常量 `ProductConstant` 定义 0/1/2，但 `Product.java` 实体字段注释写 "1=全新 2=几乎全新 3=有使用痕迹"。代码实际使用常量值 0/1/2，前端应匹配常量而非实体注释。
2. **Order 表名冲突**: `init.sql` 末尾 `RENAME TABLE order TO order_info`，但 `OrderInfo.java` 映射 `@TableName("`order`")`。部署时需确认实际表名。
3. **商品编辑功能**: `ProductController.updateProduct()` 标注"前端未做，无法验证是否可用"
4. **密码安全**: 当前使用 Base64 + 硬编码盐值（`cuzssp` + password + `2026webproject`），建议升级为 BCrypt 或 Argon2
5. **MyBatisPlus 配置类**: `MyBatisPlusConfig` 为空壳类，为占位保留（CORS 已迁移至 `CorsConfig` 独立管理）
6. **无缓存层**: 项目未集成 Redis
7. **公告模块**: `Announcement` 实体和 Mapper 已定义，但未暴露 Service 和 API
8. **分类 API 权限**: 前台 `CategoryController` GET 为公开；管理端 CRUD 通过 `/api/admin/category/**` 路由由 SecurityConfig 保护
9. **订单分页实现**: `OrderServiceImpl.getOrders()` 先查全部再内存过滤，数据量大时性能差；`AdminServiceImpl.getUserList()` 同样先全量再内存筛选
10. **支付逻辑**: `payOrder` 直接变更状态，未对接真实支付网关
11. **JWT 过期时间**: `application.yml` 默认 `86400000`（秒），代码内 `*1000` 后约为 1000 天。实际通过 `.env` 覆盖为 `21600` 秒（6 小时）
12. **环境变量解析**: `spring-dotenv` 依赖已在 pom.xml 中注释掉，当前使用 Spring Boot 原生 `spring.config.import: optional:classpath:.env[.properties]`
13. **WebSocket 身份验证**: WebSocket 连接通过 URL 参数 `userId` 标识，未做 Token 认证，存在冒充风险

---

> **文档维护**: 本 DEV.md 随项目迭代持续更新。基于 2026-07-17 代码快照 (v2.5.5) 编写，反映实际前后端代码状态。版本日志参见 `README.md`。
