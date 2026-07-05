<template>
  <div class="register-page">
    <div class="form-card">
      <h2>注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleRegister">
        <el-form-item prop="username"><el-input v-model="form.username" placeholder="用户名" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="form.password" type="password" placeholder="密码(6-20位)" show-password /></el-form-item>
        <el-form-item prop="nickname"><el-input v-model="form.nickname" placeholder="昵称(选填)" /></el-form-item>
        <el-form-item prop="phone"><el-input v-model="form.phone" placeholder="手机号(选填)" /></el-form-item>
        <el-form-item prop="school"><el-input v-model="form.school" placeholder="学校" /></el-form-item>
        <el-form-item prop="campus"><el-input v-model="form.campus" placeholder="校区" /></el-form-item>
        <el-form-item><el-button type="primary" native-type="submit" :loading="loading" style="width:100%">注册</el-button></el-form-item>
      </el-form>
      <p class="tip">已有账号？<router-link to="/login">立即登录</router-link></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { authApi } from "@/api/auth";
import { ElMessage } from "element-plus";

const router = useRouter();
const formRef = ref();
const loading = ref(false);
const form = reactive({ username: "", password: "", nickname: "", phone: "", school: "", campus: "" });
const rules = {
  username: [{ required: true, message: "请输入用户名" }],
  password: [{ required: true, min: 6, message: "密码至少6位" }],
};

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  loading.value = true;
  try { await authApi.register(form); ElMessage.success("注册成功，请登录"); router.push("/login"); }
  catch { } finally { loading.value = false; }
}
</script>

<style scoped>
.register-page { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #f5f7fa; }
.form-card { background: #fff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.08); width: 400px; }
.form-card h2 { text-align: center; margin-bottom: 24px; font-size: 24px; }
.tip { text-align: center; font-size: 14px; color: #909399; }
</style>
