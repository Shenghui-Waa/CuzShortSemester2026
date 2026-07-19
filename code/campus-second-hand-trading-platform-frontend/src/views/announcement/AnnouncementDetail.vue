<template>
  <div class="page"><AppHeader /><div class="page-container">
    <router-link to="/announcements" class="back-link">
      <el-icon><ArrowLeft /></el-icon>
      <span>返回公告列表</span>
    </router-link>

    <div class="detail-card" v-loading="loading">
      <template v-if="detail">
        <h1 class="detail-title">{{ detail.title }}</h1>
        <div class="detail-meta">
          <span>发布于 {{ formatTime(detail.createdAt) }}</span>
          <span v-if="detail.updatedAt !== detail.createdAt">更新于 {{ formatTime(detail.updatedAt) }}</span>
        </div>
        <div class="detail-divider"></div>
        <div class="detail-content" style="white-space: pre-wrap">{{ detail.content }}</div>
      </template>
      <div v-else-if="!loading" class="empty">公告不存在</div>
    </div>
  </div><AppFooter /></div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ArrowLeft } from "@element-plus/icons-vue";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import { announcementApi } from "@/api";

const route = useRoute();
const detail = ref<any>(null);
const loading = ref(false);

function formatTime(t: string) {
  if (!t) return "";
  const d = new Date(t);
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

async function fetchDetail() {
  loading.value = true;
  try {
    const id = Number(route.params.id);
    const res: any = await announcementApi.detail(id);
    if (res.code === 200 && res.data) {
      detail.value = res.data;
    }
  } catch {} finally { loading.value = false; }
}

onMounted(fetchDetail);
</script>

<style scoped>
.back-link {
  display: inline-flex; align-items: center; gap: 6px;
  font-size: 14px; color: var(--text-secondary); text-decoration: none;
  margin-bottom: 16px; transition: color .2s;
}
.back-link:hover { color: #fb1e47; }
.detail-card {
  max-width: 800px; margin: 0 auto;
  background: #fc6984; border-radius: 12px; padding: 32px;
}
.detail-title { font-size: 22px; font-weight: 700; color: #fff; margin: 0 0 12px; line-height: 1.4; }
.detail-meta { font-size: 13px; color: rgba(255,255,255,.75); display: flex; gap: 16px; }
.detail-divider { height: 1px; background: rgba(255,255,255,.2); margin: 20px 0; }
.detail-content { font-size: 15px; color: rgba(255,255,255,.9); line-height: 1.8; word-break: break-word; }
.empty { text-align: center; color: var(--text-secondary); padding: 60px 0; font-size: 15px; }
html.dark .detail-card { background: #be2b46; }
</style>