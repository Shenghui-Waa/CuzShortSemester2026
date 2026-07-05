<template>
  <div class="product-card" @click="$router.push(`/products/${product.id}`)">
    <div class="card-img">
      <img :src="product.images?.[0] || '/placeholder.png'" :alt="product.title" />
      <span v-if="product.status!==1" class="status-badge">{{ product.status===0?'待审核':product.status===2?'已售出':'已下架' }}</span>
    </div>
    <div class="card-body">
      <h4 class="title">{{ product.title }}</h4>
      <div class="price">{{ formatPrice(product.price) }}</div>
      <div class="meta">
        <span>{{ product.campus || "未知校区" }}</span>
        <span>{{ product.viewCount || 0 }}浏览</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { formatPrice } from "@/utils";
defineProps<{ product: any }>();
</script>

<style scoped>
.product-card { background: #fff; border-radius: 8px; overflow: hidden; cursor: pointer; transition: box-shadow .2s; }
.product-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,.12); }
.card-img { position: relative; height: 200px; overflow: hidden; background: #f0f0f0; }
.card-img img { width: 100%; height: 100%; object-fit: cover; }
.status-badge { position: absolute; top: 8px; right: 8px; background: rgba(0,0,0,.5); color: #fff; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
.card-body { padding: 12px; }
.title { font-size: 15px; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 8px; }
.price { font-size: 18px; font-weight: 700; color: #e6a23c; }
.meta { display: flex; justify-content: space-between; font-size: 12px; color: #909399; margin-top: 6px; }
</style>
