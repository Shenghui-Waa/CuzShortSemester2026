<template>
  <div class="page">
    <AppHeader />
    <div class="page-container" v-if="product">
      <div class="back-row"><el-button class="back-btn" @click="$router.back()" text>< 返回</el-button></div>
      <div class="detail-grid">
        <div class="gallery">
          <el-image v-if="product.images?.length" :src="product.images[activeImg]" :preview-src-list="product.images" class="main-img" fit="cover" :initial-index="activeImg" />
          <div v-else class="main-img img-placeholder">
            <el-icon :size="64"><component :is="catIcon" /></el-icon>
          </div>
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
          <div class="actions" v-if="isSeller">
            <el-button type="primary" size="large" v-if="product.status===0||product.status===1" @click="openEdit">编辑</el-button>
            <el-button type="danger" size="large" v-if="product.status===1" @click="delist">下架</el-button>
            <el-button size="large" v-if="product.status===0" @click="withdraw">撤回审核申请</el-button>
            <el-button type="primary" size="large" v-if="product.status===2||product.status===3" @click="relist">再次上架</el-button>
          </div>
          <div class="actions" v-else-if="user.isLogin()">
            <el-button type="primary" size="large" v-if="product.status!==2" @click="buyNow">立即购买</el-button>
            <el-button type="info" size="large" v-else disabled>售罄</el-button>
            <el-button size="large" :type="inCart?'warning':''" @click="toggleCart">
              {{ inCart?'已加入购物车':'加入购物车' }}
            </el-button>
            <el-button size="large" :type="product.isFavorited?'warning':''" @click="toggleFav">
              {{ product.isFavorited?'已收藏':'收藏' }}
            </el-button>
            <el-button size="large" @click="openChat">联系卖家</el-button>
          </div>
          <div class="actions" v-else>
            <el-button type="primary" size="large" @click="$router.push('/login')">登录后购买</el-button>
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

    <el-dialog v-model="editVisible" title="编辑" width="600px" :close-on-click-modal="false">
      <el-form :model="ef" ref="efRef" label-width="80px">
        <el-form-item label="标题" required><el-input v-model="ef.title" /></el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="ef.categoryId" style="width:100%">
            <el-option v-for="c in editCategories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="售价" required><el-input-number v-model="ef.price" :min="0" :precision="2" style="width:200px" /></el-form-item>
        <el-form-item label="原价"><el-input-number v-model="ef.originalPrice" :min="0" :precision="2" style="width:200px" /></el-form-item>
        <el-form-item label="成色">
          <el-radio-group v-model="ef.condition">
            <el-radio :value="1">全新</el-radio>
            <el-radio :value="2">几乎全新</el-radio>
            <el-radio :value="3">有使用痕迹</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="校区">
          <el-select v-model="ef.campus" style="width:100%">
            <el-option label="下沙校区" value="下沙校区" />
            <el-option label="桐乡校区" value="桐乡校区" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="ef.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="图片"><ImageUpload v-model="editImages" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible=false">取消</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="submitEdit">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import { productApi } from "@/api/product";
import { orderApi } from "@/api/order";
import { cartApi, categoryApi, favoriteApi, reviewApi } from "@/api/index";
import { useUserStore } from "@/stores/user";
import { formatPrice, formatDate, getConditionLabel } from "@/utils";
import request from "@/api/request";
import { getCategoryIcon } from "@/utils/icons";
import ImageUpload from "@/components/ImageUpload.vue";

const route = useRoute();
const router = useRouter();
const user = useUserStore();
const product = ref<any>(null);
const catIcon = computed(() => getCategoryIcon(product.value?.categoryName));
const isSeller = computed(() => user.isLogin() && product.value && user.userInfo?.id === product.value.userId);
const inCart = ref(false);
const reviews = ref<any[]>([]);
const activeImg = ref(0);

