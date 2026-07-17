# 校园二手交易平台 — 开发说明文档 (DEV.md)

> **版本**: v2.7.0  
> **日期**: 2026-07-17  
> **项目名**: CuzShortSemester2026 — Campus Second-Hand Trading Platform  
> **说明**: 基于项目后端 95 个 Java 源码文件完整阅读编写，反映当前代码真实状态。

---

## 1. 项目概述

校园二手物品交易 Web 应用。前后端分离，含前台用户端和后台管理端。图片存储使用 Cloudflare R2（S3 兼容 API）。

---

## 2. 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 4.0.7 |
| 安全 | Spring Security + JWT (jjwt) | 0.12.6 |
| ORM | MyBatis-Plus (仅配置，不用 BaseMapper) | 3.5.16 |
| 分页 | PageHelper | 2.1.1 |
| 对象存储 | AWS SDK S3 (Cloudflare R2) | 2.46.21 |
| 工具 | Lombok | 随 SB |
| Java | — | 17 |
| 数据库 | MySQL | 8.0+ |
| 前端 | Vue 3 + TS + Element Plus + Pinia + Vite | 见前端 package.json |

> **关键约定**: 
> - 未使用 Redis
> - Mapper **不继承** MyBatis-Plus `BaseMapper`，所有 SQL 用 `@Select`/`@Insert`/`@Update`/`@Delete` 注解显式编写
> - 密码用 `Base64Provider`（盐值: `"cuzssp" + password + "2026webproject"`），非 BCrypt
> - `SecurityConfig` 中有 `BCryptPasswordEncoder` Bean，但业务未使用

---

## 3. 需求分析

### 3.1 用户角色

| 常量 | 值 | 说明 |
|------|----|------|
| `UserConstant.ROLE_USER` | 0 | 普通用户 |
| `UserConstant.ROLE_ADMIN` | 1 | 管理员 |
| `UserConstant.STATUS_ABLE` | 0 | 正常 |
| `UserConstant.STATUS_DISABLE` | 1 | 封禁 |

### 3.2 功能模块

#### 前台 (10 个 Controller)

| 模块 | Controller | 核心功能 |
|------|-----------|----------|
| 认证 | `AuthController` | 注册/登录/登出/获取当前用户 |
| 用户 | `UserController` | 查看信息/编辑资料/改密码/换头像 |
| 商品 | `ProductController` | 列表搜索筛选/详情(浏览量+1)/发布/编辑/状态变更/我的商品 |
| 收藏 | `FavoriteController` | 收藏/取消/列表/检查是否已收藏 |
| 购物车 | `CartController` | 加入/移除/列表 |
| 订单 | `OrderController` | 创建/列表/详情/支付/发货/收货/取消 |
| 聊天 | `ChatController` | 联系人/消息记录/发送消息/标记已读 (HTTP + WebSocket 推送) |
| 评价 | `ReviewController` | 创建评价(1-5星)/查看用户评价 |
| 文件 | `FileController` | 单文件/多文件上传至 R2 |
| 分类 | `CategoryController` | 获取全部分类(含商品数) |

#### 后台 (1 个 Controller，5 个子模块)

| 模块 | 路由 | 功能 |
|------|------|------|
| 仪表盘 | `GET /api/admin/dashboard` | `DashboardVO` (用户/商品/订单/今日新增统计) |
| 用户管理 | `/api/admin/users` | 列表+关键字/修改状态/新增管理员 |
| 商品管理 | `/api/admin/products` | 列表+关键字+状态筛选/修改状态 |
| 订单管理 | `/api/admin/orders` | 列表+状态筛选(仅查看) |
| 分类管理 | `/api/admin/category/**` | 增删改(委托 `CategoryService`) |

### 3.3 前端路由 (18 条)

| 路径 | 认证 | 管理员 |
|------|:--:|:--:|
| `/`, `/login`, `/register`, `/products`, `/products/:id` | | |
| `/publish`, `/cart`, `/orders`, `/orders/:id`, `/profile`, `/my-products`, `/favorites`, `/chat` | yes | |
| `/admin` 及其 5 个子路由 | yes | yes |

