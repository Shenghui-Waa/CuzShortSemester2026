<template>
  <div>
    <div class="header-row">
      <h2>公告管理</h2>
      <el-button type="primary" @click="openDialog()">新增公告</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe row-key="id">
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="更新时间" width="170">
        <template #default="{ row }">{{ formatTime(row.updatedAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <Pagination v-if="total > 0" :total="total" :page="page" :page-size="pageSize" @change="onPageChange" />

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑公告' : '新增公告'" width="600px" :close-on-click-modal="false">
      <el-form :model="form" label-width="60px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="doSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { announcementApi } from "@/api";
import Pagination from "@/components/Pagination.vue";

const list = ref<any[]>([]);
const loading = ref(false);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);

const dialogVisible = ref(false);
const editId = ref<number | null>(null);
const form = reactive({ title: "", content: "" });
const submitting = ref(false);

function formatTime(t: string) {
  if (!t) return "";
  const d = new Date(t);
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
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

function openDialog(row?: any) {
  if (row?.id) {
    editId.value = row.id;
    form.title = row.title;
    form.content = row.content;
  } else {
    editId.value = null;
    form.title = "";
    form.content = "";
  }
  dialogVisible.value = true;
}

async function doSubmit() {
  if (!form.title.trim() || !form.content.trim()) {
    ElMessage.warning("标题和内容不能为空");
    return;
  }
  submitting.value = true;
  try {
    const res: any = editId.value
      ? await announcementApi.update(editId.value, { title: form.title, content: form.content })
      : await announcementApi.create({ title: form.title, content: form.content });
    if (res.code === 200) {
      ElMessage.success(editId.value ? "编辑成功" : "新增成功");
      dialogVisible.value = false;
      fetchList();
    }
  } catch {} finally { submitting.value = false; }
}

async function doDelete(id: number) {
  try {
    await ElMessageBox.confirm("确定删除该公告？", "删除确认", { type: "warning" });
    const res: any = await announcementApi.delete(id);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      fetchList();
    }
  } catch {}
}

onMounted(fetchList);
</script>

<style scoped>
.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.header-row h2 { margin: 0; }
</style>