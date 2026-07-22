<template>
  <div class="page">
    <AppHeader />
    <div class="page-container" style="max-width:700px">
      <h2>{{ editId ? "再次上架" : "发布商品" }}</h2>
      <el-form :model="f" :rules="rules" ref="rf" label-width="80px" @submit.prevent="submit">
        <el-form-item label="标题" prop="title"><el-input v-model="f.title" placeholder="请输入商品标题" /></el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="f.categoryId" placeholder="选择分类" style="width:100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="售价" prop="price"><el-input-number v-model="f.price" :min="0" :precision="2" style="width:200px" /></el-form-item>
        <el-form-item label="原价"><el-input-number v-model="f.originalPrice" :min="0" :precision="2" style="width:200px" /></el-form-item>
        <el-form-item label="成色" prop="condition">
          <el-radio-group v-model="f.condition">
            <el-radio :value="1">全新</el-radio>
            <el-radio :value="2">几乎全新</el-radio>
            <el-radio :value="3">有使用痕迹</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="校区" prop="campus"><el-select v-model="f.campus" placeholder="选择校区" style="width:100%"><el-option label="下沙校区" value="下沙校区" /><el-option label="桐乡校区" value="桐乡校区" /></el-select></el-form-item>
        <el-form-item label="描述"><el-input v-model="f.description" type="textarea" :rows="4" placeholder="详细描述商品状况..." /></el-form-item>
        <el-form-item label="图片">
          <ImageUpload v-model="images" />
          <div class="img-hint">上传后自动获取链接，点击图片右上角可删除</div>
        </el-form-item>
        <el-form-item><el-button type="primary" native-type="submit" :loading="loading" size="large">发布商品</el-button></el-form-item>
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
import { categoryApi } from "@/api/index";

import { useRoute } from "vue-router";
const router = useRouter();
const route = useRoute();
const rf = ref();
const loading = ref(false);
const categories = ref<any[]>([]);
const images = ref<string[]>([]);

const f = reactive({
  title: "",
  categoryId: null as number | null,
  price: 0,
  originalPrice: 0,
  condition: 1,
  campus: "",
  description: "",
});

const rules = {
  title: [{ required: true, message: "请输入标题", trigger: "blur" }],
  categoryId: [{ required: true, message: "请选择分类", trigger: "change" }],
  price: [{ required: true, message: "请输入价格", trigger: "blur" }],
  condition: [{ required: true }],
  campus: [{ required: true, message: "请输入校区", trigger: "blur" }],
};

const editId = ref<string | null>(null);

onMounted(async () => {
  const r: any = await categoryApi.getAll();
  categories.value = r.data || [];
  const q = route.query;
  if (q.id) {
    editId.value = q.id as string;
    f.title = (q.title as string) || "";
    f.price = Number(q.price) || 0;
    f.categoryId = Number(q.categoryId) || null;
    f.condition = Number(q.condition) ?? 0;
    f.campus = (q.campus as string) || "";
    f.description = (q.description as string) || "";
    f.originalPrice = Number(q.originalPrice) || 0;
    try {
      const detail: any = await productApi.detail(Number(q.id));
      if (detail.data?.images) images.value = detail.data.images;
    } catch {}
  }
});

async function submit() {
  const ok = await rf.value?.validate().catch(() => false);
  if (!ok) return;
  loading.value = true;
  try {
    if (editId.value) {
      await productApi.update(Number(editId.value), { ...f }, images.value);
      ElMessage.success("已重新上架，等待审核");
    } else {
      await productApi.create({ ...f }, images.value);
      ElMessage.success("发布成功，等待审核");
    }
    router.push("/my-products");
  } catch {} finally { loading.value = false; }
}
</script>
<style scoped>
.img-hint { font-size: 12px; color: var(--text-muted); margin-top: 4px; }
</style>

