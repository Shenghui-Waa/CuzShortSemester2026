<template>
  <div class="dashboard">
    <h2 style="margin-bottom:20px">Dashboard</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="16">
      <el-col :span="4" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover">
          <div class="stat" :style="{ color: card.color }">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 柱状对比 + 快捷入口 -->
    <el-row :gutter="20" style="margin-top:24px">
      <el-col :span="14">
        <el-card shadow="hover">
          <h4 style="margin-bottom:16px">数据对比</h4>
          <div class="bar-chart">
            <div class="bar-item" v-for="item in barData" :key="item.label">
              <div class="bar-label">{{ item.label }}</div>
              <div class="bar-track">
                <div class="bar-fill" :style="{ width: item.pct + '%', background: item.color }"></div>
              </div>
              <div class="bar-val">{{ item.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <h4 style="margin-bottom:16px">快捷入口</h4>
          <div style="display:flex;flex-direction:column;gap:10px">
            <el-button @click="$router.push('/admin/users')" :icon="User" style="justify-content:flex-start">用户管理</el-button>
            <el-button @click="$router.push('/admin/products')" :icon="Goods" style="justify-content:flex-start">商品管理</el-button>
            <el-button @click="$router.push('/admin/orders')" :icon="List" style="justify-content:flex-start">订单管理</el-button>
          </div>
          <el-divider />
          <el-descriptions :column="1" size="small">
            <el-descriptions-item label="在售商品">{{ d.productCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="待处理订单">{{ d.orderCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="今日新用户">{{ d.todayNewUsers || 0 }}</el-descriptions-item>
            <el-descriptions-item label="今日新订单">{{ d.todayNewOrders || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { User, Goods, List } from "@element-plus/icons-vue";
import { adminApi } from "@/api/index";
import { formatPrice } from "@/utils";

const d = ref<any>({});

onMounted(async () => {
  const r: any = await adminApi.dashboard();
  d.value = r.data || {};
});

const statCards = computed(() => [
  { label: "用户总数", value: d.value.userCount || 0, color: "#409eff" },
  { label: "商品总数", value: d.value.productCount || 0, color: "#67c23a" },
  { label: "订单总数", value: d.value.orderCount || 0, color: "#e6a23c" },
  { label: "交易总额", value: formatPrice(d.value.totalAmount), color: "#f56c6c" },
  { label: "今日新用户", value: d.value.todayNewUsers || 0, color: "#00bcd4" },
  { label: "今日新订单", value: d.value.todayNewOrders || 0, color: "#9c27b0" },
]);

const barData = computed(() => {
  const items = [
    { label: "用户总数", value: d.value.userCount || 0, color: "#409eff" },
    { label: "商品总数", value: d.value.productCount || 0, color: "#67c23a" },
    { label: "订单总数", value: d.value.orderCount || 0, color: "#e6a23c" },
    { label: "今日新用户", value: d.value.todayNewUsers || 0, color: "#00bcd4" },
    { label: "今日新订单", value: d.value.todayNewOrders || 0, color: "#9c27b0" },
  ];
  const maxV = Math.max(...items.map(i => i.value), 1);
  return items.map(i => ({ ...i, pct: Math.round((i.value / maxV) * 100) }));
});
</script>

<style scoped>
.stat { font-size: 28px; font-weight: 700; }
.stat-label { font-size: 12px; color: var(--text-muted); margin-top: 4px; }

.bar-chart { padding: 4px 0; }
.bar-item { display: flex; align-items: center; gap: 12px; margin-bottom: 18px; }
.bar-item:last-child { margin-bottom: 0; }
.bar-label { width: 80px; font-size: 13px; color: var(--text-secondary); text-align: right; flex-shrink: 0; }
.bar-track { flex: 1; height: 24px; background: var(--border-light); border-radius: 12px; overflow: hidden; }
.bar-fill { height: 100%; border-radius: 12px; transition: width .8s cubic-bezier(.4,0,.2,1); min-width: 4px; }
.bar-val { width: 52px; font-size: 14px; font-weight: 600; color: var(--text-primary); flex-shrink: 0; text-align: right; }
</style>
