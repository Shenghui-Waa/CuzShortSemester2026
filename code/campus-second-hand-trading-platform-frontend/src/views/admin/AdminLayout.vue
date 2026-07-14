<template>
  <div class="admin-layout">
    <el-container class="admin-container">
      <el-aside :width="collapsed ? '64px' : '200px'" class="sidebar">
        <div class="logo-area">
          <span class="logo" v-show="!collapsed">后台管理</span>
          <el-button class="collapse-btn" @click="collapsed = !collapsed" text>
            <el-icon :size="20"><component :is="collapsed ? Expand : Fold" /></el-icon>
          </el-button>
        </div>
        <el-menu :default-active="route.path" router :collapse="collapsed" class="side-menu"
          background-color="transparent" text-color="#bfcbd9" active-text-color="#409eff">
          <el-menu-item index="/admin"><el-icon><DataLine /></el-icon><span>Dashboard</span></el-menu-item>
          <el-menu-item index="/admin/users"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
          <el-menu-item index="/admin/products"><el-icon><Goods /></el-icon><span>商品管理</span></el-menu-item>
          <el-menu-item index="/admin/orders"><el-icon><List /></el-icon><span>订单管理</span></el-menu-item>
        </el-menu>
        <div class="sidebar-bottom">
          <div class="back-btn" @click="$router.push('/')" :class="{ collapsed }">
            <el-icon :size="18"><Back /></el-icon>
            <span v-show="!collapsed">返回前台</span>
          </div>
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
.sidebar { background: var(--bg-sidebar); color: #fff; height: 100%; display: flex; flex-direction: column; transition: width .3s; overflow: hidden; }
.logo-area { display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; flex-shrink: 0; min-height: 56px; }
.logo { font-size: 18px; font-weight: 700; white-space: nowrap; }
.collapse-btn { color: #bfcbd9 !important; padding: 4px; min-width: auto; }
.collapse-btn:hover { color: #fff !important; background: rgba(255,255,255,.08) !important; }
.side-menu { border-right: none !important; flex: 1; overflow-y: auto; overflow-x: hidden; }
.sidebar-bottom { padding: 8px; flex-shrink: 0; }
.back-btn { display: flex; align-items: center; gap: 8px; padding: 10px 14px; border-radius: 8px; cursor: pointer; color: #bfcbd9; font-size: 14px; transition: all .2s; white-space: nowrap; justify-content: center; }
.back-btn:hover { background: rgba(255,255,255,.08); color: #fff; }
.back-btn.collapsed { padding: 10px; border-radius: 50%; width: 40px; height: 40px; margin: 0 auto; }
.admin-main { overflow-y: auto; padding: 20px; }

html.dark .side-menu, html.dark .sidebar { --el-menu-bg-color: var(--bg-sidebar); }
html.dark .sidebar :deep(.el-menu-item) { color: #bfcbd9; }
html.dark .sidebar :deep(.el-menu-item:hover) { background: rgba(255,255,255,.05); }
html.dark .sidebar :deep(.el-menu-item.is-active) { color: #409eff; }
</style>