---

## 4. 项目结构

### 4.1 后端文件清单 (95 个 .java)

```
CampusSecondHandTradingPlatform_backend/
├── pom.xml
├── src/main/java/com/cuzssp/campussecondhandtradingplatform_backend/
│   ├── CampusSecondHandTradingPlatformBackendApplication.java  # @SpringBootApplication + @MapperScan
│   ├── controller/           (11)
│   │   ├── AdminDashboardController  # 后台: dashboard/users/products/orders/category CRUD
│   │   ├── AuthController            # /auth/register, /auth/login, /auth/logout, /auth/me
│   │   ├── CartController            # /cart
│   │   ├── CategoryController        # /categories (GET only)
│   │   ├── ChatController            # /chat/contacts, /chat/{id}, /chat/send, /chat/read
│   │   ├── FavoriteController        # /favorites
│   │   ├── FileController            # /files/upload, /files/upload/batch
│   │   ├── OrderController           # /orders
│   │   ├── ProductController         # /products, /products/my
│   │   ├── ReviewController          # /reviews
│   │   ├── SpaController             # forward:/index.html (SPA fallback)
│   │   └── UserController            # /user/{id}, /user/profile, /user/password, /user/avatar
│   ├── service/              (11 接口)
│   │   └── impl/             (11 实现)
│   ├── mapper/               (11, 全注解 @Select/@Insert/@Update/@Delete, 不继承 BaseMapper)
│   │   ├── AnnouncementMapper    # 已实现但无 Service/API
│   │   ├── CartMapper            # insertItem() 命名
│   │   ├── CategoryMapper        # insertCategory() 命名
│   │   ├── ChatMessageMapper
│   │   ├── FavoriteMapper
│   │   ├── OrderMapper
│   │   ├── OrderItemMapper
│   │   ├── ProductMapper         # 含 searchByKeyword/selectByCampus/selectAllActive 等
│   │   ├── ProductImageMapper    # insertImage() 命名
│   │   ├── ReviewMapper
│   │   └── UserMapper
│   └── common/
│       ├── config/           (5, 已删除 MyBatisPlusConfig 和 MyMetaObjectHandler)
│       │   ├── ChatWebSocketHandler    # ConcurrentHashMap<userId, session>, 仅推送
│       │   ├── CorsConfig             # CORS Bean (已从 SecurityConfig 迁移至此)
│       │   ├── S3Config               # @ConfigurationProperties("r2.s3")
│       │   ├── SecurityConfig         # URL 权限 + JWT Filter + BCrypt Bean(未用)
│       │   └── WebSocketConfig        # /ws/chat
│       ├── constant/         (3)
│       │   ├── OrderInfoConstant      # STATUS_WAIT_PAY(0)..CANCELLED(4)
│       │   ├── ProductConstant        # CONDITION 1=全新/2=95新/3=85新, STATUS 0..3
│       │   └── UserConstant           # ROLE 0/1, STATUS 0/1, CREDIT_SCORE_DEFAULT=100
│       ├── dto/              (8)
│       │   ├── ChangePasswordRequest
│       │   ├── CreateOrderRequest
│       │   ├── LoginRequest
│       │   ├── ProductQueryDTO        # keyword, categoryId, campus, minPrice, maxPrice, condition, sortBy, sortOrder, page, pageSize
│       │   ├── RegisterRequest
│       │   ├── ReviewRequest
│       │   ├── SendMessageRequest
│       │   └── UpdateProfileRequest
│       ├── entity/           (10, 全 @Data + @TableName)
│       │   ├── Announcement           # @TableName("announcement")
│       │   ├── CartItem               # @TableName("cart")
│       │   ├── Category               # @TableName("category")
│       │   ├── ChatMessage            # @TableName("chat_message")
│       │   ├── Favorite               # @TableName("favorite")
│       │   ├── OrderInfo              # @TableName("order_info")
│       │   ├── OrderItem              # @TableName("order_item")
│       │   ├── Product                # @TableName("product"), condition 常量值 1=全新/2=95新/3=85新（已统一）
│       │   ├── ProductImage           # @TableName("product_image")
│       │   ├── Review                 # @TableName("review")
│       │   └── User                   # @TableName("user")
│       ├── exception/        (2)
│       │   ├── BusinessException      # code + message
│       │   └── GlobalExceptionHandler # @RestControllerAdvice: Business/MaxUpload/Exception
│       ├── security/         (4)
│       │   ├── Base64Provider         # encode/decode/matches(盐值硬编码)
│       │   ├── JwtAuthenticationFilter  # OncePerRequestFilter, 从 Header 解析 JWT
│       │   ├── JwtTokenProvider       # generateToken/getUserIdFromToken/validateToken, jjwt 0.12 API
│       │   └── UserDetailsServiceImpl # loadUserByUsername, ROLE_USER/ROLE_ADMIN
│       ├── util/             (4)
│       │   ├── CloudflareR2Client     # S3Client 构建, listBuckets/listObjects/putObject
│       │   ├── FileUtil               # uploadImage(MultipartFile), UUID 文件名
│       │   ├── ToEntityUtil           # DTO->Entity 静态方法, 设 createdAt/updatedAt
│       │   └── ToVOUtil               # Entity->VO 静态方法, 含关联查询组装
│       └── vo/               (10)
│           ├── CartItemVO / CategoryVO / ChatContactVO / ChatMessageVO
│           ├── DashboardVO / OrderItemVO / OrderVO / PageResult<T>
│           ├── ProductVO / Result<T> / ReviewVO / UserVO
└── src/main/resources/
    ├── application.yml                 # mybatis-plus 配置(驼峰/日志/雪花ID), JWT, R2
    └── .env / .env.example
```

