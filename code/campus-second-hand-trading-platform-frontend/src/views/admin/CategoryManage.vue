<template>
  <div>
    <div class="header-row">
      <h2>分类管理</h2>
      <el-button type="primary" @click="openAdd">新增分类</el-button>
    </div>
    <el-table :data="categories" stripe row-key="id">
      <el-table-column prop="id" label="ID" width="100" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column label="图标" width="80">
        <template #default="{ row }">
          <el-icon v-if="getIcon(row.icon)" :size="18"><component :is="getIcon(row.icon)" /></el-icon>
          <span v-else style="color:var(--text-muted)">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="420px" :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item prop="name" label="分类名称">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item prop="icon" label="图标">
          <el-select v-model="form.icon" placeholder="选择图标" style="width:100%">
            <el-option
              v-for="opt in iconOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            >
              <el-icon style="vertical-align:middle"><component :is="opt.component" /></el-icon>
              <span style="margin-left:8px;vertical-align:middle">{{ opt.label }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="sortOrder" label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">{{ isEdit ? '保存' : '确认添加' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Goods, Present, School, Monitor, Phone, Watch,
  Burger, Van, Basketball, Reading, House, MoreFilled,
  ShoppingBag, MagicStick, Headset, Notebook
} from "@element-plus/icons-vue";
import { categoryApi, adminApi } from "@/api/index";

const iconOptions = [
  { label: "商品", value: "Goods", component: Goods },
  { label: "电子", value: "Monitor", component: Monitor },
  { label: "手机", value: "Phone", component: Phone },
  { label: "数码", value: "Headset", component: Headset },
  { label: "服饰", value: "ShoppingBag", component: ShoppingBag },
  { label: "美妆", value: "MagicStick", component: MagicStick },
  { label: "手表", value: "Watch", component: Watch },
  { label: "图书", value: "Reading", component: Reading },
  { label: "文具", value: "Notebook", component: Notebook },
  { label: "学习", value: "School", component: School },
  { label: "运动", value: "Basketball", component: Basketball },
  { label: "食品", value: "Burger", component: Burger },
  { label: "交通", value: "Van", component: Van },
  { label: "家居", value: "House", component: House },
  { label: "礼品", value: "Present", component: Present },
  { label: "其他", value: "MoreFilled", component: MoreFilled },
];

const iconMap: Record<string, any> = {};
iconOptions.forEach((o) => (iconMap[o.value] = o.component));
function getIcon(name: string) {
  return iconMap[name] || null;
}

const categories = ref<any[]>([]);
onMounted(() => fetch());

async function fetch() {
  const r: any = await categoryApi.getAll();
  categories.value = r.data || [];
}

const dialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const formRef = ref();
const editId = ref<number | null>(null);
const form = reactive({ name: "", icon: "", sortOrder: 0 });
const rules = {
  name: [{ required: true, message: "请输入分类名称", trigger: "blur" }],
};

function openAdd() {
  isEdit.value = false;
  editId.value = null;
  form.name = "";
  form.icon = "";
  const maxOrder = categories.value.reduce((max, c) => Math.max(max, c.sortOrder || 0), 0);
  form.sortOrder = maxOrder + 1;
  dialogVisible.value = true;
}

function openEdit(row: any) {
  isEdit.value = true;
  editId.value = row.id;
  form.name = row.name || "";
  form.icon = row.icon || "";
  form.sortOrder = row.sortOrder || 0;
  dialogVisible.value = true;
}

async function submit() {
  const ok = await formRef.value?.validate().catch(() => false);
  if (!ok) return;
  submitting.value = true;
  try {
    const data = { name: form.name, icon: form.icon, sortOrder: form.sortOrder };
    if (isEdit.value) {
      await adminApi.categoryUpdate(editId.value!, data);
      ElMessage.success("分类修改成功");
    } else {
      await adminApi.categoryCreate(data);
      ElMessage.success("分类添加成功");
    }
    dialogVisible.value = false;
    fetch();
  } catch {} finally {
    submitting.value = false;
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除分类"${row.name}"？`, "删除确认", { type: "warning" });
    await adminApi.categoryDelete(row.id);
    ElMessage.success("删除成功");
    fetch();
  } catch {}
}
</script>

<style scoped>
.header-row {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 16px;
}
.header-row h2 { margin: 0; }
</style>
