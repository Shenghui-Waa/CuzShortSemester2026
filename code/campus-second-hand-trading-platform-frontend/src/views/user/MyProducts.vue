<template>
  <div class="page"><AppHeader /><div class="page-container">
      <h2>我的发布</h2>
      <el-tabs v-model="statusFilter" @tab-change="onTabChange" style="margin-bottom:16px">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="审核中" name="0" />
        <el-tab-pane label="正常" name="1" />
        <el-tab-pane label="售罄" name="2" />
        <el-tab-pane label="下架" name="3" />
      </el-tabs>
      <div class="grid">
        <div v-for="p in filteredProducts" :key="p.id" class="card-wrap">
          <ProductCard :product="p" />
          <el-tag class="status-tag" :type="statusType(p.status)" size="small">{{ statusLabel(p.status) }}</el-tag>
        </div>
      </div>
      <el-empty v-if="!filteredProducts.length" description="还没有发布商品" />
    </div><AppFooter /></div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import ProductCard from "@/components/ProductCard.vue";
import { productApi } from "@/api/product";
const products = ref<any[]>([]);
const statusFilter = ref("");
onMounted(() => fetchList());
async function fetchList() { const r: any = await productApi.myList(1, 100); products.value = r.data?.records||[]; }
function onTabChange() {}
const filteredProducts = computed(() => {
  if (!statusFilter.value) return products.value;
  return products.value.filter((p: any) => String(p.status) === statusFilter.value);
});
function statusLabel(s: number): string {
  return ({ 0: "审核", 1: "正常", 2: "售罄", 3: "下架" } as any)[s] ?? "未知";
}
function statusType(s: number): "warning" | "success" | "info" | "danger" {
  return ({ 0: "warning", 1: "success", 2: "info", 3: "danger" } as any)[s] ?? "info";
}
</script>
<style scoped>
.grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.card-wrap { position: relative; }
.status-tag { position: absolute; top: 8px; right: 8px; z-index: 2; }
@media (max-width: 900px) { .grid { grid-template-columns: repeat(2, 1fr); } }
</style>