---

## 5. API 权限矩阵 (SecurityConfig)

| URL 模式 | 方法 | 权限 |
|----------|------|------|
| `/api/auth/register`, `/api/auth/login` | POST | permitAll |
| `/api/categories/**` | GET | permitAll |
| `/api/categories/**` | POST/PUT/DELETE | ROLE_ADMIN |
| `/api/products/**` | ALL | permitAll |
| `/api/user/**` | ALL | permitAll (JWT 手动鉴权) |
| `/api/reviews/user/**` | ALL | permitAll |
| `/api/admin/**` | ALL | ROLE_ADMIN |
| `/ws/**` | ALL | permitAll |
| `/`, `/index.html`, `/assets/**`, `/favicon.ico` | GET | permitAll |
| 其余 | ALL | authenticated |

> CORS: `CorsConfig` 独立管理，`SecurityConfig` 注入 `CorsConfigurationSource` Bean。允许所有来源/凭据/GET-POST-PUT-DELETE-OPTIONS。

---

## 6. 数据库

### 6.1 表结构

| 表 | 主键 | 说明 |
|----|------|------|
| `user` | id (雪花) | username UNIQUE, password(Base64), role, status, credit_score, created_at, updated_at |
| `category` | id (雪花) | name, icon, sort_order |
| `product` | id (雪花) | user_id, category_id, title, price, original_price, condition, campus, status, view_count, created_at, updated_at |
| `product_image` | id (雪花) | product_id, url, sort_order |
| order_info | id (雪花) | order_no(UUID前20位), buyer_id, seller_id, total_amount, status, created_at, paid_at, shipped_at, completed_at |
| `order_item` | id (雪花) | order_id, product_id, price |
| `cart` | id (雪花) | user_id, product_id, UNIQUE(user_id,product_id) |
| `favorite` | id (雪花) | user_id, product_id, UNIQUE(user_id,product_id) |
| `chat_message` | id (雪花) | sender_id, receiver_id, product_id, content, is_read |
| `review` | id (雪花) | order_id, reviewer_id, target_id, rating, content |
| `announcement` | id (雪花) | title, content (表存在，API 未暴露) |

> `order` 表已统一为 order_info（`@TableName` 与 `init.sql` 一致）。

### 6.2 关键常量

