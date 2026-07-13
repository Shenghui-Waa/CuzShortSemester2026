<template>
  <div>
    <h2>商品管理</h2>
    <el-input v-model="keyword" placeholder="搜索商品" style="width:240px;margin-bottom:16px" clearable @change="fetch" />
    <el-table :data="products" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="sellerName" label="卖家" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status===1?'success':row.status===0?'warning':'info'">
            {{ row.status===0?'待审核':row.status===1?'在售':row.status===2?'已售出':'已下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" type="success" v-if="row.status===0" @click="audit(row.id,1)">通过</el-button>
          <el-button size="small" type="danger" v-if="row.status===0" @click="audit(row.id,3)">驳回</el-button>
          <el-button size="small" type="danger" v-if="row.status===1" @click="audit(row.id,3)">下架</el-button>
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

const products = ref<any[]>([]);
const total = ref(0);
const keyword = ref("");
const page = ref(1);

onMounted(() => fetch());
async function fetch() {
  const res: any = await adminApi.productList({ page: page.value, pageSize: 10, keyword: keyword.value });
  products.value = res.data?.records || []; total.value = res.data?.total || 0;
}
function onPageChange(p: number) { page.value = p; fetch(); }
async function audit(id: number, status: number) {
  await adminApi.updateProductStatus(id, status);
  ElMessage.success("操作成功");
  fetch();
}
</script>
