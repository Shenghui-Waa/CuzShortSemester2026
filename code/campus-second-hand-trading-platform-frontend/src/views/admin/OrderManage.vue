<template>
  <div>
    <h2>订单管理</h2>
    <el-tabs v-model="statusFilter" @tab-change="fetch">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane label="待发货" name="1" />
      <el-tab-pane label="待收货" name="2" />
      <el-tab-pane label="已完成" name="3" />
    </el-tabs>
    <el-table :data="orders" stripe>
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="buyerName" label="买家" width="100" />
      <el-table-column prop="sellerName" label="卖家" width="100" />
      <el-table-column label="金额" width="100"><template #default="{ row }">{{ formatPrice(row.totalAmount) }}</template></el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }"><el-tag :type="getOrderStatusType(row.status)">{{ getOrderStatusLabel(row.status) }}</el-tag></template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" @change="onPageChange" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import Pagination from "@/components/Pagination.vue";
import { adminApi } from "@/api/review";
import { formatPrice, getOrderStatusLabel, getOrderStatusType } from "@/utils";

const orders = ref<any[]>([]);
const total = ref(0);
const page = ref(1);
const statusFilter = ref("");

onMounted(() => fetch());
async function fetch() {
  const res: any = await adminApi.orderList({ page: page.value, pageSize: 10, status: statusFilter.value || undefined });
  orders.value = res.data?.records || []; total.value = res.data?.total || 0;
}
function onPageChange(p: number) { page.value = p; fetch(); }
</script>
