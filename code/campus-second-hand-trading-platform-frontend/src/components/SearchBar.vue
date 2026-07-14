<template>
  <div class="search-bar">
    <el-input v-model="keyword" placeholder="搜索商品..." clearable class="kw" @keyup.enter="search">
      <template #prefix><el-icon><Search /></el-icon></template>
    </el-input>
    <el-select v-model="categoryId" placeholder="全部分类" clearable style="width:140px">
      <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
    </el-select>
    <el-select v-model="campus" placeholder="全部校区" clearable style="width:130px">
      <el-option v-for="c in campuses" :key="c" :label="c" :value="c" />
    </el-select>
    <el-button type="primary" @click="search">搜索</el-button>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
import { Search } from "@element-plus/icons-vue";
defineProps<{ categories: any[] }>();
const emit = defineEmits<{ search: [q: any] }>();
const keyword = ref("");
const categoryId = ref<number | null>(null);
const campus = ref("");
const campuses = ["下沙校区", "桐乡校区"];
function search() { emit("search", { keyword: keyword.value, categoryId: categoryId.value, campus: campus.value }); }
</script>
<style scoped>
.search-bar { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; margin-bottom: 16px; }
.kw { flex: 1; min-width: 200px; }
</style>
