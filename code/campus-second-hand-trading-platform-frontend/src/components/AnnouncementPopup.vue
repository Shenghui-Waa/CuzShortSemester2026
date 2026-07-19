<template>
  <el-dialog
    v-model="visible"
    :title="announcement?.title || ''"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    destroy-on-close
    class="announcement-dialog"
  >
    <div class="announcement-content">
      <p class="announcement-body">{{ announcement?.content || "" }}</p>
      <p class="announcement-time" v-if="announcement?.createdAt">
        {{ new Date(announcement.createdAt).toLocaleString() }}
      </p>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-checkbox v-model="dontShowToday" label="不再提示今天" />
        <el-button type="primary" @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { announcementApi } from "@/api";

interface Announcement {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

const visible = ref(false);
const announcement = ref<Announcement | null>(null);
const dontShowToday = ref(false);

const STORAGE_KEY = "announcement_last_shown_date";

function getToday(): string {
  return new Date().toDateString();
}

async function fetchAndShow() {
  try {
    const res: any = await announcementApi.list(1, 1);
    if (res?.code === 200 && res?.data?.records?.length > 0) {
      announcement.value = res.data.records[0];
      visible.value = true;
    }
  } catch {
    // 静默失败，不影响用户体验
  }
}

function handleClose() {
  if (dontShowToday.value) {
    localStorage.setItem(STORAGE_KEY, getToday());
  }
  visible.value = false;
}

onMounted(() => {
  const lastShown = localStorage.getItem(STORAGE_KEY);
  if (lastShown === getToday()) return;
  fetchAndShow();
});
</script>

<style scoped>
.announcement-content {
  padding: 8px 0;
}

.announcement-body {
  white-space: pre-wrap;
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.7;
  margin: 0;
}

.announcement-time {
  color: var(--text-muted);
  font-size: 12px;
  margin-top: 16px;
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
