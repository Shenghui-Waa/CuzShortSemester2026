<template>
  <div class="page"><AppHeader /><div class="page-container" style="max-width:600px">
      <h2>个人中心</h2>
      <el-form :model="form" label-width="80px" @submit.prevent="save">
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="学校"><el-input v-model="form.school" /></el-form-item>
        <el-form-item label="校区"><el-input v-model="form.campus" /></el-form-item>
        <el-form-item><el-button type="primary" native-type="submit">保存</el-button></el-form-item>
      </el-form>
      <h3>修改密码</h3>
      <el-form :model="pwForm" label-width="80px" @submit.prevent="changePw">
        <el-form-item label="旧密码"><el-input v-model="pwForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item><el-button type="warning" native-type="submit">修改密码</el-button></el-form-item>
      </el-form>
    </div><AppFooter /></div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import { userApi } from "@/api/user";
import { useUserStore } from "@/stores/user";

const user = useUserStore();
const form = reactive({ nickname: "", phone: "", email: "", school: "", campus: "" });
const pwForm = reactive({ oldPassword: "", newPassword: "" });

onMounted(() => { Object.assign(form, user.userInfo); });

async function save() { await userApi.updateProfile(form); user.setUser({ ...user.userInfo, ...form }); ElMessage.success("保存成功"); }
async function changePw() { await userApi.changePassword(pwForm); ElMessage.success("密码修改成功"); pwForm.oldPassword = ""; pwForm.newPassword = ""; }
</script>
