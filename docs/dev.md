# 校园二手交易平台 — 开发说明文档 (DEV.md)

> **版本**: v2.9.0  
> **日期**: 2026-07-22  
> **项目名**: CuzShortSemester2026 — Campus Second-Hand Trading Platform  
> **说明**: 基于项目后端 106 个 Java 源码文件完整阅读编写，反映当前代码真实状态。

---

## 1. 项目概述

校园二手物品交易 Web 应用。前后端分离，含前台用户端和后台管理端。图片存储支持 Cloudflare R2 与阿里云 OSS（S3 兼容 API）。聊天消息使用 AES-256-GCM 加密存储。内置基于规则匹配的 AI 智能助手。

---

## 2. 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 4.0.7 |
| 安全 | Spring Security + JWT (jjwt) | 0.12.6 |
| 密码加密 | BCryptPasswordEncoder | (Spring Security 内置) |
| ORM | MyBatis-Plus (仅驼峰映射+雪花ID+SQL日志) | 3.5.16 |
| 分页 | PageHelper | 2.1.1 |
| 对象存储 | AWS SDK S3 (R2/OSS 双兼容) | 2.46.21 |
| 消息加密 | AES-256-GCM (javax.crypto) | JDK 内置 |
| 工具 | Lombok | 随 SB |
| Java | — | 17 |
| 数据库 | MySQL | 8.0+ |
| 前端 | Vue 3 + TS + Element Plus + Pinia + Vite | 见前端 package.json |

> **关键约定**: 
> - 未使用 Redis
> - Mapper **不继承** MyBatis-Plus `BaseMapper`，所有 SQL 用 `@Select`/`@Insert`/`@Update`/`@Delete` 注解显式编写
> - 密码使用 BCrypt (`PasswordProvider` 封装 `PasswordEncoder`)
> - 聊天消息使用 `AesEncryptionUtil` (AES-256-GCM) 加密存储，密钥配置项 `chat.secret.key`

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

#### 前台 (12 个 Controller)

| 模块 | Controller | 核心功能 |
|------|-----------|----------|
| 认证 | `AuthController` | 注册/登录/登出/获取当前用户 |
| 用户 | `UserController` | 查看信息/编辑资料/改密码/换头像 |
| 商品 | `ProductController` | 列表搜索筛选/详情(浏览量+1)/发布/编辑/状态变更/我的商品 |
| 收藏 | `FavoriteController` | 收藏/取消/列表/检查是否已收藏 |
| 购物车 | `CartController` | 加入/移除/列表 |
| 订单 | `OrderController` | 创建/列表/详情/支付/发货/收货/取消 |
| 聊天 | `ChatController` | 联系人/消息记录/发送消息(加密)/标记已读 (HTTP + WebSocket JWT推送) |
| 评价 | `ReviewController` | 创建评价(1-5星)/查看用户评价 |
| 文件 | `FileController` | 单文件/多文件上传至 R2/OSS，类型白名单+大小限制 |
| 分类 | `CategoryController` | 获取全部分类(含商品数) |
| AI助手 | `AiAssistantController` | 关键字匹配式智能问答 |
| 公告 | `AnnouncementController` | 公告列表/详情（前台只读） |

#### 后台 (1 个 Controller，6 个子模块)

| 模块 | 路由 | 功能 |
|------|------|------|
| 仪表盘 | `GET /api/admin/dashboard` | `DashboardVO` (用户/商品/订单/今日新增统计) |
| 用户管理 | `/api/admin/users` | 列表+关键字/修改状态(连带下架商品)/新增管理员 |
| 商品管理 | `/api/admin/products` | 列表+关键字+状态筛选/修改状态(无状态转换校验) |
| 订单管理 | `/api/admin/orders` | 列表+状态筛选(仅查看) |
| 分类管理 | `/api/admin/category/**` | 增删改(委托 `CategoryService`) |
| 公告管理 | `/api/admin/announcements` | 增删改公告 |

