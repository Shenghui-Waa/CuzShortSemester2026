<template>
  <el-upload :action="uploadUrl" :headers="headers" list-type="picture-card" :on-success="onSuccess" :on-remove="onRemove"
    :file-list="fileList" :limit="6" multiple>
    <el-icon><Plus /></el-icon>
  </el-upload>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { Plus } from "@element-plus/icons-vue";
const props = defineProps<{ modelValue: string[] }>();
const emit = defineEmits(["update:modelValue"]);
const uploadUrl = "/api/files/upload";
const headers = computed(() => ({ Authorization: `Bearer ${localStorage.getItem("token") || ""}` }));
const fileList = ref<any[]>(props.modelValue.map((url: string, i: number) => ({ name: `img${i}`, url })));
function onSuccess(res: any) { if (res.code === 200) { props.modelValue.push(res.data); emit("update:modelValue", props.modelValue); } }
function onRemove(_file: any, fileList: any[]) { const urls = fileList.map((f: any) => f.url || f.response?.data).filter(Boolean); emit("update:modelValue", urls); }
</script>