| 枚举 | 值 | 类 |
|------|----|----|
| 角色 | 0=用户, 1=管理员 | `UserConstant` |
| 用户状态 | 0=正常, 1=封禁 | `UserConstant` |
| 信誉分 | 默认100, 好评>=3星+1, 差评-1 | `UserConstant` |
| 新旧程度 | 0=全新, 1=95新, 2=85新 | `ProductConstant` (1=全新/2=95新/3=85新（已统一）) |
| 商品状态 | 0=待审核, 1=在售, 2=已售出, 3=已下架 | `ProductConstant` |
| 订单状态 | 0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消 | `OrderInfoConstant` |
| 消息已读 | 0=未读, 1=已读 | `ChatMessage` |

---

## 7. 核心业务流程

### 7.1 认证

| 端点 | 说明 |
|------|------|
| `POST /api/auth/register` | `Base64Provider.encode(pwd)`, 昵称默认=用户名 |
| `POST /api/auth/login` | 返回 JWT (sub=userId, username, role), expiration 秒*1000=ms |
| `POST /api/auth/logout` | 无状态，客户端删 token |
| `GET /api/auth/me` | JWT 解析 userId 查库 |

### 7.2 商品

- 发布: 先 `POST /api/files/upload` 上传图片到 R2 -> 得 URL 数组 -> `POST /api/products?images=url1&images=url2` + JSON body
- 查询: `ProductQueryDTO` 支持 keyword/categoryId/campus/minPrice/maxPrice/condition/sortBy/sortOrder
- 详情: `viewCount` 自增
- 状态: 0=待审核 -> 1=在售 -> 2=已售出 | 3=已下架

### 7.3 订单

- 创建: `ToEntityUtil.toOrderInfoEntity()` 生成 `orderNo` (UUID 去横线取前20位) + `order_item`
- 状态流: 0=待付款 -> 1=待发货 -> 2=待收货 -> 3=已完成; 任意非终态 -> 4=已取消
- 支付: `payOrder` 直接改状态(未对接真实支付)
- 分页: `getOrders` 先全量查再内存过滤(status)

### 7.4 聊天

- 消息发送: `POST /api/chat/send` -> 持久化 DB + `ChatWebSocketHandler.sendMessageToUser()` 推送
- WebSocket: 仅推送通知, 连接 `ws://host/ws/chat?userId={id}`, `ConcurrentHashMap` 管理
- 联系人: 遍历消息表聚合去重, 含未读数

### 7.5 文件上传

- `POST /api/files/upload` (@RequestPart "file") / `POST /api/files/upload/batch` (@RequestPart "files")
- 路径: `{childFolder}/cuzssp-{UUID}.{ext}`, 返回 CDN URL

### 7.6 评价

- `createReview` -> 写 review 表 -> 被评者信誉分 +1(>=3星) 或 -1(<3星) -> `userMapper.updateById(target)` (未设 updatedAt)

### 7.7 数据流

```
Request DTO -> ToEntityUtil (静态方法, 手动设 createdAt/updatedAt) -> Entity
  -> Mapper.insert/updateById (@Insert/@Update 注解 SQL)
Entity -> Mapper.selectById/selectAll (@Select 注解 SQL)
  -> ToVOUtil (静态方法, 组装关联数据) -> Response VO
```

---

## 8. 配置

### 8.1 application.yml 要点

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true    # 下划线转驼峰
    log-impl: StdOutImpl                  # SQL 日志
  global-config:
    db-config:
      id-type: assign_id                  # 雪花 ID

jwt:
  expiration: ${JWT_EXPIRATION:86400}     # 秒, JwtTokenProvider 内 *1000