### 3.3 前端路由 (22 条)

| 路径 | 认证 | 管理员 |
|------|:--:|:--:|
| `/`, `/login`, `/register`, `/products`, `/products/:id` | | |
| `/announcements`, `/announcements/:id` | | |
| `/publish`, `/cart`, `/orders`, `/orders/:id`, `/profile`, `/my-products`, `/favorites`, `/chat` | yes | |
| `/admin` 及其 6 个子路由 | yes | yes |

---

## 4. 项目结构

### 4.1 后端文件清单 (106 个 .java)

```
CampusSecondHandTradingPlatform_backend/
├── pom.xml
├── src/main/java/com/cuzssp/campussecondhandtradingplatform_backend/
│   ├── CampusSecondHandTradingPlatformBackendApplication.java  # @SpringBootApplication + @MapperScan
│   ├── controller/           (14)
│   │   ├── AdminDashboardController  # 后台统一入口: dashboard/users/products/orders/categories/announcements
│   │   ├── AiAssistantController     # /api/ai/chat (关键字匹配)
│   │   ├── AnnouncementController    # /api/announcements (前台只读)
│   │   ├── AuthController            # /auth/register, /auth/login, /auth/logout, /auth/me
│   │   ├── CartController            # /cart
│   │   ├── CategoryController        # /categories (GET only)
│   │   ├── ChatController            # /chat/contacts, /chat/{id}, /chat/send, /chat/read
│   │   ├── FavoriteController        # /favorites
│   │   ├── FileController            # /files/upload, /files/uploads (类型白名单+大小限制+Token校验)
│   │   ├── OrderController           # /orders
│   │   ├── ProductController         # /products, /products/my
│   │   ├── ReviewController          # /reviews
│   │   ├── SpaController             # forward:/index.html (SPA fallback)
│   │   └── UserController            # /user/{id}, /user/profile, /user/password, /user/avatar
│   ├── service/              (14 接口)
│   │   └── impl/             (14 实现)
│   ├── mapper/               (11, 全注解 @Select/@Insert/@Update/@Delete, 不继承 BaseMapper)
│   │   ├── AnnouncementMapper    # selectAll/selectById/insert/updateById/deleteById
│   │   ├── CartMapper            # insertItem/deleteByUserAndProduct/selectByUserId
│   │   ├── CategoryMapper        # insertCategory/selectByIds 等
│   │   ├── ChatMessageMapper     # selectByUserId/selectByConversation/markAsRead/insert
│   │   ├── FavoriteMapper        # insert/delete/selectFavoriteProductIdsByUserId/selectByUserId
│   │   ├── OrderMapper           # selectAll/selectById/selectByUserIdOrStatus(XML)/countTodayNew
│   │   ├── OrderItemMapper       # insert/selectByOrderId
│   │   ├── ProductMapper         # selectByKeywordOrCategoryOrCampusOrStatus(XML)/selectByUserIdWithLimit(XML) 等
│   │   ├── ProductImageMapper    # insertImage/selectByProductId/selectByProductIds/deleteByProductId
│   │   ├── ReviewMapper          # insert/selectByTargetId
│   │   └── UserMapper            # selectAll/selectById/selectByIds/selectByUsername/selectByKeyword/countTodayNew 等
│   └── common/
│       ├── config/           (4)
│       │   ├── CorsConfig             # CORS Bean, allowed-origins 外部化
│       │   ├── S3Config               # @ConfigurationProperties("s3.config"), 含 storageSupport(R2/OSS)
│       │   ├── SecurityConfig         # URL 权限矩阵 + JWT Filter + BCrypt Bean
│       │   └── WebSocketConfig        # /ws/chat
│       ├── handler/          (1)
│       │   └── ChatWebSocketHandler   # JWT认证, ConcurrentHashMap<userId, session>, 仅推送通知
│       ├── constant/         (5)
│       │   ├── ChatMessageConstant    # READ_STATUS_NO=0, READ_STATUS_YES=1
│       │   ├── ConfigConstant         # R2="r2", OSS="oss" (存储支持类型)
│       │   ├── OrderInfoConstant      # STATUS_WAIT_PAY(0)..CANCELLED(4)
│       │   ├── ProductConstant        # CONDITION: 1=全新/2=几乎全新/3=有使用痕迹, STATUS: 0=待审核..3=已下架
│       │   └── UserConstant           # ROLE 0/1, STATUS 0/1, CREDIT_SCORE_DEFAULT=100
│       ├── dto/              (11)
│       │   ├── AnnouncementRequest / ChangePasswordRequest / ChatRequest
│       │   ├── CreateOrderRequest / LoginRequest / ProductQueryDTO
│       │   ├── RegisterRequest / Result<T> / ReviewRequest
│       │   ├── SendMessageRequest / UpdateProfileRequest
│       ├── entity/           (10, 全 @Data + @TableName)
│       │   ├── Announcement / CartItem / Category / ChatMessage / Favorite
│       │   ├── OrderInfo / OrderItem / Product / ProductImage / Review / User
│       ├── exception/        (2)
│       │   ├── BusinessException      # code + message
│       │   └── GlobalExceptionHandler # @RestControllerAdvice
│       ├── security/         (4)
│       │   ├── JwtAuthenticationFilter  # OncePerRequestFilter, 查库校验用户状态
│       │   ├── JwtTokenProvider         # SHA-256 HMAC签名, jjwt 0.12 API
│       │   ├── PasswordProvider         # BCrypt 封装 (encode/matches)
│       │   └── SecurityUtil             # 从 Authorization header 提取当前用户ID
│       ├── util/             (5)
│       │   ├── AesEncryptionUtil    # AES-256-GCM 加解密, 密钥由 chat.secret.key 经 SHA-256 派生
│       │   ├── FileUtil             # uploadImage(MultipartFile), UUID 文件名, 支持 CDN
│       │   ├── S3ClientUtil         # S3Client 构建, 根据 storageSupport 选择 R2(pathStyle) 或 OSS
│       │   ├── ToEntityUtil         # DTO/Request -> Entity 静态方法, 设时间字段
│       │   └── ToVOUtil             # Entity -> VO 静态方法, 含关联查询组装
│       └── vo/               (11)
│           ├── AnnouncementVO / CartItemVO / CategoryVO / ChatContactVO
│           ├── ChatMessageVO / DashboardVO / OrderItemVO / OrderVO
│           ├── PageResult<T> / ProductVO / ReviewVO / UserVO
└── src/main/resources/
    ├── application.yml                 # mybatis-plus(驼峰/日志/雪花ID), JWT, S3配置
    └── .env / .env.example
```

