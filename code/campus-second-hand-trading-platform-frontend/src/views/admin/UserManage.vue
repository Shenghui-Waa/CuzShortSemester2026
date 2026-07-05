<template>
  <div>
    <h2>用户管理</h2>
    <el-input v-model="keyword" placeholder="搜索用户名/昵称" style="width:240px;margin-bottom:16px" clearable @change="fetch" />
    <el-table :data="users" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="school" label="学校" />
      <el-table-column label="状态" width="80"><template #default="{ row }"><el-tag :type="row.status?'danger':'success'">{{ row.status?'封禁':'正常' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" :type="row.status?'success':'danger'" @click="toggleStatus(row)">{{ row.status?'解封':'封禁' }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" @change="onPageChange" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import Pagination from "@/components/Pagination.vue";
import { adminApi } from "@/api/review";

const users = ref<any[]>([]);
const total = ref(0);
const keyword = ref("");
const page = ref(1);

onMounted(() => fetch());
async function fetch() {
  const res: any = await adminApi.userList({ page: page.value, pageSize: 10, keyword: keyword.value });
  users.value = res.data?.records || []; total.value = res.data?.total || 0;
}
function onPageChange(p: number) { page.value = p; fetch(); }
async function toggleStatus(row: any) {
  await adminApi.updateUserStatus(row.id, row.status ? 0 : 1);
  ElMessage.success("操作成功");
  fetch();
}
</script>
