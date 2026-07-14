<template>
  <div class="page">
    <AppHeader />
    <div class="page-container" v-if="product">
      <div class="detail-grid">
        <div class="gallery">
          <img :src="product.images?.[activeImg] || '/placeholder.png'" class="main-img" />
          <div class="thumbs" v-if="product.images?.length > 1">
            <img v-for="(u,i) in product.images" :key="i" :src="u"
              :class="{act:i===activeImg}" @click="activeImg=Number(i)" />
          </div>
        </div>
        <div class="info">
          <h1>{{ product.title }}</h1>
          <div class="price">{{ formatPrice(product.price) }}
            <span class="orig" v-if="product.originalPrice">{{ formatPrice(product.originalPrice) }}</span>
          </div>
          <div class="meta">
            <el-tag size="small">{{ getConditionLabel(product.condition) }}</el-tag>
            <span>{{ product.campus }}</span>
            <span>{{ product.viewCount }}次浏览</span>
          </div>
          <div class="seller">卖家：{{ product.sellerName }}</div>
          <div class="actions">
            <el-button type="primary" size="large" @click="buyNow">立即购买</el-button>
            <el-button size="large" @click="addCart">加入购物车</el-button>
            <el-button size="large" :type="product.isFavorited?'warning':''" @click="toggleFav">
              {{ product.isFavorited?'已收藏':'收藏' }}
            </el-button>
            <el-button size="large" @click="openChat" v-if="user.isLogin()">联系卖家</el-button>
          </div>
        </div>
      </div>
      <div class="section">
        <h3>商品描述</h3>
        <p>{{ product.description || "暂无描述" }}</p>
      </div>
      <div class="section">
        <h3>买家评价</h3>
        <div v-if="reviews.length" class="review-list">
          <div v-for="r in reviews" :key="r.id" class="r-item">
            <div><strong>{{ r.reviewerName }}</strong>
              <el-rate :model-value="r.rating" disabled show-score size="small" />
            </div>
            <p>{{ r.content || "无文字评价" }}</p>
            <span class="r-date">{{ formatDate(r.createdAt) }}</span>
          </div>
        </div>
        <p v-else class="nr">暂无评价</p>
      </div>
    </div>
    <div v-else class="loading-state">加载中...</div>
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
import { cartApi, favoriteApi, reviewApi } from "@/api/index";
import { useUserStore } from "@/stores/user";
import { formatPrice, formatDate, getConditionLabel } from "@/utils";

const route = useRoute();
const router = useRouter();
const user = useUserStore();
const product = ref<any>(null);
const reviews = ref<any[]>([]);
const activeImg = ref(0);

onMounted(async () => {
  await user.ensureUserInfo();
  const r: any = await productApi.detail(Number(route.params.id));
  product.value = r.data;
  if (product.value && product.value.userId) {
    try {
      const rr: any = await reviewApi.getUserReviews(product.value.userId, 1, 10);
      reviews.value = rr.data?.records || [];
    } catch {}
  }
});

async function buyNow() {
  if (!user.isLogin()) { router.push("/login"); return; }
  try {
    await ElMessageBox.confirm("确认购买此商品？", "确认");
    await orderApi.create({ productId: product.value.id });
    ElMessage.success("下单成功");
    router.push("/orders");
  } catch {}
}

async function addCart() {
  if (!user.isLogin()) { router.push("/login"); return; }
  await cartApi.add(product.value.id);
  ElMessage.success("已加入购物车");
}

async function toggleFav() {
  if (!user.isLogin()) { router.push("/login"); return; }
  if (product.value.isFavorited) {
    await favoriteApi.remove(product.value.id);
    product.value.isFavorited = false;
    ElMessage.success("已取消收藏");
  } else {
    await favoriteApi.add(product.value.id);
    product.value.isFavorited = true;
    ElMessage.success("已收藏");
  }
}

function openChat() {
  router.push("/chat");
  ElMessage.info("请通过消息页面联系卖家");
}
</script>

<style scoped>
.detail-grid { display: flex; gap: 32px; flex-wrap: wrap; }
.gallery { flex: 0 0 450px; }
.main-img { width: 100%; border-radius: 8px; max-height: 400px; object-fit: cover; }
.thumbs { display: flex; gap: 8px; margin-top: 8px; }
.thumbs img { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; cursor: pointer; border: 2px solid transparent; }
.thumbs img.act { border-color: #fb1e47; }
.info { flex: 1; }
.info h1 { font-size: 22px; margin-bottom: 16px; }
.price { font-size: 28px; color: #e6a23c; font-weight: 700; margin-bottom: 12px; }
.orig { font-size: 16px; color: var(--text-muted); text-decoration: line-through; margin-left: 8px; }
.meta { display: flex; gap: 12px; align-items: center; color: var(--text-muted); font-size: 14px; margin-bottom: 16px; }
.seller { font-size: 15px; color: var(--text-primary); margin-bottom: 20px; }
.actions { display: flex; gap: 12px; flex-wrap: wrap; }
.section { margin-top: 32px; padding-top: 20px; border-top: 1px solid var(--border-color); }
.section h3 { margin-bottom: 12px; }
.section p { color: #606266; line-height: 1.8; white-space: pre-wrap; }
.r-item { padding: 8px 0; border-bottom: 1px solid var(--border-light); }
.r-item p { font-size: 14px; margin: 4px 0; }
.r-date { font-size: 12px; color: #c0c4cc; }
.nr, .loading-state { color: var(--text-muted); text-align: center; padding: 60px; }
@media (max-width: 768px) { .gallery { flex: 0 0 100%; } }
</style>
