<template>
  <div class="pagination" v-if="total > pageSize">
    <el-pagination background layout="prev, pager, next" :page-size="pageSize" :total="total"
      v-model:current-page="cp" @current-change="$emit('change', $event)" />
  </div>
</template>
<script setup lang="ts">
import { ref, watch } from "vue";
const props = withDefaults(defineProps<{ total: number; pageSize?: number }>(), { pageSize: 12 });
defineEmits<{ change: [p: number] }>();
const cp = ref(1);
watch(() => props.total, () => { cp.value = 1; });
</script>
<style scoped>
.pagination { display: flex; justify-content: center; padding: 24px 0; }
</style>
