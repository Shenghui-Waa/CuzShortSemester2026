<template>
  <div>
    <h2>Dashboard</h2>
    <el-row :gutter="20">
      <el-col :span="6"><el-card shadow="hover"><div class="stat blue">{{ d.userCount }}</div><div>用户总数</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><div class="stat green">{{ d.productCount }}</div><div>商品总数</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><div class="stat orange">{{ d.orderCount }}</div><div>订单总数</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><div class="stat red">{{ formatPrice(d.totalAmount) }}</div><div>交易总额</div></el-card></el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:24px">
      <el-col :span="12">
        <el-card shadow="hover">
          <h4 style="margin-bottom:16px">数据趋势</h4>
          <svg viewBox="0 0 500 180" width="100%" height="180">
            <line v-for="i in 4" :key="i" x1="50" :y1="20+i*35" x2="470" :y2="20+i*35" stroke="#f0f0f0" stroke-width="1" />
            <text v-for="i in 5" :key="i" x="44" :y="164-(i-1)*35" text-anchor="end" font-size="9" fill="#909399">{{ Math.round(maxVal*(i-1)/4) }}</text>
            <polyline :points="linePoints" fill="none" stroke="#409eff" stroke-width="2.5" stroke-linejoin="round" stroke-linecap="round" />
            <circle v-for="(pt, i) in points" :key="i" :cx="pt.x" :cy="pt.y" r="4" fill="#fff" stroke="#409eff" stroke-width="2.5" />
            <text v-for="(pt, i) in points" :key="'t'+i" :x="pt.x" :y="pt.y-13" text-anchor="middle" font-size="10" fill="#606266">{{ pt.val }}</text>
            <text v-for="(lb, i) in xLabels" :key="'x'+i" :x="80+i*98" y="178" text-anchor="middle" font-size="10" fill="#909399">{{ lb }}</text>
          </svg>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <h4 style="margin-bottom:16px">数据概览</h4>
          <div class="bar-row"><span class="bar-label">用户总数</span><div class="bar-track"><div class="bar-fill blue" :style="{width:barPercent(d.userCount,maxVal)+'%'}"></div></div><span class="bar-num">{{ d.userCount||0 }}</span></div>
          <div class="bar-row"><span class="bar-label">商品总数</span><div class="bar-track"><div class="bar-fill green" :style="{width:barPercent(d.productCount,maxVal)+'%'}"></div></div><span class="bar-num">{{ d.productCount||0 }}</span></div>
          <div class="bar-row"><span class="bar-label">订单总数</span><div class="bar-track"><div class="bar-fill orange" :style="{width:barPercent(d.orderCount,maxVal)+'%'}"></div></div><span class="bar-num">{{ d.orderCount||0 }}</span></div>
          <div class="bar-row"><span class="bar-label">今日新用户</span><div class="bar-track"><div class="bar-fill cyan" :style="{width:barPercent(d.todayNewUsers,maxVal)+'%'}"></div></div><span class="bar-num">{{ d.todayNewUsers||0 }}</span></div>
          <div class="bar-row"><span class="bar-label">今日新订单</span><div class="bar-track"><div class="bar-fill purple" :style="{width:barPercent(d.todayNewOrders,maxVal)+'%'}"></div></div><span class="bar-num">{{ d.todayNewOrders||0 }}</span></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:24px">
      <el-col :span="12">
        <el-card shadow="hover">
          <h4 style="margin-bottom:12px">快捷入口</h4>
          <div class="quick-links">
            <el-button @click="$router.push('/admin/users')" :icon="User">用户管理</el-button>
            <el-button @click="$router.push('/admin/products')" :icon="Goods">商品管理</el-button>
            <el-button @click="$router.push('/admin/orders')" :icon="List">订单管理</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <h4 style="margin-bottom:12px">平台概况</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="用户总数">{{ d.userCount }}</el-descriptions-item>
            <el-descriptions-item label="商品总数">{{ d.productCount }}</el-descriptions-item>
            <el-descriptions-item label="订单总数">{{ d.orderCount }}</el-descriptions-item>
            <el-descriptions-item label="交易总额">{{ formatPrice(d.totalAmount) }}</el-descriptions-item>
            <el-descriptions-item label="今日新用户">{{ d.todayNewUsers }}</el-descriptions-item>
            <el-descriptions-item label="今日新订单">{{ d.todayNewOrders }}</el-descriptions-item>
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

onMounted(async () => { const r: any = await adminApi.dashboard(); d.value = r.data || {}; });

const maxVal = computed(() => Math.max(
  ...[d.value.userCount,d.value.productCount,d.value.orderCount,d.value.todayNewUsers,d.value.todayNewOrders].filter(Boolean) as number[], 1));

const xLabels = ["用户总数","商品总数","订单总数","今日新用户","今日新订单"];

const points = computed(() => {
  const m = maxVal.value;
  const vals = [d.value.userCount||0, d.value.productCount||0, d.value.orderCount||0, d.value.todayNewUsers||0, d.value.todayNewOrders||0];
  const xs = [80, 178, 276, 374, 472];
  return vals.map((v, i) => ({ x: xs[i], y: 160 - Math.round((v/m)*140), val: v }));
});

const linePoints = computed(() => points.value.map(p => `${p.x},${p.y}`).join(" "));

function barPercent(v: number, max: number): number { return Math.round(((v||0)/max)*100); }
</script>

<style scoped>
.stat { font-size: 32px; font-weight: 700; }
.stat.blue { color: #409eff; } .stat.green { color: #67c23a; } .stat.orange { color: #e6a23c; } .stat.red { color: #f56c6c; }
.bar-row { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.bar-label { width: 80px; font-size: 12px; color: #606266; text-align: right; flex-shrink: 0; }
.bar-track { flex: 1; height: 16px; background: #f0f0f0; border-radius: 8px; overflow: hidden; }
.bar-fill { height: 100%; border-radius: 8px; transition: width .6s; }
.bar-fill.blue { background: #409eff; } .bar-fill.green { background: #67c23a; }
.bar-fill.orange { background: #e6a23c; } .bar-fill.cyan { background: #00bcd4; } .bar-fill.purple { background: #9c27b0; }
.bar-num { width: 40px; font-size: 12px; font-weight: 600; color: #303133; text-align: left; }
.quick-links { display: flex; gap: 12px; flex-wrap: wrap; }
html.dark .bar-num { color: var(--text-primary); }
</style>
