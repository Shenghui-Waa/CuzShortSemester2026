# CuzShortSemester2026

2026 年 CUZ 短学期项目：校园二手交易平台。

这是一个前后端一体化的校园二手交易 Web 应用。项目包含用户端、管理员端和后端 API，支持商品、订单、购物车、收藏、评价、聊天、公告、AI 助手、文件上传和管理后台。

当前仓库使用重制版后端：

- 后端：Spring Boot 4.0.8、Java 17、Spring Security、JWT、MyBatis-Plus、PageHelper
- 数据库：开发环境 SQLite，生产环境 MySQL 8+
- 前端：Vue 3、TypeScript、Element Plus、Pinia、Vite
- 对象存储：AWS SDK S3，兼容 Cloudflare R2 和阿里云 OSS
- 实时通信：WebSocket
- 消息加密：AES-256-GCM
- 密码加密：BCrypt
- 登录认证：JWT `jti` 撤销机制，退出登录后当前 Token 立即失效
- 时间策略：后端统一使用 UTC，数据库使用无时区 `DATETIME` 保存

## 项目结构

```text
CuzShortSemester2026/
├── README.md
├── LICENSE
├── code/
│   ├── CampusSecondHandTradingPlatformBackend/
│   │   ├── pom.xml
│   │   ├── env.yml.example
│   │   └── src/
│   │       ├── main/java/com/cuzssp/campussecondhandtradingplatformbackend/
│   │       │   ├── controller/
│   │       │   ├── service/
│   │       │   ├── mapper/
│   │       │   └── common/
│   │       └── main/resources/
│   │           ├── application.yml
│   │           ├── application-dev.yml
│   │           ├── application-prod.yml
│   │           ├── dev/
│   │           ├── prod/
│   │           └── static/          # 已内联的前端构建产物
│   └── campus-second-hand-trading-platform-frontend/
│       ├── package.json
│       ├── vite.config.ts
│       └── src/
└── docs/
    ├── dev.md
    ├── overlook.md
    └── versionlog.md
```

## 快速开始

### 环境要求

- JDK 17
- Maven 3.9+
- Node.js 20+（仅前端开发或重新构建前端时需要）
- SQLite（开发环境由 SQLite JDBC 驱动提供）
- MySQL 8+（生产环境）
- 可选：Cloudflare R2 或阿里云 OSS 账号

### 配置后端

进入后端目录：

```bash
cd code/CampusSecondHandTradingPlatformBackend
```

复制配置模板：

```bash
cp env.yml.example env.yml
```

Windows PowerShell：

```powershell
Copy-Item env.yml.example env.yml
```

编辑 `env.yml`。

开发环境示例：

```yaml
profiles-active: dev

db:
  url: ./data/campus-second-hand.db
```

生产环境示例：

```yaml
profiles-active: prod

db:
  url: //localhost:3306/cuzssp
  username: your-db-username
  password: your-db-password
```

还需要填写：

- `external.jwt.secret`
- `external.chat.secret`
- `external.s3.config.*`
- `external.cors.allow-origins`

不要把真实密钥提交到 Git。

### 启动后端并访问内联前端

```bash
cd code/CampusSecondHandTradingPlatformBackend
mvn spring-boot:run
```

启动后访问：

```text
http://localhost:8080/
```

后端会直接提供 `src/main/resources/static/` 中的前端构建产物。前端页面路由由 `SpaController` 转发到 `index.html`，因此不需要单独启动 Vite。

API 基础路径：

```text
http://localhost:8080/api
```

WebSocket 地址：

```text
ws://localhost:8080/ws/chat
```

### 前端独立开发

需要修改 Vue 页面时，在另一个终端运行：

```bash
cd code/campus-second-hand-trading-platform-frontend
npm install
npm run dev
```

开发地址：

```text
http://localhost:5173/
```

Vite 会将 `/api` 和 `/ws` 代理到后端 8080 端口。

前端类型检查和生产构建：

```bash
npm run build
```

构建完成后，将前端 `dist/` 内容复制到后端：

```text
code/CampusSecondHandTradingPlatformBackend/src/main/resources/static/
```

复制后，后端即可提供最新前端页面。

## 数据库初始化

### 开发环境 SQLite

开发 profile 使用：

```text
src/main/resources/dev/schema.sql
src/main/resources/dev/data.sql
```

应用启动时会自动执行初始化脚本。开发数据脚本使用 SQLite 的幂等插入语法，重复启动不会因为默认分类和管理员账号已存在而失败。

当前开发 schema 使用 SQLite `INTEGER AUTOINCREMENT` 主键，并启用
`PRAGMA foreign_keys = ON`。商品、订单、购物车、收藏、聊天、评价等表均声明显式外键。

建议使用文件数据库：

```yaml
db:
  url: ./data/campus-second-hand.db
```

如果使用 SQLite 内存数据库，需要将连接池限制为单连接，否则不同连接可能看到不同的内存数据库。

