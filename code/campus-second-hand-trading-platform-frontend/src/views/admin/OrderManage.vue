<template>
  <div>
    <div class="header-row"><h2>订单管理</h2></div>
    <div class="tabs-wrapper">
      <el-tabs v-model="sf" @tab-change="fetch" ref="tabsRef">
        <el-tab-pane label="全部" name="" /><el-tab-pane label="待付款" name="0" />
        <el-tab-pane label="待发货" name="1" /><el-tab-pane label="待收货" name="2" /><el-tab-pane label="已完成" name="3" />
      </el-tabs>
      <div class="tab-indicator" :style="tabIndicatorStyle"></div>
    </div>
    <div class="table-wrapper">
      <el-table :data="orders" stripe ref="tableRef">
        <el-table-column prop="orderNo" label="订单号" width="190" />
        <el-table-column prop="buyerName" label="买家" width="100" />
        <el-table-column prop="sellerName" label="卖家" width="100" />
        <el-table-column label="金额" width="110"><template #default="{row}">{{ formatPrice(row.totalAmount) }}</template></el-table-column>
        <el-table-column label="状态" width="90"><template #default="{row}"><el-tag :type="getOrderStatusType(row.status)">{{ getOrderStatusLabel(row.status) }}</el-tag></template></el-table-column>
        <el-table-column label="时间" width="170"><template #default="{row}">{{ formatDate(row.createdAt) }}</template></el-table-column>
      </el-table>
      <div class="header-indicator" :style="indicatorStyle"></div>
    </div>
    <Pagination :total="total" @change="onPage" />
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, nextTick } from "vue";
import Pagination from "@/components/Pagination.vue";
import { adminApi } from "@/api/index";
import { formatPrice, formatDate, getOrderStatusLabel, getOrderStatusType } from "@/utils";
const orders = ref<any[]>([]); const total = ref(0); const page = ref(1); const sf = ref("");
onMounted(() => { fetch(); nextTick(() => { setupHeaderIndicator(); setupTabIndicator(); }); });
async function fetch() { const r: any = await adminApi.orderList({ page: page.value, pageSize: 10, status: sf.value||undefined }); orders.value = r.data?.records||[]; total.value = r.data?.total||0; }
function onPage(p: number) { page.value = p; fetch(); }

// 表头滑动指示器
const tableRef = ref();
const indicatorStyle = ref({ left: "0px", width: "0px", opacity: "0" });

function setupHeaderIndicator() {
  const el = tableRef.value?.$el;
  if (!el) return;
  const gridEl = el.closest(".table-wrapper") as HTMLElement;
  const hdrWrapper = el.querySelector(".el-table__header-wrapper");
  if (!hdrWrapper || !gridEl) return;
  const ths = hdrWrapper.querySelectorAll("th");
  ths.forEach((th: HTMLElement) => {
    th.addEventListener("mouseenter", () => {
      const thRect = th.getBoundingClientRect();
      const wrapRect = gridEl.getBoundingClientRect();
      indicatorStyle.value = {
        left: (thRect.left - wrapRect.left + 10) + "px",
        width: (thRect.width - 20) + "px",
        opacity: "1",
      };
    });
  });
  hdrWrapper.addEventListener("mouseleave", () => {
    indicatorStyle.value = { left: "0px", width: "0px", opacity: "0" };
  });
  setupTabIndicator();
}

// 标签页滑动指示器
const tabsRef = ref();
const tabIndicatorStyle = ref({ left: "0px", width: "0px", opacity: "0" });

function setupTabIndicator() {
  const el = tabsRef.value?.$el;
  if (!el) return;
  const gridEl = el.closest(".tabs-wrapper") as HTMLElement;
  if (!gridEl) return;
  const nav = el.querySelector(".el-tabs__nav") as HTMLElement;
  if (!nav) return;
  const items = nav.querySelectorAll(".el-tabs__item");
  items.forEach((item: HTMLElement) => {
    item.addEventListener("mouseenter", () => {
      const itemRect = item.getBoundingClientRect();
      const wrapRect = gridEl.getBoundingClientRect();
      tabIndicatorStyle.value = {
        left: (itemRect.left - wrapRect.left + 6) + "px",
        width: (itemRect.width - 12) + "px",
        opacity: "1",
      };
    });
  });
  nav.addEventListener("mouseleave", () => {
    tabIndicatorStyle.value = { left: "0px", width: "0px", opacity: "0" };
  });
}
</script>

<style scoped>
.header-row {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 16px;
}
.header-row h2 { margin: 0; }
.tabs-wrapper { position: relative; }
.tab-indicator {
  position: absolute; top: 0; height: 2px;
  background: var(--el-color-primary);
  border-radius: 1px;
  transition: left 0.35s cubic-bezier(0.4, 0, 0.2, 1), width 0.35s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.2s;
  pointer-events: none; z-index: 3;
}
.table-wrapper { position: relative; }
.header-indicator {
  position: absolute; top: 0; height: 2px;
  background: var(--el-color-primary);
  border-radius: 1px;
  transition: left 0.35s cubic-bezier(0.4, 0, 0.2, 1), width 0.35s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.2s;
  pointer-events: none; z-index: 3;
}

</style>

