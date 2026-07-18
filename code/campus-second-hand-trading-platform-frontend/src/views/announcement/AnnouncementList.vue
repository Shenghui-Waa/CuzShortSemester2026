<template>
  <div class="page"><AppHeader /><div class="page-container">
    <h2>系统公告</h2>

    <div class="announcement-list" v-loading="loading">
      <div v-if="list.length === 0 && !loading" class="empty">暂无公告</div>
      <div
        v-for="item in list"
        :key="item.id"
        class="announcement-card"
        @click="openDetail(item)"
      >
        <div class="card-title">{{ item.title }}</div>
        <div class="card-meta">
          <span>{{ formatTime(item.createdAt) }}</span>
          <span v-if="item.updatedAt !== item.createdAt" class="edited">已编辑</span>
        </div>
        <div class="card-preview">{{ item.content?.slice(0, 120) }}{{ item.content?.length > 120 ? '...' : '' }}</div>
      </div>
    </div>

    <Pagination v-if="total > 0" :total="total" :page="page" :page-size="pageSize" @change="onPageChange" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" :close-on-click-modal="false">
      <div class="detail-body">
        <div class="detail-meta">
          <span>发布于 {{ formatTime(dialogCreatedAt) }}</span>
          <span v-if="dialogUpdatedAt !== dialogCreatedAt">更新于 {{ formatTime(dialogUpdatedAt) }}</span>
        </div>
        <div class="detail-divider"></div>
        <div class="detail-content" v-html="dialogContent"></div>
      </div>
    </el-dialog>
  </div><AppFooter /></div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import Pagination from "@/components/Pagination.vue";
import { announcementApi } from "@/api";

const list = ref<any[]>([]);
const loading = ref(false);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);

const dialogVisible = ref(false);
const dialogTitle = ref("");
const dialogContent = ref("");
const dialogCreatedAt = ref("");
const dialogUpdatedAt = ref("");

function formatTime(t: string) {
  if (!t) return "";
  const d = new Date(t);
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

function openDetail(item: any) {
  dialogTitle.value = item.title;
  dialogContent.value = (item.content || "").replace(/\n/g, "<br>");
  dialogCreatedAt.value = item.createdAt;
  dialogUpdatedAt.value = item.updatedAt;
  dialogVisible.value = true;
}

async function fetchList() {
  loading.value = true;
  try {
    const res: any = await announcementApi.list(page.value, pageSize.value);
    if (res.code === 200 && res.data) {
      list.value = res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch {} finally { loading.value = false; }
}

function onPageChange(p: number, ps: number) {
  page.value = p; pageSize.value = ps; fetchList();
}

onMounted(fetchList);
</script>

<style scoped>
.announcement-list { max-width: 800px; margin: 0 auto; }
.announcement-card {
  background: #fd8fa3;
  border-radius: 10px;
  padding: 20px 24px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all .2s;
}
.announcement-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,.12); }
.card-title { font-size: 16px; font-weight: 600; color: #fff; margin-bottom: 8px; }
.card-meta { font-size: 13px; color: rgba(255,255,255,.75); margin-bottom: 10px; display: flex; gap: 12px; }
.edited { color: #fff7c4; }
.card-preview { font-size: 14px; color: rgba(255,255,255,.85); line-height: 1.6; }
.empty { text-align: center; color: var(--text-secondary); padding: 60px 0; font-size: 15px; }

.detail-body { background: #fc6984; border-radius: 10px; padding: 24px; }
.detail-meta { font-size: 13px; color: rgba(255,255,255,.75); display: flex; gap: 16px; margin-bottom: 16px; }
.detail-divider { height: 1px; background: rgba(255,255,255,.2); margin-bottom: 16px; }
.detail-content { font-size: 15px; color: rgba(255,255,255,.9); line-height: 1.8; word-break: break-word; }

html.dark .announcement-card { background: #a03146; }
html.dark .detail-body { background: #be2b46; }
</style>