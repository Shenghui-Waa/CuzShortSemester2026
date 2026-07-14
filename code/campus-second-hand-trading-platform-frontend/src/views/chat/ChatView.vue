<template>
  <div class="page"><AppHeader /><div class="page-container" style="text-align:center;padding:80px">
      <el-icon :size="60" color="#c0c4cc"><ChatDotRound /></el-icon>
      <h2 style="margin:16px 0;color:#909399">聊天功能</h2>
      <p style="color:#c0c4cc;margin-bottom:24px">实时聊天功能正在建设中，敬请期待</p>
      <el-button type="primary" @click="$router.push('/products')">先去逛逛</el-button>
    </div><AppFooter /></div>
</template>
<script setup lang="ts">
import { ChatDotRound } from "@element-plus/icons-vue";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
</script>
