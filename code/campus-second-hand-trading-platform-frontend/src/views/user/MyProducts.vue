<template>
  <div class="page"><AppHeader /><div class="page-container">
      <h2>我的发布</h2>
      <div class="grid"><ProductCard v-for="p in products" :key="p.id" :product="p" /></div>
      <el-empty v-if="!products.length" description="还没有发布商品" />
      <Pagination :total="total" @change="onPageChange" />
    </div><AppFooter /></div>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import ProductCard from "@/components/ProductCard.vue";
import Pagination from "@/components/Pagination.vue";
import { productApi } from "@/api/product";
const products = ref<any[]>([]); const total = ref(0); const page = ref(1);
onMounted(() => fetchList());
async function fetchList() { const r: any = await productApi.myList(page.value, 12); products.value = r.data?.records||[]; total.value = r.data?.total||0; }
function onPageChange(p: number) { page.value = p; fetchList(); }
</script>
<style scoped>
.grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
@media (max-width: 900px) { .grid { grid-template-columns: repeat(2, 1fr); } }
</style>
