<template>
  <div class="page">
    <AppHeader />
    <div class="page-container">
      <SearchBar :categories="categories" @search="onSearch" />
      <div class="sort-row">
        <el-radio-group v-model="sortBy" size="small" @change="fetchList">
          <el-radio-button value="created_at">最新</el-radio-button>
          <el-radio-button value="price">价格↑</el-radio-button>
        </el-radio-group>
      </div>
      <div class="grid">
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
const sortBy = ref("created_at");
const query = ref<any>({});
onMounted(async () => { const r: any = await categoryApi.getAll(); categories.value = r.data || []; fetchList(); });
async function fetchList() {
  loading.value = true;
  try {
    const r: any = await productApi.list({ ...query.value, sortBy: sortBy.value, page: page.value, pageSize: 12 });
    products.value = r.data?.records || []; total.value = r.data?.total || 0;
  } catch {} finally { loading.value = false; }
}
function onSearch(q: any) { query.value = q; page.value = 1; fetchList(); }
function onPageChange(p: number) { page.value = p; fetchList(); }
</script>
<style scoped>
.sort-row { margin: 0 0 16px; }
.grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.loading { text-align: center; padding: 40px; color: #909399; }
@media (max-width: 900px) { .grid { grid-template-columns: repeat(2, 1fr); } }
</style>
