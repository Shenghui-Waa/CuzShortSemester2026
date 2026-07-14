<template>
  <div class="image-upload">
    <div v-for="(url, i) in urls" :key="i" class="img-preview">
      <img :src="url" />
      <span class="img-remove" @click="removeUrl(i)">&times;</span>
    </div>
    <el-upload
      v-if="urls.length < limit"
      :action="`/api/files/upload`"
      :headers="uploadHeaders"
      :show-file-list="false"
      :on-success="onUploaded"
      :before-upload="beforeUpload"
      accept="image/*"
      class="upload-trigger"
    >
      <el-icon :size="28"><Plus /></el-icon>
    </el-upload>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { Plus } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const props = withDefaults(defineProps<{ modelValue: string[]; limit?: number }>(), { limit: 6 });
const emit = defineEmits<{ "update:modelValue": [v: string[]] }>();

const uploadHeaders = { Authorization: `Bearer ${localStorage.getItem("token") || ""}` };
const urls = ref<string[]>([...props.modelValue]);

watch(() => props.modelValue, (v) => { urls.value = [...v]; });

function beforeUpload(file: File) {
  const isImage = file.type.startsWith("image/");
  if (!isImage) ElMessage.error("只能上传图片");
  return isImage;
}

function onUploaded(res: any) {
  if (res.code === 200 && res.data) {
    urls.value.push(res.data);
    emit("update:modelValue", [...urls.value]);
  } else {
    ElMessage.error("图片上传失败");
  }
}

function removeUrl(i: number) {
  urls.value.splice(i, 1);
  emit("update:modelValue", [...urls.value]);
}
</script>

<style scoped>
.image-upload { display: flex; flex-wrap: wrap; gap: 8px; }
.img-preview { width: 100px; height: 100px; border-radius: 6px; overflow: hidden; position: relative; }
.img-preview img { width: 100%; height: 100%; object-fit: cover; }
.img-remove { position: absolute; top: 2px; right: 2px; width: 20px; height: 20px; background: rgba(0,0,0,.6); color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; font-size: 16px; line-height: 1; }
.upload-trigger { width: 100px; height: 100px; border: 1px dashed #d9d9d9; border-radius: 6px; display: flex; align-items: center; justify-content: center; cursor: pointer; }
.upload-trigger:hover { border-color: #409eff; }
</style>
