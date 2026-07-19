<template>
  <div>
    <div class="header-row">
      <h2>用户管理</h2>
      <el-button type="primary" @click="openDialog">添加管理员</el-button>
    </div>
    <el-input v-model="keyword" placeholder="搜索用户名/昵称" style="width:240px;margin-bottom:16px" clearable @change="fetch" />
    <el-table :data="users" stripe>
      <el-table-column prop="id" label="ID" width="80" /><el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" /><el-table-column prop="school" label="学校" />
      <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status?'danger':'success'">{{ row.status?'封禁':'正常' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="120"><template #default="{row}"><el-button size="small" :type="row.status?'success':'danger'" @click="toggle(row)">{{ row.status?'解封':'封禁' }}</el-button></template></el-table-column>
    </el-table>
    <Pagination :total="total" @change="onPage" />

    <el-dialog v-model="dialogVisible" title="添加管理员" width="420px" :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item prop="username" label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item prop="school" label="学校">
          <el-input v-model="form.school" placeholder="请输入学校" />
        </el-form-item>
        <el-form-item prop="campus" label="校区">
          <el-input v-model="form.campus" placeholder="请输入校区" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { h, ref, reactive, onMounted } from "vue";
import { ElMessage, ElButton } from "element-plus";
import Pagination from "@/components/Pagination.vue";
import { adminApi } from "@/api/index";
const users = ref<any[]>([]); const total = ref(0); const keyword = ref(""); const page = ref(1);
onMounted(() => fetch());
async function fetch() { const r: any = await adminApi.userList({ page: page.value, pageSize: 10, keyword: keyword.value }); users.value = r.data?.records||[]; total.value = r.data?.total||0; }
function onPage(p: number) { page.value = p; fetch(); }
async function toggle(row: any) { await adminApi.updateUserStatus(row.id, row.status?0:1); ElMessage.success("操作成功"); fetch(); }

const dialogVisible = ref(false);
const submitting = ref(false);
const formRef = ref();
const form = reactive({ username: "", school: "", campus: "" });
const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  school: [{ required: true, message: "请输入学校", trigger: "blur" }],
  campus: [{ required: true, message: "请输入校区", trigger: "blur" }],
};

function openDialog() {
  form.username = "";
  form.school = "";
  form.campus = "";
  dialogVisible.value = true;
}

async function submit() {
  const ok = await formRef.value?.validate().catch(() => false);
  if (!ok) return;
  submitting.value = true;
  try {
    const generatedPassword = Math.random().toString(36).slice(-10);
    await adminApi.addAdmin({ username: form.username, password: generatedPassword, school: form.school, campus: form.campus });
    ElMessage.success({
      message: h("div", { style: "display:flex;align-items:center;gap:12px" }, [
        h("span", `管理员添加成功，初始密码为: ${generatedPassword}`),
        h(ElButton, {
          size: "small",
          type: "warning",
          plain: true,
          onClick: () => {
            navigator.clipboard.writeText(generatedPassword).then(() => {
              ElMessage.success("密码已复制到剪贴板");
            }).catch(() => {
              ElMessage.error("复制失败，请手动复制");
            });
          }
        }, () => "复制密码")
      ]),
      duration: 5000
    });
    dialogVisible.value = false;
    fetch();
  } catch {} finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.header-row {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 16px;
}
.header-row h2 { margin: 0; }
</style>
