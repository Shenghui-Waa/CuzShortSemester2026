<template>
  <div class="page"><AppHeader /><div class="page-container">
      <h2>购物车</h2>
      <div v-if="items.length">
        <el-table :data="items" stripe @selection-change="onSel">
          <el-table-column type="selection" width="50" />
          <el-table-column label="商品" min-width="300">
            <template #default="{ row }">
              <div class="cp" @click="$router.push(`/products/${row.productId}`)">
                <img :src="row.productImage||'/placeholder.png'" class="ci" />
                <div><div class="ct">{{ row.productTitle }}</div><div class="cs">卖家：{{ row.sellerName }}</div></div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120"><template #default="{ row }">{{ formatPrice(row.price) }}</template></el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ row }"><el-button size="small" type="danger" @click="removeItem(row.productId)">删除</el-button></template>
          </el-table-column>
        </el-table>
        <div class="bottom">
          <span>已选 {{ selected.length }} 件</span>
          <el-button type="primary" size="large" :disabled="!selected.length" @click="batchBuy">批量购买</el-button>
        </div>
      </div>
      <el-empty v-else description="购物车是空的"><el-button type="primary" @click="$router.push('/products')">去逛逛</el-button></el-empty>
    </div><AppFooter /></div>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import { cartApi } from "@/api/index";
import { orderApi } from "@/api/order";
import { formatPrice } from "@/utils";
const router = useRouter();
const items = ref<any[]>([]);
const selected = ref<number[]>([]);
onMounted(() => fetchCart());
async function fetchCart() { const r: any = await cartApi.list(); items.value = r.data || []; }
function onSel(rows: any[]) { selected.value = rows.map((r:any) => r.productId); }
async function removeItem(pid: number) { await cartApi.remove(pid); ElMessage.success("已移除"); fetchCart(); }
async function batchBuy() { for (const pid of selected.value) { try { await orderApi.create({ productId: pid }); } catch {} } ElMessage.success("下单成功"); router.push("/orders"); }
</script>
<style scoped>
.cp { display: flex; align-items: center; gap: 12px; cursor: pointer; }
.ci { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; }
.ct { font-size: 14px; font-weight: 500; }
.cs { font-size: 12px; color: #909399; margin-top: 4px; }
.bottom { display: flex; justify-content: space-between; align-items: center; margin-top: 20px; padding: 16px 0; border-top: 1px solid var(--border-color); font-size: 15px; }
</style>

