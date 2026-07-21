<template>
  <div class="admin-layout" :class="{ collapsed, 'mobile-open': mobileSidebarVisible }">
    <el-container class="admin-container">
      <!-- 移动端遮罩 -->
      <div class="sidebar-overlay" :class="{ show: mobileSidebarVisible }" @click="mobileSidebarVisible = false"></div>
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
        <div class="menu-wrapper" ref="menuWrapper">
          <div class="theme-row">
            <el-button class="theme-toggle-btn" @click="theme.toggle()" text>
              <el-icon :size="18">
                <Moon v-if="!theme.isDark" />
                <Sunny v-else />
              </el-icon>
              <span class="theme-label">{{ theme.isDark ? '浅色模式' : '深色模式' }}</span>
            </el-button>
          </div>
          <el-menu
            :default-active="route.path"
            router
            :collapse="collapsed"
            class="side-menu"
            background-color="transparent"
            text-color="var(--sidebar-text)"
            active-text-color="#fb1e47"
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
            <el-menu-item index="/admin/categories">
              <el-icon><Menu /></el-icon>
              <span>分类管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/announcements">
              <el-icon><Bell /></el-icon>
              <span>公告管理</span>
            </el-menu-item>

          </el-menu>
          <div class="menu-indicator" :style="menuIndicatorStyle"></div>
        </div>
        <div class="sidebar-bottom">
          <el-button class="back-btn" @click="$router.push('/')" text>
            <el-icon :size="18"><Back /></el-icon>
            <span v-show="!collapsed">返回前台</span>
          </el-button>
        </div>
      </el-aside>
      <el-main class="admin-main">
        <!-- 移动端侧边栏切换按钮 -->
        <el-button class="mobile-sidebar-toggle" @click="mobileSidebarVisible = !mobileSidebarVisible" text>
          <el-icon :size="20"><Menu /></el-icon>
        </el-button>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from "vue";
import { useRoute } from "vue-router";
import { DataLine, User, Goods, List, Menu, Bell, Expand, Fold, Back, Moon, Sunny } from "@element-plus/icons-vue";
import { useThemeStore } from "@/stores/theme";
const route = useRoute();
const theme = useThemeStore();
const collapsed = ref(false);
const mobileSidebarVisible = ref(false);

// 移动端点击菜单项后自动关闭侧边栏
watch(() => route.path, () => { mobileSidebarVisible.value = false; });

const menuWrapper = ref();
const menuIndicatorStyle = ref({ top: "0px", height: "0px", opacity: "0" });

function moveToItem(el: HTMLElement) {
  const wrapper = menuWrapper.value as HTMLElement;
  const itemRect = el.getBoundingClientRect();
  const wrapRect = wrapper.getBoundingClientRect();
  menuIndicatorStyle.value = {
    top: (itemRect.top - wrapRect.top) + "px",
    height: itemRect.height + "px",
    opacity: "1",
  };
}

function moveToActive() {
  const wrapper = menuWrapper.value as HTMLElement;
  if (!wrapper) return;
  const active = wrapper.querySelector(".el-menu-item.is-active") as HTMLElement;
  if (active) moveToItem(active);
}

function setupMenuIndicator() {
  nextTick(() => moveToActive());
}

onMounted(() => { nextTick(() => setupMenuIndicator()); });
watch(() => route.path, () => nextTick(() => moveToActive()));
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

.menu-wrapper {
  position: relative; flex: 1; display: flex; flex-direction: column;
  overflow: hidden;
}
.side-menu {
  border-right: none !important;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}
.menu-indicator {
  position: absolute; left: 4px; right: 4px;
  background: rgba(251, 30, 71, 0.15);
  border-radius: 6px;
  transition: top 0.4s cubic-bezier(0.4, 0, 0.2, 1), height 0.4s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.25s;
  pointer-events: none; z-index: 0;
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
  color: #fb1e47 !important;
  background: transparent !important;
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
  justify-content: flex-start;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 8px;
  color: var(--sidebar-text) !important;
  font-size: 14px;
  transition: all .2s;
  white-space: nowrap;
}
.back-btn:hover {
  background: rgba(251,30,71,.15) !important;
  color: #fb1e47 !important;
}

.theme-row {
  padding: 4px 8px;
  flex-shrink: 0;
}
.theme-toggle-btn {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  height: 42px;
  border-radius: 8px;
  color: var(--sidebar-text) !important;
  font-size: 14px;
  white-space: nowrap;
  justify-content: flex-start;
  transition: width 0.3s, height 0.3s, border-radius 0.3s, padding 0.3s, background .2s, color .2s;
}
.theme-toggle-btn:hover {
  background: rgba(255,255,255,.08) !important;
  color: #ffd04b !important;
}
.theme-label {
  flex: 1;
  text-align: left;
}

.admin-layout.collapsed .theme-row {
  padding: 4px 0;
  display: flex;
  justify-content: center;
}
.admin-layout.collapsed .theme-toggle-btn {
  width: 42px;
  min-width: 42px;
  height: 42px;
  padding: 0;
  border-radius: 50%;
  justify-content: center;
}
.admin-layout.collapsed .theme-label {
  display: none;
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

html.dark .side-menu :deep(.el-menu-item) { color: #a0aec0; }
html.dark .side-menu :deep(.el-menu-item:hover) { background: rgba(255,255,255,.06) !important; color: #e0e0e0 !important; }
html.dark .side-menu :deep(.el-menu-item.is-active) { color: #cd2846 !important; background: transparent !important; }
html.dark .menu-indicator { background: rgba(205, 40, 70, 0.2); }
html.dark .back-btn:hover { background: rgba(205,40,70,.2) !important; color: #cd2846 !important; }

.el-menu--collapse { width: 64px; }
.el-menu--collapse .el-menu-item { padding: 0 !important; justify-content: center; }

/* ======== 移动端响应式 ======== */
.mobile-sidebar-toggle { display: none; }
.sidebar-overlay { display: none; }

@media (max-width: 768px) {
  .mobile-sidebar-toggle {
    display: inline-flex; position: fixed; top: 8px; left: 8px; z-index: 200;
    color: var(--text-secondary); background: var(--bg-card);
    border-radius: 8px; box-shadow: var(--shadow); width: 40px; height: 40px;
    justify-content: center; align-items: center;
  }

  .sidebar {
    position: fixed; top: 0; left: -220px; height: 100vh; z-index: 300;
    width: 220px !important; transition: left .3s ease;
    overflow-y: auto;
  }

  .sidebar-overlay {
    display: block; position: fixed; inset: 0; z-index: 250;
    background: rgba(0,0,0,.4); opacity: 0; pointer-events: none;
    transition: opacity .3s ease;
  }
  .sidebar-overlay.show { opacity: 1; pointer-events: auto; }

  .admin-layout.mobile-open .sidebar { left: 0; }

  .admin-main { padding: 16px; padding-top: 56px; }
  .admin-container { position: relative; }
  .collapse-btn, .logo-text, .theme-label, .back-btn span { /* 保持可见 */ }
}
</style>
