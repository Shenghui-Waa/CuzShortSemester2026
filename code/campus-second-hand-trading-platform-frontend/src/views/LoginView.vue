<template>
  <div class="login-page">
    <div class="form-card">
      <h2>登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username"><el-input v-model="form.username" placeholder="用户名" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="form.password" type="password" placeholder="密码" show-password /></el-form-item>
        <el-form-item><el-button type="primary" native-type="submit" :loading="loading" style="width:100%">登录</el-button></el-form-item>
      </el-form>
      <p class="tip">还没有账号？<router-link to="/register">立即注册</router-link></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { authApi } from "@/api/auth";
import { useUserStore } from "@/stores/user";
import { ElMessage } from "element-plus";

const router = useRouter();
const user = useUserStore();
const formRef = ref();
const loading = ref(false);
const form = reactive({ username: "", password: "" });
const rules = {
  username: [{ required: true, message: "请输入用户名" }],
  password: [{ required: true, message: "请输入密码" }],
};

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  loading.value = true;
  try {
    const res: any = await authApi.login(form);
    user.setToken(res.data);
    const me: any = await authApi.me();
    user.setUser(me.data);
    ElMessage.success("登录成功");
    router.push("/");
  } catch { } finally { loading.value = false; }
}
</script>

<style scoped>
.login-page { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #f5f7fa; }
.form-card { background: #fff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.08); width: 380px; }
.form-card h2 { text-align: center; margin-bottom: 24px; font-size: 24px; }
.tip { text-align: center; font-size: 14px; color: #909399; }
</style>
