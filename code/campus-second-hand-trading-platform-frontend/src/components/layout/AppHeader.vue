<template>
  <el-header class="app-header">
    <div class="inner">
      <!-- 移动端汉堡按钮 -->
      <el-button class="menu-toggle" @click="mobileMenuVisible = !mobileMenuVisible" text>
        <el-icon :size="22"><Menu /></el-icon>
      </el-button>
      <router-link to="/" class="logo">校园二手交易</router-link>
      <nav>
        <router-link to="/products" class="nav-btn">商品浏览</router-link>
        <router-link to="/publish" v-if="user.isLogin()" class="nav-btn">发布商品</router-link>
        <router-link to="/cart" v-if="user.isLogin()" class="nav-btn">购物车</router-link>
      </nav>
      <div class="right">
        <template v-if="user.isLogin()">
          <router-link to="/announcements" class="nav-btn">公告</router-link>
          <router-link to="/chat" class="nav-btn">消息</router-link>
          <router-link to="/favorites" class="nav-btn">收藏</router-link>
          <router-link to="/orders" class="nav-btn">订单</router-link>
          <el-dropdown trigger="click">
            <span class="user-btn">{{ user.userInfo?.nickname || "用户" }}</span>
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
          <router-link to="/login" class="nav-btn">登录</router-link>
          <router-link to="/register" class="nav-btn primary">注册</router-link>
        </template>
        <el-button @click="theme.toggle()" text circle size="small" class="theme-btn">
          <el-icon :size="18">
            <Moon v-if="!theme.isDark" />
            <Sunny v-else />
          </el-icon>
        </el-button>
      </div>
    </div>

    <!-- 移动端下拉菜单 -->
    <div class="mobile-menu" :class="{ open: mobileMenuVisible }">
      <router-link to="/products" class="m-nav-btn" @click="mobileMenuVisible = false">商品浏览</router-link>
      <router-link v-if="user.isLogin()" to="/publish" class="m-nav-btn" @click="mobileMenuVisible = false">发布商品</router-link>
      <router-link v-if="user.isLogin()" to="/cart" class="m-nav-btn" @click="mobileMenuVisible = false">购物车</router-link>
      <router-link v-if="user.isLogin()" to="/announcements" class="m-nav-btn" @click="mobileMenuVisible = false">公告</router-link>
      <router-link v-if="user.isLogin()" to="/chat" class="m-nav-btn" @click="mobileMenuVisible = false">消息</router-link>
      <router-link v-if="user.isLogin()" to="/favorites" class="m-nav-btn" @click="mobileMenuVisible = false">收藏</router-link>
      <router-link v-if="user.isLogin()" to="/orders" class="m-nav-btn" @click="mobileMenuVisible = false">订单</router-link>
      <router-link v-if="user.isLogin()" to="/profile" class="m-nav-btn" @click="mobileMenuVisible = false">个人中心</router-link>
      <router-link v-if="user.isLogin()" to="/my-products" class="m-nav-btn" @click="mobileMenuVisible = false">我的发布</router-link>
      <router-link v-if="user.isLogin() && user.isAdmin()" to="/admin" class="m-nav-btn" @click="mobileMenuVisible = false">后台管理</router-link>
      <a v-if="user.isLogin()" class="m-nav-btn" @click="doLogout(); mobileMenuVisible = false">退出登录</a>
      <template v-if="!user.isLogin()">
        <router-link to="/login" class="m-nav-btn" @click="mobileMenuVisible = false">登录</router-link>
        <router-link to="/register" class="m-nav-btn m-primary" @click="mobileMenuVisible = false">注册</router-link>
      </template>
      <a class="m-nav-btn" @click="theme.toggle()">{{ theme.isDark ? '切换浅色模式' : '切换深色模式' }}</a>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { Moon, Sunny } from "@element-plus/icons-vue";
import { Menu } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/user";
import { useThemeStore } from "@/stores/theme";
import { useRouter } from "vue-router";
import { authApi } from "@/api/auth";

