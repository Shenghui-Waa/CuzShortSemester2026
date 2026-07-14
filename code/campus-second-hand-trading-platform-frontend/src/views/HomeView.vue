<template>
  <div class="page">
    <AppHeader />
    <div class="hero">
      <h1>校园二手交易平台</h1>
      <p>安全便捷的校内二手交易，让闲置物品找到新主人</p>
      <el-button type="primary" size="large" @click="$router.push('/products')">立即浏览</el-button>
    </div>
    <div class="page-container">
      <h2 class="st">最新发布</h2>
      <div class="grid">
        <ProductCard v-for="p in products" :key="p.id" :product="p" />
      </div>
      <div v-if="loading" class="loading"><el-icon class="is-loading"><Loading /></el-icon>加载中...</div>
    </div>
    <AppFooter />
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";
import { Loading } from "@element-plus/icons-vue";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import ProductCard from "@/components/ProductCard.vue";
import { productApi } from "@/api/product";

const products = ref<any[]>([]);
const loading = ref(false);
onMounted(async () => {
  loading.value = true;
  try { const r: any = await productApi.list({ page: 1, pageSize: 8 }); products.value = r.data?.records || []; } catch {}
  finally { loading.value = false; }
});
</script>
<style scoped>
.hero { text-align: center; padding: 80px 20px 60px; background: var(--hero-bg); }
.hero h1 { font-size: 36px; margin-bottom: 12px; color: var(--text-primary); }
.hero p { font-size: 16px; color: var(--text-secondary); margin-bottom: 24px; }
.st { font-size: 22px; font-weight: 600; margin-bottom: 20px; }
.grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.loading { text-align: center; padding: 40px; color: #909399; }
@media (max-width: 900px) { .grid { grid-template-columns: repeat(2, 1fr); } }
</style>

