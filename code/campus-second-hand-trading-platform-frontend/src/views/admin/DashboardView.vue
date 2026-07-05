<template>
  <div>
    <h2>仪表盘</h2>
    <el-row :gutter="20">
      <el-col :span="6"><el-card><div class="stat">{{ dash.userCount }}</div><div>用户总数</div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat">{{ dash.productCount }}</div><div>商品总数</div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat">{{ dash.orderCount }}</div><div>订单总数</div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat">{{ formatPrice(dash.totalAmount) }}</div><div>交易总额</div></el-card></el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12"><el-card><div>今日新增用户: {{ dash.todayNewUsers }}</div></el-card></el-col>
      <el-col :span="12"><el-card><div>今日新增订单: {{ dash.todayNewOrders }}</div></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { adminApi } from "@/api/review";
import { formatPrice } from "@/utils";

const dash = ref<any>({});
onMounted(async () => { const res: any = await adminApi.dashboard(); dash.value = res.data || {}; });
</script>

<style scoped>
.stat { font-size: 28px; font-weight: 700; color: #409eff; }
</style>
