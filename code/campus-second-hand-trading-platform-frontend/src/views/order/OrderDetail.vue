<template>
  <div class="page">
    <AppHeader />
    <div class="page-container" style="max-width:800px">
      <h2>订单详情</h2>
      <el-descriptions v-if="order" :column="2" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getOrderStatusType(order.status)">{{ getOrderStatusLabel(order.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="买家">{{ order.buyerName }}</el-descriptions-item>
        <el-descriptions-item label="卖家">{{ order.sellerName }}</el-descriptions-item>
        <el-descriptions-item label="金额">{{ formatPrice(order.totalAmount) }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatDate(order.createdAt) }}</el-descriptions-item>
      </el-descriptions>
      <div class="order-actions" v-if="order" style="margin-top:16px;display:flex;gap:12px;flex-wrap:wrap">
        <el-button type="success" v-if="order.status===0 && isBuyer" @click="doPay">付款</el-button>
        <el-button type="primary" v-if="order.status===1 && isSeller" @click="doShip">发货</el-button>
        <el-button type="success" v-if="order.status===2 && isBuyer" @click="doConfirm">确认收货</el-button>
        <el-button type="danger" v-if="order.status<3 && isBuyer" @click="doCancel">取消订单</el-button>
        <el-button v-if="order.status===3" @click="showReview">评价</el-button>
      </div>
      <h3 style="margin-top:20px">商品明细</h3>
      <el-table :data="order?.items||[]" stripe>
        <el-table-column prop="productTitle" label="商品" />
        <el-table-column label="单价">
          <template #default="{ row }">{{ formatPrice(row.price) }}</template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="reviewVisible" title="评价交易" width="420px">
      <el-form label-width="60px">
        <el-form-item label="评分"><el-rate v-model="reviewForm.rating" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="reviewForm.content" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewVisible=false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交</el-button>
      </template>
    </el-dialog>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import { orderApi } from "@/api/order";
import { reviewApi } from "@/api/index";
import { useUserStore } from "@/stores/user";
import { formatPrice, formatDate, getOrderStatusLabel, getOrderStatusType } from "@/utils";

const route = useRoute();
const userStore = useUserStore();
const order = ref<any>(null);
const uid = computed(() => userStore.userInfo?.id || 0);
const isBuyer = computed(() => order.value?.buyerId === uid.value);
const isSeller = computed(() => order.value?.sellerId === uid.value);

const reviewVisible = ref(false);
const reviewForm = reactive({ rating: 3, content: "" });

onMounted(async () => {
  await userStore.ensureUserInfo();
  const r: any = await orderApi.detail(Number(route.params.id));
  order.value = r.data;
});

async function doPay() {
  await orderApi.pay(order.value.id);
  ElMessage.success("付款成功");
  order.value.status = 1;
}

async function doShip() {
  await orderApi.ship(order.value.id);
  ElMessage.success("已发货");
  order.value.status = 2;
}

async function doConfirm() {
  await orderApi.confirm(order.value.id);
  ElMessage.success("已确认收货");
  order.value.status = 3;
}

async function doCancel() {
  try {
    await ElMessageBox.confirm("确定取消订单？");
    await orderApi.cancel(order.value.id);
    ElMessage.success("已取消");
    order.value.status = 4;
  } catch {}
}

function showReview() {
  reviewForm.rating = 3; reviewForm.content = "";
  reviewVisible.value = true;
}

async function submitReview() {
  const o = order.value; if (!o) return;
  const targetId = isBuyer.value ? o.sellerId : o.buyerId;
  try {
    await reviewApi.create({ orderId: o.id, targetId, rating: reviewForm.rating, content: reviewForm.content });
    ElMessage.success("评价成功"); reviewVisible.value = false;
  } catch {}
}
</script>

<style scoped>
.order-actions { margin-top: 16px; display: flex; gap: 12px; flex-wrap: wrap; }
</style>