r2.s3:                                    # Cloudflare R2, @ConfigurationProperties
```

### 8.2 环境变量 (.env)

`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`, `R2_ACCOUNT_ID`, `R2_ACCESS_KEY_ID`, `R2_SECRET_ACCESS_KEY`, `R2_BUCKET_NAME`, `R2_ENDPOINT`, `R2_PUBLIC_URL`, `R2_REGION`, `SERVER_PORT`, `UPLOAD_MAX_FILE_SIZE`, `UPLOAD_MAX_REQUEST_SIZE`

解析方式: `spring.config.import: optional:classpath:.env[.properties]` (spring-dotenv 已注释)

---

## 9. 启动

```bash
# 后端
cd CampusSecondHandTradingPlatform_backend
cp src/main/resources/.env.example src/main/resources/.env  # 填写实际值
mysql -u root -p < ../docs/sql/init.sql
./mvnw spring-boot:run    # http://localhost:8080

# 前端
cd campus-second-hand-trading-platform-frontend
pnpm install && pnpm dev  # http://localhost:5173

# 生产
./mvnw clean package -DskipTests
java -jar target/CampusSecondHandTradingPlatform_backend-0.0.1-SNAPSHOT.jar
```

---

## 10. 开发规范

### 10.1 架构约定

- **分层**: Controller -> Service(接口) -> ServiceImpl -> Mapper(注解)
- **Mapper**: 纯 `@Select`/`@Insert`/`@Update`/`@Delete` 注解, **不继承 BaseMapper**, 列名完全显式
- **实体转换**: DTO -> `ToEntityUtil`(静态, 含时间初始化) -> Entity -> `ToVOUtil`(静态, 含关联组装) -> VO
- **时间字段**: `ToEntityUtil` 在创建时统一设 `createdAt`/`updatedAt`；更新时 `AdminServiceImpl` 和部分 `ProductServiceImpl` 手动设 `updatedAt`，其余 12 处未设 (见第11节)
- **分页**: PageHelper (`PageHelper.startPage` + `PageInfo`)
- **异常**: `BusinessException(code, message)` + `@RestControllerAdvice GlobalExceptionHandler`
- **ID**: MyBatis-Plus `assign_id` (雪花), 配置于 application.yml, 实体 `@TableId(type = IdType.ASSIGN_ID)`
- **MyBatis-Plus 用途**: 仅驼峰映射 + SQL 日志 + 雪花 ID；不使用 `BaseMapper`、`MetaObjectHandler`、代码生成器等

### 10.2 命名

| 类别 | 规范 | 示例 |
|------|------|------|
| Java 类 | UpperCamelCase | `ProductController` |
| Java 方法 | lowerCamelCase | `getProductList()` |
| DB 表 | snake_case | `product_image` |
| REST URL | 短横线+复数 | `/api/products/{id}` |

### 10.3 提交

`feat:` / `fix:` / `docs:` / `refactor:` / `style:` / `test:`

---

## 11. 已知问题

1. ~~**Product.condition 值不一致**~~: 已修正，常量值与实体注释统一为 1=全新/2=95新/3=85新。
2. ~~**Order 表名冲突**~~: 已修正，`@TableName("order_info")` 与 `init.sql` 一致。
3. **商品编辑未验证**: `ProductController.updateProduct()` 注释"前端未做"
4. **密码非 BCrypt**: `Base64Provider` + 硬编码盐值
5. **无缓存**: 未集成 Redis
6. **公告模块**: 实体/Mapper 存在, 无 Service/API
7. **订单分页**: 先全量查再内存过滤, 大数据量性能差
8. **支付**: `payOrder` 直接改状态, 未对接支付
9. **JWT 默认过期**: `application.yml` 默认 86400 秒(24h), `.env` 实际覆盖 21600 秒(6h)
10. **updatedAt 更新缺失**: 12/16 处 `updateById()` 调用未设 `updatedAt` (`UserServiceImpl` 3处, `OrderServiceImpl` 5处, `CategoryServiceImpl` 1处, `ProductServiceImpl.updateViewCount` 1处, `ReviewServiceImpl` 1处)
11. **Mapper 方法命名不统一**: `CartMapper.insertItem()`, `CategoryMapper.insertCategory()`, `ProductImageMapper.insertImage()` vs 其余 `insert()`
12. **WebSocket 无认证**: URL 参数 `userId` 标识, 未做 Token 校验

---

> **维护**: 基于 2026-07-17 后端 95 个 .java 文件完整阅读。版本日志见 `README.md`。