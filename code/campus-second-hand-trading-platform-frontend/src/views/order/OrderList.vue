<template>
  <div class="page">
    <AppHeader />
    <div class="page-container">
      <h2>我的订单</h2>
      <el-tabs v-model="statusFilter" @tab-change="fetchList">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="待付款" name="0" />
        <el-tab-pane label="待发货" name="1" />
        <el-tab-pane label="待收货" name="2" />
        <el-tab-pane label="已完成" name="3" />
      </el-tabs>
      <el-table :data="orders" stripe>
        <el-table-column prop="orderNo" label="订单号" width="190" />
        <el-table-column label="商品" min-width="200">
          <template #default="{ row }">{{ row.items?.[0]?.productTitle || "—" }}</template>
        </el-table-column>
        <el-table-column label="角色" width="70">
          <template #default="{ row }"><el-tag size="small" :type="isBuyer(row)?'':'warning'">{{ isBuyer(row)?"买家":"卖家" }}</el-tag></template>
        </el-table-column>
        <el-table-column label="金额" width="110">
          <template #default="{ row }">{{ formatPrice(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }"><el-tag :type="getOrderStatusType(row.status)">{{ getOrderStatusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/orders/${row.id}`)">详情</el-button>
            <!-- 买家操作 -->
            <el-button size="small" type="success" v-if="row.status===0&&isBuyer(row)" @click="doPay(row.id)">付款</el-button>
            <el-button size="small" type="success" v-if="row.status===2&&isBuyer(row)" @click="doConfirm(row.id)">确认收货</el-button>
            <el-button size="small" type="danger" v-if="row.status<3&&isBuyer(row)" @click="doCancel(row.id)">取消</el-button>
            <!-- 卖家操作 -->
            <el-button size="small" type="primary" v-if="row.status===1&&isSeller(row)" @click="doShip(row.id)">发货</el-button>
            <!-- 评价按钮（双方都可评价已完成订单） -->
            <el-button size="small" v-if="row.status===3" @click="showReview(row)">评价</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination :total="total" @change="onPageChange" />
    </div>
    <!-- 评价弹窗 -->
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
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import Pagination from "@/components/Pagination.vue";
import { orderApi } from "@/api/order";
import { reviewApi } from "@/api/index";
import { useUserStore } from "@/stores/user";
import { formatPrice, formatDate, getOrderStatusLabel, getOrderStatusType } from "@/utils";

const userStore = useUserStore();
const orders = ref<any[]>([]);
const total = ref(0);
const page = ref(1);
const statusFilter = ref("");
const uid = ref<number>(userStore.userInfo?.id || 0);

const reviewVisible = ref(false);
const reviewingOrder = ref<any>(null);
const reviewForm = reactive({ rating: 3, content: "" });

onMounted(() => fetchList());

function isBuyer(o: any) { return o.buyerId === uid.value; }
function isSeller(o: any) { return o.sellerId === uid.value; }

async function fetchList() {
  const r: any = await orderApi.list({ status: statusFilter.value || undefined, page: page.value, pageSize: 10 });
  orders.value = r.data?.records || []; total.value = r.data?.total || 0;
}
function onPageChange(p: number) { page.value = p; fetchList(); }

async function doPay(id: number) { await orderApi.pay(id); ElMessage.success("付款成功"); fetchList(); }
async function doShip(id: number) { await orderApi.ship(id); ElMessage.success("已发货"); fetchList(); }
async function doConfirm(id: number) { await orderApi.confirm(id); ElMessage.success("已确认收货"); fetchList(); }
async function doCancel(id: number) {
  try { await ElMessageBox.confirm("确定取消订单？"); await orderApi.cancel(id); ElMessage.success("已取消"); fetchList(); } catch {}
}

function showReview(order: any) {
  reviewingOrder.value = order;
  reviewForm.rating = 3; reviewForm.content = "";
  reviewVisible.value = true;
}
async function submitReview() {
  const o = reviewingOrder.value; if (!o) return;
  const targetId = isBuyer(o) ? o.sellerId : o.buyerId;
  try {
    await reviewApi.create({ orderId: o.id, targetId, rating: reviewForm.rating, content: reviewForm.content });
    ElMessage.success("评价成功"); reviewVisible.value = false;
  } catch {}
}
</script>
