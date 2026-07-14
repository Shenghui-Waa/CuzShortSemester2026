<template>
  <div class="product-card" @click="$router.push(`/products/${product.id}`)">
    <div class="card-img"><img :src="product.images?.[0] || '/placeholder.png'" /></div>
    <div class="card-body">
      <h4>{{ product.title }}</h4>
      <div class="price">{{ formatPrice(product.price) }}</div>
      <div class="meta"><span>{{ product.campus || "未知校区" }}</span><span>{{ product.viewCount || 0 }}浏览</span></div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { formatPrice } from "@/utils";
defineProps<{ product: any }>();
</script>
<style scoped>
.product-card { background: var(--bg-card); border-radius: 8px; overflow: hidden; cursor: pointer; transition: box-shadow .2s, background .3s; }
.product-card:hover { box-shadow: var(--shadow-hover); }
.card-img { height: 200px; overflow: hidden; background: var(--border-light); }
.card-img img { width: 100%; height: 100%; object-fit: cover; }
.card-body { padding: 12px; }
.card-body h4 { font-size: 15px; color: var(--text-primary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 8px; }
.price { font-size: 18px; font-weight: 700; color: #e6a23c; }
.meta { display: flex; justify-content: space-between; font-size: 12px; color: var(--text-muted); margin-top: 6px; }
</style>