---

## 5. API 权限矩阵 (SecurityConfig)

| URL 模式 | 方法 | 权限 |
|----------|------|------|
| `/api/auth/register`, `/api/auth/login` | POST | permitAll |
| `/api/ai/**` | ALL | permitAll |
| `/api/categories/**` | GET | permitAll |
| `/api/categories/**` | POST/PUT/DELETE | ROLE_ADMIN |
| `/api/products/**` | GET | permitAll |
| `/api/products/**` | POST/PUT/DELETE | authenticated |
| `/api/user/**` | ALL | authenticated |
| `/api/files/**` | ALL | authenticated |
| `/api/reviews/user/**` | ALL | permitAll |
| `/api/announcement/**` | GET | permitAll |
| `/api/admin/**` | ALL | ROLE_ADMIN |
| `/ws/**` | ALL | permitAll |
| `/`, `/index.html`, `/assets/**`, `/favicon.ico` | GET | permitAll |
| 其余 | ALL | authenticated |

> **变更**: v2.9.0 起 `/api/products/**` 的 POST/PUT/DELETE 改为需认证；`/api/user/**` 和 `/api/files/**` 从 permitAll 改为 authenticated；新增 `/api/ai/**` 和 `/api/announcement/**` 路由。

> CORS: `CorsConfig` 独立管理，`allowed-origins` 通过 `cors.allowed-origins` 外部化配置；允许凭据/GET-POST-PUT-DELETE-OPTIONS。

