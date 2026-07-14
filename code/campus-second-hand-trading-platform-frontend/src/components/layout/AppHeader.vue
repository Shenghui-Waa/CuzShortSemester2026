<template>
  <el-header class="app-header">
    <div class="inner">
      <router-link to="/" class="logo">校园二手交易</router-link>
      <nav><router-link to="/products">商品浏览</router-link>
        <router-link to="/publish" v-if="user.isLogin()">发布商品</router-link>
        <router-link to="/cart" v-if="user.isLogin()">购物车</router-link>
      </nav>
      <div class="right">
        <template v-if="user.isLogin()">
          <router-link to="/chat">消息</router-link>
          <router-link to="/favorites">收藏</router-link>
          <router-link to="/orders">订单</router-link>
          <el-dropdown trigger="click">
            <span class="uname">{{ user.userInfo?.nickname || "用户" }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/my-products')">我的发布</el-dropdown-item>
                <el-dropdown-item v-if="user.isAdmin()" @click="$router.push('/admin')">后台管理</el-dropdown-item>
                <el-dropdown-item divided @click="doLogout">退出登录</el-dropdown-item>
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
import { authApi } from "@/api/auth";
const user = useUserStore();
const router = useRouter();
async function doLogout() { try { await authApi.logout(); } catch {} user.logout(); router.push("/"); }
</script>
<style scoped>
.app-header { background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,.08); position: sticky; top: 0; z-index: 100; height: 56px; }
.inner { max-width: 1200px; margin: 0 auto; display: flex; align-items: center; height: 100%; gap: 24px; }
.logo { font-size: 20px; font-weight: 700; color: #409eff; }
nav { display: flex; gap: 16px; flex: 1; }
nav a, .right a { color: #606266; font-size: 14px; text-decoration: none; }
nav a:hover, .right a:hover, .router-link-active { color: #409eff !important; }
.right { display: flex; align-items: center; gap: 16px; }
.uname { cursor: pointer; color: #409eff; font-size: 14px; }
</style>
