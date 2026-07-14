<template>
  <div class="admin-layout" :class="{ collapsed }">
    <el-container class="admin-container">
      <el-aside :width="collapsed ? '64px' : '220px'" class="sidebar">
        <div class="logo-area">
          <el-button class="collapse-btn" @click="collapsed = !collapsed" text>
            <el-icon :size="20">
              <Fold v-if="!collapsed" />
              <Expand v-else />
            </el-icon>
          </el-button>
          <span class="logo-text" v-show="!collapsed">后台管理</span>
        </div>
        <el-menu
          :default-active="route.path"
          router
          :collapse="collapsed"
          class="side-menu"
          background-color="transparent"
          text-color="var(--sidebar-text)"
          active-text-color="#409eff"
        >
          <el-menu-item index="/admin">
            <el-icon><DataLine /></el-icon>
            <span>Dashboard</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders">
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
        </el-menu>
        <div class="sidebar-bottom">
          <el-button class="back-btn" @click="$router.push('/')" text>
            <el-icon :size="18"><Back /></el-icon>
            <span v-show="!collapsed">返回前台</span>
          </el-button>
        </div>
      </el-aside>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRoute } from "vue-router";
import { DataLine, User, Goods, List, Expand, Fold, Back } from "@element-plus/icons-vue";
const route = useRoute();
const collapsed = ref(false);
</script>

<style scoped>
.admin-layout { height: 100vh; overflow: hidden; }
.admin-container { height: 100%; }
.sidebar {
  background: var(--bg-sidebar);
  color: #fff;
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: width .3s;
  overflow: hidden;
  --sidebar-text: #bfcbd9;
  flex-shrink: 0;
}
html.dark .sidebar {
  --sidebar-text: #a0aec0;
  background: #1a1a2e;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 12px;
  flex-shrink: 0;
  min-height: 56px;
  height: 56px;
  border-bottom: 1px solid rgba(255,255,255,.08);
}
.logo-text {
  font-size: 17px;
  font-weight: 700;
  white-space: nowrap;
  color: #fff;
}

.collapse-btn {
  color: var(--sidebar-text) !important;
  padding: 4px !important;
  min-width: auto !important;
  width: 32px;
  height: 32px;
  border-radius: 6px !important;
  flex-shrink: 0;
}
.collapse-btn:hover {
  color: #fff !important;
  background: rgba(255,255,255,.1) !important;
}

.side-menu {
  border-right: none !important;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}
.side-menu :deep(.el-menu-item) {
  color: var(--sidebar-text);
  transition: all .2s;
}
.side-menu :deep(.el-menu-item:hover) {
  background: rgba(255,255,255,.06) !important;
  color: #fff !important;
}
.side-menu :deep(.el-menu-item.is-active) {
  color: #409eff !important;
  background: rgba(64,158,255,.12) !important;
}

.sidebar-bottom {
  padding: 8px;
  flex-shrink: 0;
  border-top: 1px solid rgba(255,255,255,.08);
}

.back-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 8px;
  color: var(--sidebar-text) !important;
  font-size: 14px;
  transition: all .2s;
  white-space: nowrap;
}
.back-btn:hover {
  background: rgba(255,255,255,.08) !important;
  color: #fff !important;
}

.admin-layout.collapsed .back-btn {
  padding: 10px;
  border-radius: 50%;
  width: 40px;
  margin: 0 auto;
}

.admin-main {
  overflow-y: auto;
  padding: 20px;
  background: var(--bg-page);
}

/* 暗色模式菜单文字 */
html.dark .side-menu :deep(.el-menu-item) { color: #a0aec0; }
html.dark .side-menu :deep(.el-menu-item:hover) { background: rgba(255,255,255,.06) !important; color: #e0e0e0 !important; }
html.dark .side-menu :deep(.el-menu-item.is-active) { color: #409eff !important; }

/* Element Plus menu collapse 内部样式覆盖 */
.el-menu--collapse { width: 64px; }
.el-menu--collapse .el-menu-item { padding: 0 !important; justify-content: center; }
</style>