### 生产环境 MySQL

生产 profile 使用：

```text
src/main/resources/prod/schema.sql
src/main/resources/prod/data.sql
```

MySQL 数据库应提前创建，并通过 `db.url` 指定：

```yaml
db:
  url: //localhost:3306/cuzssp
```

生产数据脚本使用 MySQL 兼容的幂等插入语法。正式部署前请确认数据库账号具有建表、建索引和写入初始化数据的权限。

当前生产 schema 使用 `BIGINT AUTO_INCREMENT` 主键，并包含与开发库一致的外键关系。
已有数据库不会因为 `CREATE TABLE IF NOT EXISTS` 自动补充新字段和外键，升级前应先备份数据库，
检查孤儿数据，再使用独立迁移脚本调整表结构。

## 认证与时间处理

### 登录与退出

- 登录成功后签发的 JWT 包含唯一 `jti`。
- 退出登录会把当前 Token 写入 `revoked_token` 表，后续 REST 请求立即返回 401。
- 退出只撤销当前 Token，不会影响同一账号在其他设备或标签页签发的 Token。
- WebSocket 握手和消息推送前都会重新检查撤销状态。
- 旧版不含 `jti` 的 Token 会被视为无效，升级后需要重新登录一次。

### UTC 时间

- 后端业务时间统一通过 `UtcTime` 生成，时间为 UTC。
- MySQL 连接初始化时设置 `SET time_zone = '+00:00'`，SQLite 的
  `CURRENT_TIMESTAMP` 本身也按 UTC 保存。
- 数据库继续使用无时区 `DATETIME`，前端将接口返回的无时区时间按 UTC 解析后再转换为本地时间显示。

## 功能模块

### 用户端

- 注册、登录、退出登录和当前用户信息
- JWT 身份认证与角色权限
- 个人资料、密码和头像管理
- 商品发布、编辑、浏览、搜索和筛选
- 商品图片上传
- 商品字段统一使用 `state`，收藏状态统一使用 `isFavorite`
- 购物车和收藏
- 创建订单、模拟支付、发货、收货和取消
- 评价及信誉分变更
- 聊天消息、未读状态和 WebSocket 通知
- 公告浏览
- 规则匹配式 AI 助手

### 管理端

- 数据仪表盘
- 用户查询、禁用、删除、重置密码和新增管理员
- 商品查询和状态管理
- 用户与商品 ID 按 `...` 加后四位显示，避免后台列表展示过长
- 订单查询
- 分类增删改
- 公告增删改

## API 路径约定

前端 Axios 的 `baseURL` 为 `/api`，以下路径省略 `/api` 前缀。

### 公共和用户接口

| 模块 | 路径 |
| --- | --- |
| 认证 | `/auth/**` |
| 用户 | `/user/**` |
| 商品 | `/product/**` |
| 分类读取 | `GET /category/**` |
| 公告读取 | `GET /announcement/**` |
| 评价 | `/review/**` |
| 购物车 | `/cart/**` |
| 收藏 | `/favorite/**` |
| 订单 | `/order/**` |
| 聊天 | `/chat/**` |
| 文件 | `/files/**` |
| AI | `/ai/**` |

### 管理接口

| 模块 | 路径 |
| --- | --- |
| 仪表盘 | `/admin/dashboard` |
| 用户管理 | `/admin/user/**` |
| 商品管理 | `/admin/product/**` |
| 订单管理 | `/admin/order/**` |
| 分类管理 | `/admin/category/**` |
| 公告管理 | `/admin/announcement/**` |

### 权限规则

- 登录和注册公开访问。
- 商品、分类、公告的读取接口按 Controller 规则开放。
- 用户中心、订单、购物车、收藏、聊天、文件上传需要登录。
- 管理接口需要 `ROLE_ADMIN`。
- 无效或过期 JWT 返回 401。
- 已退出登录的 JWT 立即失效；缺少 `jti` 的旧 Token 也返回 401。
- 已登录但没有权限返回 403。

## 后端分层

### Controller

负责 HTTP 路由、请求参数接收和 `Result` 响应封装。

### Service

负责业务规则、权限检查、事务边界和领域流程。

### Mapper

- 原生增删改查优先直接调用 MyBatis-Plus `BaseMapper`。
- 自定义简单 SQL 使用 MyBatis 注解。
- 复杂动态 SQL 使用 XML。
- SQL 需要同时兼容 SQLite 和 MySQL。

### Common

包含配置、安全、异常、实体、DTO、VO、常量和工具类。

## 前端构建：

```bash
cd code/campus-second-hand-trading-platform-frontend
npm run build
```

## 文档

- [**`版本日志`↗**](./docs/versionlog.md) `v2.20.1` `2026.9.17`

## 当前限制

- 尚未进行真实 MySQL 测试库连接验证。

## License

[Apache 2.0 ↗](./LICENSE)
