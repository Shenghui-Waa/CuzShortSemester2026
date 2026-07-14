<template>
  <div class="page"><AppHeader /><div class="page-container" style="max-width:600px">
      <h2>个人中心</h2>
      <div class="av">
        <el-avatar :src="user.userInfo?.avatar" :size="80" />
        <el-upload :action="`/api/files/upload`" :headers="{Authorization:`Bearer ${user.token}`}" :show-file-list="false"
          :on-success="onAvatarOk" accept="image/*">
          <el-button size="small" type="primary" plain>更换头像</el-button>
        </el-upload>
      </div>
      <el-form :model="f" label-width="80px" @submit.prevent="save" style="margin-top:20px">
        <el-form-item label="昵称"><el-input v-model="f.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="f.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="f.email" /></el-form-item>
        <el-form-item label="学校"><el-input v-model="f.school" /></el-form-item>
        <el-form-item label="校区"><el-input v-model="f.campus" /></el-form-item>
        <el-form-item><el-button type="primary" native-type="submit">保存资料</el-button></el-form-item>
      </el-form>
      <h3>修改密码</h3>
      <el-form :model="pw" label-width="80px" @submit.prevent="changePw">
        <el-form-item label="旧密码"><el-input v-model="pw.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pw.newPassword" type="password" show-password /></el-form-item>
        <el-form-item><el-button type="warning" native-type="submit">修改密码</el-button></el-form-item>
      </el-form>
    </div><AppFooter /></div>
</template>
<script setup lang="ts">
import { reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import { userApi } from "@/api/user";
import { useUserStore } from "@/stores/user";
const user = useUserStore();
const f = reactive({ nickname: "", phone: "", email: "", school: "", campus: "" });
const pw = reactive({ oldPassword: "", newPassword: "" });
onMounted(() => { if (user.userInfo) Object.assign(f, user.userInfo); });
async function save() { await userApi.updateProfile(f); user.setUser({ ...user.userInfo, ...f }); ElMessage.success("已保存"); }
async function changePw() {
  if (!pw.oldPassword || !pw.newPassword) { ElMessage.warning("请填写新旧密码"); return; }
  await userApi.changePassword(pw); ElMessage.success("密码已修改"); pw.oldPassword = ""; pw.newPassword = "";
}
async function onAvatarOk(res: any) {
  if (res.code === 200 && res.data) {
    await userApi.updateAvatar(res.data);
    user.setUser({ ...user.userInfo, avatar: res.data });
    ElMessage.success("头像已更新");
  }
}
</script>
<style scoped>
.av { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; }
</style>
