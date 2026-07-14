<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="200px" class="sidebar">
        <div class="logo">后台管理</div>
        <el-menu :default-active="route.path" router>
          <el-menu-item index="/admin"><el-icon><DataLine /></el-icon>仪表盘</el-menu-item>
          <el-menu-item index="/admin/users"><el-icon><User /></el-icon>用户管理</el-menu-item>
          <el-menu-item index="/admin/products"><el-icon><Goods /></el-icon>商品管理</el-menu-item>
          <el-menu-item index="/admin/orders"><el-icon><List /></el-icon>订单管理</el-menu-item>
        </el-menu>
        <div style="padding:16px"><el-button @click="$router.push('/')" size="small">← 返回前台</el-button></div>
      </el-aside>
      <el-main><router-view /></el-main>
    </el-container>
  </div>
</template>
<script setup lang="ts">
import { useRoute } from "vue-router";
import { DataLine, User, Goods, List } from "@element-plus/icons-vue";
const route = useRoute();
</script>
<style scoped>
.admin-layout { min-height: 100vh; }
.sidebar { background: #304156; color: #fff; }
.logo { font-size: 20px; font-weight: 700; padding: 20px; text-align: center; }
.el-menu { border-right: none; }
</style>
