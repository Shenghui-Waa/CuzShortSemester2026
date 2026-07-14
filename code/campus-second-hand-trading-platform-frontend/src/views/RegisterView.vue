<template>
  <div class="auth-page">
    <div class="card">
      <h2>注册</h2>
      <el-form :model="f" :rules="rules" ref="rf" @submit.prevent="reg">
        <el-form-item prop="username"><el-input v-model="f.username" placeholder="用户名" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="f.password" type="password" placeholder="密码(6-20位)" show-password /></el-form-item>
        <el-form-item prop="nickname"><el-input v-model="f.nickname" placeholder="昵称(选填)" /></el-form-item>
        <el-form-item prop="phone"><el-input v-model="f.phone" placeholder="手机号(选填)" /></el-form-item>
        <el-form-item prop="email"><el-input v-model="f.email" placeholder="邮箱(选填)" /></el-form-item>
        <el-form-item prop="school"><el-input v-model="f.school" placeholder="学校" /></el-form-item>
        <el-form-item prop="campus"><el-input v-model="f.campus" placeholder="校区" /></el-form-item>
        <el-form-item><el-button type="primary" native-type="submit" :loading="loading" style="width:100%">注册</el-button></el-form-item>
      </el-form>
      <p class="tip">已有账号？<router-link to="/login">立即登录</router-link></p>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { authApi } from "@/api/auth";
const router = useRouter();
const rf = ref();
const loading = ref(false);
const f = reactive({ username: "", password: "", nickname: "", phone: "", email: "", school: "", campus: "" });
const rules = {
  username: [{ required: true, message: "请输入用户名" }],
  password: [{ required: true, min: 6, message: "密码至少6位" }],
};
async function reg() {
  const ok = await rf.value?.validate().catch(() => false);
  if (!ok) return;
  loading.value = true;
  try { await authApi.register(f); ElMessage.success("注册成功，请登录"); router.push("/login"); }
  catch {} finally { loading.value = false; }
}
</script>
<style scoped>
.auth-page { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #f5f7fa; }
.card { background: #fff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.08); width: 400px; }
.card h2 { text-align: center; margin-bottom: 24px; font-size: 24px; }
.tip { text-align: center; font-size: 14px; color: #909399; }
</style>
