<template>
  <div class="page"><AppHeader /><div class="page-container" style="max-width:800px">
      <h2>订单详情</h2>
      <el-descriptions v-if="order" :column="2" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="getOrderStatusType(order.status)">{{ getOrderStatusLabel(order.status) }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="买家">{{ order.buyerName }}</el-descriptions-item>
        <el-descriptions-item label="卖家">{{ order.sellerName }}</el-descriptions-item>
        <el-descriptions-item label="金额">{{ formatPrice(order.totalAmount) }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatDate(order.createdAt) }}</el-descriptions-item>
      </el-descriptions>
      <h3 style="margin-top:20px">商品明细</h3>
      <el-table :data="order?.items||[]" stripe>
        <el-table-column prop="productTitle" label="商品" />
        <el-table-column label="单价"><template #default="{ row }">{{ formatPrice(row.price) }}</template></el-table-column>
      </el-table>
    </div><AppFooter /></div>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import { orderApi } from "@/api/order";
import { formatPrice, formatDate, getOrderStatusLabel, getOrderStatusType } from "@/utils";
const route = useRoute();
const order = ref<any>(null);
onMounted(async () => { const r: any = await orderApi.detail(Number(route.params.id)); order.value = r.data; });
</script>