import { ref } from "vue";
const mobileMenuVisible = ref(false);
const user = useUserStore();
const theme = useThemeStore();
const router = useRouter();
async function doLogout() {
  try { await authApi.logout(); } catch {}
  user.logout(); router.push("/");
}
</script>

<style scoped>
.app-header {
  background: var(--bg-header);
  box-shadow: 0 1px 4px rgba(0,0,0,.08);
  position: sticky; top: 0; z-index: 100;
  height: 56px;
  transition: background .3s;
}
.inner {
  max-width: 1200px; margin: 0 auto;
  display: flex; align-items: center; height: 100%; gap: 16px;
}
.logo {
  font-size: 20px; font-weight: 700; color: #fb1e47; flex-shrink: 0;
}
nav { display: flex; gap: 6px; flex: 1; }

.nav-btn {
  display: inline-flex; align-items: center;
  padding: 6px 14px; border-radius: 8px;
  font-size: 14px; color: #737373; text-decoration: none;
  transition: all .2s; white-space: nowrap;
}
.nav-btn:hover { background: #fec7d1; color: #fb1e47; }
.nav-btn.router-link-active { background: #fec7d1; color: #fb1e47; font-weight: 600; }
.nav-btn.primary { background: #fb1e47; color: #fff; }
.nav-btn.primary:hover { background: #fc5675; }

.right { display: flex; align-items: center; gap: 6px; }

.user-btn {
  display: inline-flex; align-items: center;
  padding: 6px 14px; border-radius: 8px;
  cursor: pointer; font-size: 14px; color: #737373; transition: all .2s;
}
.user-btn:hover { background: #fec7d1; color: #fb1e47; }

.theme-btn { color: #a2a2a2; margin-left: 4px; }

/* 暗色 — 三阶: #723b45(底) / #a03146(中) / #cd2846(主) */
html.dark .nav-btn { color: #a2a2a2; }
html.dark .nav-btn:hover { background: #723b45; color: #fff; }
html.dark .nav-btn.router-link-active { background: #a03146; color: #fff; font-weight: 600; }
html.dark .nav-btn.primary { background: #cd2846; color: #fff; }
html.dark .nav-btn.primary:hover { background: #fb1e47; }
html.dark .user-btn { color: #a2a2a2; }
html.dark .user-btn:hover { background: #723b45; color: #fff; }
html.dark .logo { color: #cd2846; }
html.dark .theme-btn { color: #737373; }

/* ======== 移动端响应式 ======== */
.menu-toggle { display: none; }
.mobile-menu { display: none; }

@media (max-width: 768px) {
  .menu-toggle { display: flex; flex-shrink: 0; color: var(--text-secondary); }
  .menu-toggle:hover { color: #fb1e47; }
  nav { display: none; }
  .right { display: none; }
  .inner { gap: 8px; }
  .logo { font-size: 17px; }

  .mobile-menu {
    display: flex; flex-direction: column;
    position: absolute; top: 56px; left: 0; right: 0;
    background: var(--bg-header);
    box-shadow: 0 4px 12px rgba(0,0,0,.12);
    padding: 8px 16px 16px;
    max-height: 0; overflow: hidden; padding-top: 0; padding-bottom: 0;
    transition: max-height .3s ease, padding .3s ease;
  }
  .mobile-menu.open { max-height: 500px; padding: 8px 16px 16px; }

  .m-nav-btn {
    display: block; padding: 12px 8px;
    font-size: 15px; color: var(--text-primary); text-decoration: none;
    border-radius: 6px; transition: background .2s;
    cursor: pointer;
  }
  .m-nav-btn:hover { background: rgba(251,30,71,.08); color: #fb1e47; }
  .m-nav-btn.router-link-active { color: #fb1e47; font-weight: 600; }
  .m-primary { color: #fb1e47 !important; font-weight: 600; }
}

html.dark .m-nav-btn { color: #e0e0e0; }
html.dark .m-nav-btn:hover { background: rgba(205,40,70,.2); color: #cd2846; }
html.dark .m-nav-btn.router-link-active { color: #cd2846; }
html.dark .m-primary { color: #cd2846 !important; }
html.dark .menu-toggle:hover { color: #cd2846; }
</style>