onMounted(async () => {
  await user.ensureUserInfo();
  const r: any = await productApi.detail(Number(route.params.id));
  product.value = r.data;
  if (user.isLogin() && product.value?.id) {
    try {
      const cartRes: any = await cartApi.list();
      const items = cartRes.data || [];
      inCart.value = items.some((i: any) => i.productId === product.value.id);
    } catch {}
  }
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

async function toggleCart() {
  if (inCart.value) {
    await cartApi.remove(product.value.id);
    inCart.value = false;
    ElMessage.success("已移出购物车");
  } else {
    await cartApi.add(product.value.id);
    inCart.value = true;
    ElMessage.success("已加入购物车");
  }
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
  router.push({ path: "/chat", query: { contactId: product.value.userId, contactName: product.value.sellerName } });
}

async function delist() {
  try {
    await ElMessageBox.confirm("确认下架此商品？", "确认");
    await productApi.updateStatus(product.value.id, 3);
    product.value.status = 3;
    ElMessage.success("已下架");
  } catch {}
}

async function withdraw() {
  try {
    await ElMessageBox.confirm("确认撤回审核申请？", "确认");
    await request.delete(`/products/${product.value.id}/del`);
    ElMessage.success("已撤回");
    router.push("/my-products");
  } catch {}
}

function relist() {
  const p = product.value;
  router.push({
    path: "/publish",
    query: {
      id: p.id, title: p.title, price: p.price,
      categoryId: p.categoryId, condition: p.condition,
      campus: p.campus, description: p.description,
      originalPrice: p.originalPrice,
    }
  });
}

const editVisible = ref(false);
const editSubmitting = ref(false);
const efRef = ref();
const editImages = ref<string[]>([]);
const editCategories = ref<any[]>([]);
const ef = reactive({
  title: "", categoryId: null as number | null, price: 0,
  originalPrice: 0, condition: 1, campus: "", description: "",
});

async function openEdit() {
  await fetchCategories();
  const p = product.value;
  ef.title = p.title || "";
  ef.categoryId = p.categoryId || null;
  ef.price = p.price || 0;
  ef.originalPrice = p.originalPrice || 0;
  ef.condition = p.condition ?? 1;
  ef.campus = p.campus || "";
  ef.description = p.description || "";
  editImages.value = p.images ? [...p.images] : [];
  editVisible.value = true;
}

async function fetchCategories() {
  if (editCategories.value.length) return;
  try { const r: any = await categoryApi.getAll(); editCategories.value = r.data || []; } catch {}
}

async function submitEdit() {
  editSubmitting.value = true;
  try {
    const data = {
      title: ef.title, categoryId: ef.categoryId, price: ef.price,
      originalPrice: ef.originalPrice, condition: ef.condition,
      campus: ef.campus, description: ef.description, status: 0,
    };
    await productApi.update(product.value.id, data, editImages.value);
    ElMessage.success("修改成功，已提交审核");
    editVisible.value = false;
    product.value.status = 0;
  } catch {} finally {
    editSubmitting.value = false;
  }
}
</script>

<style scoped>
.detail-grid { display: flex; gap: 32px; flex-wrap: wrap; }
.gallery { flex: 0 1 450px; min-width: 0; max-width: 100%; }
.main-img { width: 100%; border-radius: 8px; max-height: 400px; object-fit: cover; }
.img-placeholder { width: 100%; height: 300px; display: flex; align-items: center; justify-content: center; background: var(--border-light); color: var(--text-muted); }
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
@media (max-width: 768px) { .detail-grid { flex-direction: column; } .gallery { flex: none; width: 100%; max-width: 100%; } .info h1 { font-size: 18px; } .price { font-size: 22px; } .actions { gap: 8px; } .actions .el-button { flex: 1; min-width: 0; } }
.back-row { margin-bottom: 12px; }
.back-btn { cursor: pointer; color: var(--text-secondary); font-size: 14px; transition: color .2s; }
.back-btn:hover { color: var(--el-color-primary); }
</style>
