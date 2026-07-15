<template>
  <div>
    <div class="header-row"><h2>商品管理</h2></div>
    <el-input v-model="keyword" placeholder="搜索商品" style="width:240px;margin-bottom:16px" clearable @change="fetch" />
    <el-table :data="products" stripe>
      <el-table-column prop="id" label="ID" width="80" /><el-table-column prop="title" label="标题" />
      <el-table-column prop="sellerName" label="卖家" width="100" />
      <el-table-column label="状态" width="90"><template #default="{ row }"><el-tag :type="getProductStatusType(row.status)">{{ getProductStatusLabel(row.status) }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="210">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row.id)">详情</el-button>
          <el-button size="small" type="success" v-if="row.status===0" @click="audit(row.id,1)">通过</el-button>
          <el-button size="small" type="danger" v-if="row.status===0" @click="audit(row.id,3)">驳回</el-button>
          <el-button size="small" type="danger" v-if="row.status===1" @click="audit(row.id,3)">下架</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" @change="onPage" />

    <el-dialog v-model="detailVisible" title="商品详情" width="560px" :close-on-click-modal="false">
      <div v-if="detail" class="detail-body">
        <div class="detail-imgs" v-if="detail.images?.length">
          <img v-for="(u,i) in detail.images" :key="i" :src="u" />
        </div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="标题" :span="2">{{ detail.title }}</el-descriptions-item>
          <el-descriptions-item label="售价">{{ formatPrice(detail.price) }}</el-descriptions-item>
          <el-descriptions-item label="原价">{{ formatPrice(detail.originalPrice) }}</el-descriptions-item>
          <el-descriptions-item label="卖家">{{ detail.sellerName }}</el-descriptions-item>
          <el-descriptions-item label="校区">{{ detail.campus }}</el-descriptions-item>
          <el-descriptions-item label="成色">{{ getConditionLabel(detail.condition) }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ getProductStatusLabel(detail.status) }}</el-descriptions-item>
          <el-descriptions-item label="浏览">{{ detail.viewCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ detail.description || "无" }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div v-else class="detail-loading">加载中...</div>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import Pagination from "@/components/Pagination.vue";
import { adminApi } from "@/api/index";
import { productApi } from "@/api/product";
import { formatPrice } from "@/utils";
import { getProductStatusLabel, getProductStatusType, getConditionLabel } from "@/utils";
const products = ref<any[]>([]); const total = ref(0); const keyword = ref(""); const page = ref(1);
onMounted(() => fetch());
async function fetch() { const r: any = await adminApi.productList({ page: page.value, pageSize: 10, keyword: keyword.value }); products.value = r.data?.records||[]; total.value = r.data?.total||0; }
function onPage(p: number) { page.value = p; fetch(); }
async function audit(id: number, status: number) { await adminApi.updateProductStatus(id, status); ElMessage.success("操作成功"); fetch(); }

const detailVisible = ref(false);
const detail = ref<any>(null);
async function showDetail(id: number) {
  detail.value = null;
  detailVisible.value = true;
  try {
    const r: any = await productApi.detail(id);
    detail.value = r.data;
  } catch {}
}
</script>

<style scoped>
.header-row {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 16px;
}
.header-row h2 { margin: 0; }
.detail-imgs { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 16px; }
.detail-imgs img { width: 120px; height: 120px; object-fit: cover; border-radius: 6px; }
.detail-loading { text-align: center; padding: 40px; color: var(--text-muted); }
html.dark .detail-loading { color: #e0e0e0; }
</style>
