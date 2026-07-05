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
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column label="商品" width="200">
          <template #default="{ row }">{{ row.items?.[0]?.productTitle }}</template>
        </el-table-column>
        <el-table-column label="金额" width="100"><template #default="{ row }">{{ formatPrice(row.totalAmount) }}</template></el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag :type="getOrderStatusType(row.status)">{{ getOrderStatusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="时间" width="180"><template #default="{ row }">{{ formatDate(row.createdAt) }}</template></el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/orders/${row.id}`)">详情</el-button>
            <el-button size="small" type="success" v-if="row.status===0" @click="doPay(row.id)">付款</el-button>
            <el-button size="small" type="primary" v-if="row.status===1" @click="doShip(row.id)">发货</el-button>
            <el-button size="small" type="success" v-if="row.status===2" @click="doConfirm(row.id)">确认收货</el-button>
            <el-button size="small" type="danger" v-if="row.status<3" @click="doCancel(row.id)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination :total="total" @change="onPageChange" />
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import Pagination from "@/components/Pagination.vue";
import { orderApi } from "@/api/order";
import { formatPrice, formatDate, getOrderStatusLabel, getOrderStatusType } from "@/utils";

const orders = ref<any[]>([]);
const total = ref(0);
const page = ref(1);
const statusFilter = ref("");

onMounted(() => fetchList());
async function fetchList() {
  const res: any = await orderApi.list({ status: statusFilter.value || undefined, page: page.value, pageSize: 10 });
  orders.value = res.data?.records || []; total.value = res.data?.total || 0;
}
function onPageChange(p: number) { page.value = p; fetchList(); }

async function doPay(id: number) { await orderApi.pay(id); ElMessage.success("付款成功"); fetchList(); }
async function doShip(id: number) { await orderApi.ship(id); ElMessage.success("已发货"); fetchList(); }
async function doConfirm(id: number) { await orderApi.confirm(id); ElMessage.success("已确认收货"); fetchList(); }
async function doCancel(id: number) {
  try { await ElMessageBox.confirm("确定取消订单？"); await orderApi.cancel(id); ElMessage.success("已取消"); fetchList(); } catch { }
}
</script>
