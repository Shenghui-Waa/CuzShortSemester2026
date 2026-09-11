# CuzShortSemester2026 项目概述与技术审阅

> 审阅日期：2026-09-11

## 项目定位

CuzShortSemester2026 是面向校园场景的前后端分离二手交易平台，提供商品发布与检索、购物车、收藏、订单流转、评价、站内聊天、公告、AI 知识问答及管理员运营后台。

## 总体架构

```text
Vue 3 + TypeScript + Element Plus + Pinia + Axios
                │ Vite proxy (/api, /ws)
Spring Boot 4 + Spring Security/JWT + WebSocket
                │ Controller -> Service -> Mapper/XML -> Entity
MySQL 8                         Cloudflare R2 / Aliyun OSS
```

后端按 Controller、Service、Mapper、Entity 分层，DTO/VO 与统一 `Result<T>` 响应隔离接口模型；常规 SQL 使用 MyBatis 注解，动态查询使用 XML。关键写操作使用事务，订单创建通过行锁避免并发超卖。前端按路由、API、Pinia stores、组件和样式目录组织，Vite 默认将 `/api` 与 `/ws` 转发到后端 8080 端口。

## 技术栈

| 层级 | 技术 |
|---|---|
| 后端 | Java 17、Spring Boot 4.0.7、Maven |
| 安全 | Spring Security、JJWT 0.12.6、BCrypt |
| 持久化 | MyBatis-Plus 3.5.16、PageHelper、MySQL 8+ |
| 实时与存储 | Spring WebSocket、AES-256-GCM、S3 SDK（R2/OSS） |
| 前端 | Vue 3.5、TypeScript 6、Vite 8、Vue Router 4、Pinia 2、Element Plus 2、Sass |

## 功能与数据

公开页面包括首页、登录注册、商品列表/详情、公告列表/详情；登录用户可发布商品、管理购物车/收藏/订单/个人资料并聊天；管理员拥有仪表盘、用户、商品、订单、分类和公告管理。主要数据表为 `user`、`category`、`product`、`product_image`、`order_info`、`order_item`、`cart`、`favorite`、`chat_message`、`review`、`announcement`。商品状态为待审核、在售、已售出、已下架；订单状态为待付款、待发货、待收货、已完成、已取消。

## 配置与启动

1. 准备 JDK 17、Maven（或修复 Maven Wrapper）、MySQL 8、Node.js 与 pnpm/npm。
2. 执行 `docs/sql/init.sql` 创建数据库。
3. 复制后端 `.env.example` 为 `.env`，填写数据库、JWT、聊天加密和 R2/OSS 配置。
4. 后端运行 `mvn spring-boot:run`，默认 `http://localhost:8080`。
5. 前端运行 `pnpm install` 与 `pnpm dev`，默认 `http://localhost:5173`。

## 风险与改进

- 生产环境必须覆盖默认数据库密码、JWT 密钥和聊天密钥，禁止提交真实 `.env`。
- WebSocket 当前允许任意 Origin；应配置可信来源并保留 JWT 校验。
- `/api/ai/**` 当前匿名放行，建议鉴权、限流并记录调用量。
- SecurityConfig 放行的是单数 `/api/announcement/**`，前台控制器实际为复数 `/api/announcements`，需统一。
- 前端路由守卫依赖 localStorage 的 `token` 和 `role`，不能替代后端授权。
- 文件上传依赖 S3/R2 配置；生产应限制扩展名、大小、内容类型和访问策略。
- MyBatis `StdOutImpl` 会输出 SQL，生产环境应关闭。
- `user` 是 MySQL 兼容性敏感表名，迁移数据库时应评估转义或重命名。

## 验证结论

- 已完成目录、配置、前后端源码和 SQL 结构审阅。
- 后端 Maven Wrapper 在当前 PowerShell 环境无法启动 Maven，未完成编译验证。
- 前端 `npm run build` 受 `node_modules/.tmp` 写入 EPERM、分页回调/DOM 类型错误及头像包类型声明缺失影响。
- 测试以 Spring 上下文、JWT/BCrypt 为主，Controller、数据库集成、WebSocket 和完整权限矩阵仍需补充。

## 相关文档

- [项目 README](../README.md)
- [开发说明](./dev.md)
- [版本日志](./versionlog.md)
- [数据库初始化脚本](./sql/init.sql)
