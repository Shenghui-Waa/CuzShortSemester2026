<template>
  <div class="my-pagination" v-if="total > pageSize">
    <el-pagination v-model:current-page="current" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="onChange" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
const props = defineProps<{ total: number; pageSize?: number }>();
const emit = defineEmits(["change"]);
const current = ref(1);
watch(() => props.total, () => { current.value = 1; });
function onChange(p: number) { emit("change", p); }
</script>
