# 08 前端构建、类型与依赖治理

## 现状与问题

前端 `npm run build` 当前受 `node_modules/.tmp` 写入 EPERM、公告分页回调签名、订单 DOM 类型和头像包类型声明等问题影响。仓库同时存在 npm 与 pnpm 锁文件，依赖版本和安装结果可能不一致；源码中仍有 `any` 和未使用变量。

## 目标

在干净环境中可重复安装和构建，TypeScript 错误为零，依赖来源和升级策略明确。

## 实施方案

1. 选择 pnpm 或 npm 作为唯一包管理器，删除另一套锁文件并在 README/CI 固化版本。
2. 清理并重建依赖缓存，定位 EPERM 是权限、杀毒软件还是残留进程；CI 使用干净工作目录验证。
3. 修复分页回调、NodeList/HTMLElement 类型收窄、头像库声明和未使用引用；禁止新增无类型 `any`。
4. 将 API 响应定义为共享 TypeScript 类型，统一 request 错误、分页和空状态模型。
5. 增加 lint、format、vue-tsc、build 脚本，依赖升级使用 Renovate/Dependabot 或固定评审窗口。
6. 对路由、上传、聊天和管理员页面拆分异步 chunk，检查首屏和重复依赖体积。

## 改动范围

`package.json`、锁文件、tsconfig、Vite 配置、`src/api`、类型声明、受影响 Vue 页面和 CI。

## 验收标准

- 干净 Windows/CI 环境安装后 `npm run build` 或 `pnpm build` 稳定通过。
- `vue-tsc` 无 error，lint 无阻断级问题。
- 依赖安装命令和 Node/pnpm 版本在文档中唯一且可复现。
- 关键页面 chunk 和首屏体积有基线记录。

## 风险与回滚

锁文件切换会改变依赖解析。先记录现有版本树，在独立分支锁定并执行构建、页面冒烟和安全扫描后合并。
