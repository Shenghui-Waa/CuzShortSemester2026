<template>
  <div class="page">
    <AppHeader />
    <div class="page-container" style="max-width:700px">
      <h2>发布商品</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" @submit.prevent="submit">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" placeholder="请输入商品标题" /></el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类"><el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" /></el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="原价"><el-input-number v-model="form.originalPrice" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="成色" prop="condition">
          <el-radio-group v-model="form.condition"><el-radio :value="1">全新</el-radio><el-radio :value="2">几乎全新</el-radio><el-radio :value="3">有使用痕迹</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="校区" prop="campus"><el-input v-model="form.campus" placeholder="交易校区" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="4" placeholder="详细描述商品状况" /></el-form-item>
        <el-form-item label="图片"><ImageUpload v-model="form.images" /></el-form-item>
        <el-form-item><el-button type="primary" native-type="submit" :loading="loading">发布</el-button></el-form-item>
      </el-form>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import ImageUpload from "@/components/ImageUpload.vue";
import { productApi } from "@/api/product";
import { categoryApi } from "@/api/category";

const router = useRouter();
const formRef = ref();
const loading = ref(false);
const categories = ref<any[]>([]);
const form = reactive({ title: "", categoryId: null as number | null, price: 0, originalPrice: 0, condition: 1, campus: "", description: "", images: [] as string[] });
const rules = {
  title: [{ required: true, message: "请输入标题" }],
  categoryId: [{ required: true, message: "请选择分类" }],
  price: [{ required: true, message: "请输入价格" }],
  condition: [{ required: true }],
  campus: [{ required: true, message: "请输入校区" }],
};

onMounted(async () => { const res: any = await categoryApi.getAll(); categories.value = res.data || []; });

async function submit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  loading.value = true;
  try {
    await productApi.create({ ...form, images: form.images });
    ElMessage.success("发布成功");
    router.push("/my-products");
  } catch { } finally { loading.value = false; }
}
</script>