---

## 6. 数据库

### 6.1 表结构

| 表 | 主键 | 说明 |
|----|------|------|
| `user` | id (雪花) | username UNIQUE, password(BCrypt 60字符), role, status, credit_score, created_at, updated_at |
| `category` | id (雪花) | name, icon, sort_order, created_at |
| `product` | id (雪花) | user_id, category_id, title, description, price, original_price, condition, campus, status, view_count, created_at, updated_at |
| `product_image` | id (雪花) | product_id, url, sort_order |
| `order_info` | id (雪花) | order_no(UUID前20位), buyer_id, seller_id, total_amount, status, remark, created_at, updated_at, paid_at, shipped_at, completed_at |
| `order_item` | id (雪花) | order_id, product_id, price, created_at |
| `cart` | id (雪花) | user_id, product_id, UNIQUE(user_id,product_id), created_at |
| `favorite` | id (雪花) | user_id, product_id, UNIQUE(user_id,product_id), created_at |
| `chat_message` | id (雪花) | sender_id, receiver_id, product_id, content(AES密文), is_read, created_at |
| `review` | id (雪花) | order_id, reviewer_id, target_id, rating, content, created_at |
| `announcement` | id (雪花) | title, content, created_at, updated_at |

### 6.2 关键常量

| 枚举 | 值 | 类 |
|------|----|----|
| 角色 | 0=用户, 1=管理员 | `UserConstant` |
| 用户状态 | 0=正常, 1=封禁 | `UserConstant` |
| 信誉分 | 默认100, 好评>=3星+1, 差评-1 | `UserConstant` |
| 新旧程度 | 1=全新, 2=几乎全新, 3=有使用痕迹 | `ProductConstant` |
| 商品状态 | 0=待审核, 1=在售, 2=已售出, 3=已下架 | `ProductConstant` |
| 订单状态 | 0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消 | `OrderInfoConstant` |
| 消息已读 | 0=未读, 1=已读 | `ChatMessageConstant` |
| 存储类型 | "r2"=Cloudflare R2, "oss"=阿里云OSS | `ConfigConstant` |

### 6.3 初始数据

- 8 个默认分类: 教材教辅/数码产品/生活用品/服饰鞋包/运动户外/美妆护肤/图书文具/其他
- 默认管理员: `admin` / `admin123` (BCrypt密文，部署后请立即修改)

---

## 7. 核心业务流程

### 7.1 认证

| 端点 | 说明 |
|------|------|
| `POST /api/auth/register` | `PasswordProvider.encode(pwd)` (BCrypt), 昵称默认=用户名 |
| `POST /api/auth/login` | 校验BCrypt密码, 返回JWT (sub=userId, username, role), expiration单位秒 |
| `POST /api/auth/logout` | 无状态，客户端删 token |
| `GET /api/auth/me` | JWT 解析 userId 查库 |

### 7.2 商品

- 发布: 先上传图片到 R2/OSS -> 得 URL 数组 -> `POST /api/products?images=url1&images=url2` + JSON body
- 查询: `ProductQueryDTO` 支持 keyword/categoryId/campus，分页默认 12 条/页
- 详情: `viewCount` 原子自增 (`UPDATE ... SET view_count = view_count + 1`)
- 状态转换(用户侧): 仅允许 1(在售)->3(已下架) 和 3->0(待审核)，由 `ALLOWED_TRANSITIONS` 白名单控制
- 批量预加载: `buildUserMap/buildCategoryMap/buildImageMap` 一次查询避免 N+1

### 7.3 订单

