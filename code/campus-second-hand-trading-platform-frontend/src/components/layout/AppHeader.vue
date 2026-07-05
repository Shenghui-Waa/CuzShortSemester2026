<template>
  <el-header class="app-header">
    <div class="header-inner">
      <router-link to="/" class="logo">校园二手交易</router-link>
      <div class="nav-links">
        <router-link to="/products">商品浏览</router-link>
        <router-link to="/publish" v-if="user.token">发布商品</router-link>
      </div>
      <div class="user-area">
        <template v-if="user.token">
          <router-link to="/chat">消息</router-link>
          <router-link to="/favorites">收藏</router-link>
          <router-link to="/orders">订单</router-link>
          <el-dropdown>
            <span class="user-name">{{ user.userInfo?.nickname || "用户" }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/my-products')">我的发布</el-dropdown-item>
                <el-dropdown-item v-if="user.role==='1'" @click="$router.push('/admin')">后台管理</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login">登录</router-link>
          <router-link to="/register">注册</router-link>
        </template>
      </div>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { useRouter } from "vue-router";
const user = useUserStore();
const router = useRouter();
function handleLogout() { user.logout(); router.push("/"); }
</script>

<style scoped>
.app-header { background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,.08); position: sticky; top: 0; z-index: 100; height: 56px; }
.header-inner { max-width: 1200px; margin: 0 auto; display: flex; align-items: center; height: 100%; gap: 24px; }
.logo { font-size: 20px; font-weight: 700; color: #409eff; }
.nav-links { display: flex; gap: 16px; flex: 1; }
.nav-links a { color: #606266; font-size: 14px; }
.user-area { display: flex; align-items: center; gap: 16px; }
.user-area a { color: #606266; font-size: 14px; }
.user-name { cursor: pointer; color: #409eff; }
</style>
