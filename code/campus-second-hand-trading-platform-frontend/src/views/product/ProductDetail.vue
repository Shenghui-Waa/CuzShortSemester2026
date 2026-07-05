<template>
  <div class="page">
    <AppHeader />
    <div class="page-container" v-if="product">
      <div class="detail-grid">
        <div class="gallery">
          <img :src="product.images?.[0] || '/placeholder.png'" :alt="product.title" class="main-img" />
        </div>
        <div class="info">
          <h1>{{ product.title }}</h1>
          <div class="price">{{ formatPrice(product.price) }}
            <span class="original" v-if="product.originalPrice">{{ formatPrice(product.originalPrice) }}</span>
          </div>
          <div class="meta">
            <span>{{ getConditionLabel(product.condition) }}</span>
            <span>{{ product.campus }}</span>
            <span>{{ product.viewCount }}次浏览</span>
          </div>
          <div class="seller">卖家：{{ product.sellerName }}</div>
          <div class="actions">
            <el-button type="primary" size="large" @click="buyNow">立即购买</el-button>
            <el-button size="large" @click="addCart">加入购物车</el-button>
            <el-button size="large" :type="product.isFavorited?'warning':''" @click="toggleFav">
              {{ product.isFavorited ? "已收藏" : "收藏" }}
            </el-button>
          </div>
        </div>
      </div>
      <div class="desc-section">
        <h3>商品描述</h3>
        <p>{{ product.description || "暂无描述" }}</p>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import { productApi } from "@/api/product";
import { orderApi } from "@/api/order";
import { cartApi } from "@/api/cart";
import { favoriteApi } from "@/api/favorite";
import { formatPrice, getConditionLabel } from "@/utils";

const route = useRoute();
const router = useRouter();
const product = ref<any>(null);

onMounted(async () => {
  const res: any = await productApi.detail(Number(route.params.id));
  product.value = res.data;
});

async function buyNow() {
  try {
    await ElMessageBox.confirm("确认购买此商品？", "确认", { confirmButtonText: "确认", cancelButtonText: "取消" });
    await orderApi.create({ productId: product.value.id });
    ElMessage.success("下单成功");
    router.push("/orders");
  } catch { }
}
async function addCart() { await cartApi.add(product.value.id); ElMessage.success("已加入购物车"); }
async function toggleFav() {
  if (product.value.isFavorited) { await favoriteApi.remove(product.value.id); product.value.isFavorited = false; }
  else { await favoriteApi.add(product.value.id); product.value.isFavorited = true; }
}
</script>

<style scoped>
.detail-grid { display: flex; gap: 32px; flex-wrap: wrap; }
.gallery { flex: 0 0 450px; }
.main-img { width: 100%; border-radius: 8px; max-height: 400px; object-fit: cover; }
.info { flex: 1; }
.info h1 { font-size: 22px; margin-bottom: 16px; }
.price { font-size: 28px; color: #e6a23c; font-weight: 700; margin-bottom: 12px; }
.original { font-size: 16px; color: #909399; text-decoration: line-through; margin-left: 8px; }
.meta { display: flex; gap: 16px; color: #909399; font-size: 14px; margin-bottom: 12px; }
.seller { font-size: 14px; color: #606266; margin-bottom: 20px; }
.actions { display: flex; gap: 12px; }
.desc-section { margin-top: 32px; padding-top: 20px; border-top: 1px solid #ebeef5; }
.desc-section h3 { margin-bottom: 12px; }
.desc-section p { color: #606266; line-height: 1.8; white-space: pre-wrap; }
</style>
