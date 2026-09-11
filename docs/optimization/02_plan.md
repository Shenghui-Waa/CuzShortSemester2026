# 02 统一认证与会话生命周期

## 现状与问题

前端路由守卫主要读取 localStorage 的 `token` 和 `role`，角色值可被客户端篡改；后端 JWT 为无状态 token，当前没有 refresh token、撤销机制和统一 401 会话处理的完整设计。前端启动时虽请求当前用户，但守卫与后端授权存在重复且可能不一致。

## 目标

前端只负责导航体验，后端负责唯一授权事实；token 过期可恢复，退出后旧 token 不再可用，用户状态变化能及时生效。

## 实施方案

1. 后端统一从认证上下文取得用户和角色，所有管理员资源使用方法级或 URL 级角色校验。
2. 增加短时 access token 与 HttpOnly、Secure、SameSite refresh token；refresh token 保存哈希、设备标识、过期时间和撤销状态。
3. 增加登录、刷新、退出、注销设备接口；密码修改和封禁时撤销相关 refresh token。
4. Axios 统一响应拦截器处理 401，单次刷新并重放原请求，失败后清理状态并跳转登录，避免并发刷新风暴。
5. 路由守卫只判断 Pinia 中的认证状态和路由 meta，不再把 localStorage 的 role 当作安全凭据。

## 改动范围

后端认证 DTO、token service、refresh token 表/DAO、SecurityConfig；前端 auth store、request 拦截器、router。需补充数据库迁移脚本。

## 验收标准

- 篡改 localStorage role 无法访问管理员 API。
- access token 过期时普通请求自动刷新一次；刷新失败统一退出。
- 退出、改密、封禁后旧 refresh token 不能换取新 token。
- 多标签页和并发请求不会重复刷新或死循环。

## 风险与回滚

引入 cookie 会影响跨域配置。先通过 feature flag 保留旧 token 登录兼容窗口，确认前端切换完成后再关闭旧流程。
