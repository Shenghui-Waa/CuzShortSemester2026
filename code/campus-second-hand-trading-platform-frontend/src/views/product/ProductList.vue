<template>
  <div class="page">
    <AppHeader />
    <div class="page-container">
      <SearchBar :categories="categories" @search="onSearch" />
      <div class="product-grid">
        <ProductCard v-for="p in products" :key="p.id" :product="p" />
      </div>
      <el-empty v-if="!loading && !products.length" description="没有找到商品" />
      <div v-if="loading" class="loading">加载中...</div>
      <Pagination :total="total" @change="onPageChange" />
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import SearchBar from "@/components/SearchBar.vue";
import ProductCard from "@/components/ProductCard.vue";
import Pagination from "@/components/Pagination.vue";
import { productApi } from "@/api/product";
import { categoryApi } from "@/api/index";

const products = ref<any[]>([]);
const categories = ref<any[]>([]);
const loading = ref(false);
const total = ref(0);
const page = ref(1);
const query = ref<any>({});

onMounted(async () => {
  const r: any = await categoryApi.getAll();
  categories.value = r.data || [];
  fetchList();
});

async function fetchList() {
  loading.value = true;
  try {
    const r: any = await productApi.list({ ...query.value, page: page.value, pageSize: 12 });
    products.value = r.data?.records || [];
    total.value = r.data?.total || 0;
  } catch {} finally {
    loading.value = false;
  }
}

function onSearch(q: any) { query.value = q; page.value = 1; fetchList(); }
function onPageChange(p: number) { page.value = p; fetchList(); }
</script>

<style scoped>
.loading { text-align: center; padding: 40px; color: #909399; }
</style>
