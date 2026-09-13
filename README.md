# CuzShortSemester2026
> 2026 年 CUZ 短学期项目：校园二手交易平台

这是一个前后端分离的校园二手交易 Web 应用，覆盖商品、订单、购物车、收藏、评价、聊天、公告、AI 助手和管理员后台。后端使用 Spring Boot + MySQL，前端使用 Vue 3 + TypeScript。

## 快速开始

### 环境
- JDK 17、Maven、MySQL 8+
- Node.js、pnpm 或 npm

### 后端
```bash
cd code/CampusSecondHandTradingPlatform_backend
cp src/main/resources/.env.example src/main/resources/.env
# 填写数据库、JWT、CHAT_SECRET_KEY、R2/OSS 等配置
mysql -u root -p < ../../docs/sql/init.sql
mvn spring-boot:run
```
默认地址：`http://localhost:8080`。

### 前端
```bash
cd code/campus-second-hand-trading-platform-frontend
pnpm install
pnpm dev
```
默认地址：`http://localhost:5173`。Vite 已将 `/api` 和 `/ws` 代理到后端 8080 端口。

## 目录
```text
code/
├── CampusSecondHandTradingPlatform_backend/      Spring Boot 后端
└── campus-second-hand-trading-platform-frontend/ Vue 前端
docs/
├── overlook.md                                   项目概述与技术审阅
├── dev.md                                        开发说明
├── versionlog.md                                 版本日志
└── sql/init.sql                                  数据库初始化
```

## 功能概览
- 用户注册、登录、JWT 会话和个人资料
- 商品发布、图片上传、搜索筛选、状态管理
- 购物车、收藏、订单创建及支付/发货/收货模拟流程
- AES 加密聊天消息、WebSocket 新消息通知
- 评价与信誉分、公告、规则匹配式 AI 助手
- 管理员仪表盘及用户、商品、订单、分类、公告管理

## 文档与版本
- [项目概述与技术审阅](./docs/overlook.md)
- [开发说明](./docs/dev.md)
- [版本日志](./docs/versionlog.md)（当前记录版本：v2.15.18）

构建和安全限制、已知问题及改进建议以 `docs/overlook.md` 为准。

## License
[Apache 2.0](./LICENSE)