- 创建: `@Transactional`, 行锁 `SELECT ... FOR UPDATE` 防超卖, 买家≠卖家校验
- 状态流: 0=待付款 -> 1=待发货 -> 2=待收货 -> 3=已完成; 任意非终态 -> 4=已取消
- 支付: `payOrder` 直接改状态(未对接真实支付)
- 分页: `selectByUserIdOrStatus` XML 动态 SQL，status 传 null 查全部

### 7.4 聊天

- 消息发送: `POST /api/chat/send` -> AES-256-GCM 加密 -> 持久化 DB + `ChatWebSocketHandler.sendMessageToUser()` 推送 JSON 通知
- WebSocket: `ws://host/ws/chat?token={jwt}`, JWT 认证, 仅推送 `{"type":"new_message","from":senderId}`
- 联系人: 遍历消息表聚合去重, 含未读数
- 消息解密: 读取时 `aesEncryptionUtil.decrypt()` 还原明文返回前端

### 7.5 文件上传

- `POST /api/files/upload` / `POST /api/files/uploads` (多文件)
- 安全校验: Token验证 + 文件类型白名单(jpg/jpeg/png/gif/webp/svg) + 大小限制(10MB)
- 路径: `{childFolder}/cuzssp-{UUID}.{ext}`, 返回 CDN URL 或 Endpoint URL
- 存储适配: `S3ClientUtil` 根据 `s3.config.storage-support` 选择 R2(pathStyle) 或 OSS

### 7.6 评价

- `createReview` -> 写 review 表 -> 被评者信誉分 +1(>=3星) 或 -1(<3星) -> `userMapper.updateById(target)`

### 7.7 AI 助手 (关键字匹配)

- `POST /api/ai/chat` -> 关键字匹配 (含"发布"/"购买"/"订单"/"联系"等) -> 返回预设回复
- 前端: 右下角浮动按钮，点击展开聊天面板

### 7.8 数据流

```
Request DTO -> ToEntityUtil (静态方法, 手动设时间) -> Entity
  -> Mapper.insert/updateById (@Insert/@Update 注解 SQL)
Entity -> Mapper.selectById/selectAll (@Select 注解 SQL)
  -> ToVOUtil (静态方法, 组装关联数据) -> Response VO -> Result<T> 包装
```

---

## 8. 配置

### 8.1 application.yml 要点

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: StdOutImpl
  global-config:
    db-config:
      id-type: assign_id          # 雪花 ID

jwt:
  secret: ${JWT_SECRET}           # SHA-256 派生 HMAC 密钥
  expiration: ${JWT_EXPIRATION:86400}  # 秒

s3.config:                        # @ConfigurationProperties("s3.config")
  storage-support: ${STORAGE_SUPPORT:r2}  # r2 或 oss
  account-id: ${R2_ACCOUNT_ID:}
  access-key: ${R2_ACCESS_KEY_ID:}
  secret-key: ${R2_SECRET_ACCESS_KEY:}
  endpoint: ${R2_ENDPOINT:}
  bucket-name: ${R2_BUCKET_NAME:}
  child-folder: ${R2_CHILD_FOLDER:}
  cdn-domain: ${R2_PUBLIC_URL:}
  region: ${R2_REGION:auto}

chat:
  secret:
    key: ${CHAT_SECRET_KEY:}      # AES-256-GCM 加密密钥，经 SHA-256 派生

cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:*}
```

### 8.2 环境变量

`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`, `STORAGE_SUPPORT`, `R2_ACCOUNT_ID`, `R2_ACCESS_KEY_ID`, `R2_SECRET_ACCESS_KEY`, `R2_BUCKET_NAME`, `R2_ENDPOINT`, `R2_PUBLIC_URL`, `R2_REGION`, `R2_CHILD_FOLDER`, `CHAT_SECRET_KEY`, `CORS_ALLOWED_ORIGINS`, `SERVER_PORT`, `UPLOAD_MAX_FILE_SIZE`, `UPLOAD_MAX_REQUEST_SIZE`

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
- **Mapper**: 纯 `@Select`/`@Insert`/`@Update`/`@Delete` 注解 + XML 动态 SQL(ProductMapper/OrderMapper), **不继承 BaseMapper**, 列名完全显式
- **实体转换**: DTO/Request -> `ToEntityUtil`(静态, 含时间初始化) -> Entity -> `ToVOUtil`(静态, 含关联组装) -> VO
- **时间字段**: `ToEntityUtil` 在创建时统一设 `createdAt`/`updatedAt`；更新时各 ServiceImpl 手动设 `updatedAt`
- **分页**: PageHelper (`PageHelper.startPage` + `PageInfo`)
- **异常**: `BusinessException(code, message)` + `@RestControllerAdvice GlobalExceptionHandler`
- **统一响应**: `Result<T>` (code, message, data)，`PageResult<T>` (records, total, page, pageSize)
- **ID**: MyBatis-Plus `assign_id` (雪花), 配置于 application.yml, 实体 `@TableId(type = IdType.ASSIGN_ID)`
- **Bean注入**: 构造注入，`@RequiredArgsConstructor` + `private final`
- **事务**: 关键写操作使用 `@Transactional(rollbackFor = Exception.class)`

### 10.2 命名

| 类别 | 规范 | 示例 |
|------|------|------|
| Java 类 | UpperCamelCase | `ProductController` |
| Java 方法 | lowerCamelCase | `getProductList()` |
| DB 表 | snake_case | `product_image` |
| REST URL | 短横线+复数 | `/api/products/{id}` |
| Mapper XML | 与接口同名同包 | `ProductMapper.xml` |

### 10.3 提交

`feat:` / `fix:` / `docs:` / `refactor:` / `style:` / `test:`

---

## 11. 已知问题与改进建议

### 安全问题

1. **AI助手无认证**: `/api/ai/**` 设为 `permitAll`，任何人都可调用。影响有限（仅关键字匹配无数据泄露），但建议改为 `authenticated`。
2. **管理员商品状态修改无校验**: `AdminServiceImpl` -> `ProductServiceImpl.updateProduct(Long, Integer)` 不检查状态转换合法性，也不检查商品是否存在，与用户侧不一致。
3. **JWT 无刷新机制**: Token 过期后需重新登录，无 refresh token。

### 功能完善

4. **AI助手为关键字匹配**: 非真实 AI 模型调用，回复模板有限。
5. **支付为模拟**: `payOrder` 直接改状态，未对接真实支付网关。
6. **无缓存**: 未集成 Redis，高频查询(分类、首页商品)直接查库。
7. **订单分页**: `selectByUserIdOrStatus` 需进一步确认 XML 是否含 `LIMIT`（PageHelper 拦截方式依赖配置）。
8. **`updatedAt` 部分缺失**: 少数更新操作未设 `updatedAt`，建议使用 MyBatis-Plus `MetaObjectHandler` 自动填充或数据库 `ON UPDATE CURRENT_TIMESTAMP`。

### 代码质量

9. **`AiAssistantServiceImpl` 拼写错误**: "只能助手" 应为 "智能助手"。
10. **`OrderMapper.updateById` 未更新 `updated_at`**: SQL 中缺少 `updated_at=#{updatedAt}`。
11. **联系人查询全量加载**: `ChatServiceImpl.getContacts` 加载用户全部消息后 Java 聚合，消息量大时性能差，建议用 SQL 聚合。
12. **`ProductServiceImpl` 详情查询两次**: `getProductDetail` 中 `selectById` + `addViewCount` 后再 `selectById` 获取最新浏览量，可合并为一次。

---

> **维护**: 基于 2026-07-22 后端 106 个 .java 文件及前端 46 个源文件完整阅读。版本日志见 `docs/VERSIOINLOG.md`。
