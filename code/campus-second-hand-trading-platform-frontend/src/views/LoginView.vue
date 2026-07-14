<template>
  <div class="auth-page">
    <div class="card">
      <h2>登录</h2>
      <el-form :model="f" :rules="rules" ref="rf" @submit.prevent="login">
        <el-form-item prop="username"><el-input v-model="f.username" placeholder="用户名" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="f.password" type="password" placeholder="密码" show-password /></el-form-item>
        <el-form-item><el-button type="primary" native-type="submit" :loading="loading" style="width:100%">登录</el-button></el-form-item>
      </el-form>
      <p class="tip">还没有账号？<router-link to="/register">立即注册</router-link></p>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { authApi } from "@/api/auth";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const user = useUserStore();
const rf = ref();
const loading = ref(false);
const f = reactive({ username: "", password: "" });
const rules = {
  username: [{ required: true, message: "请输入用户名" }],
  password: [{ required: true, message: "请输入密码" }],
};
async function login() {
  const ok = await rf.value?.validate().catch(() => false);
  if (!ok) return;
  loading.value = true;
  try {
    const r: any = await authApi.login(f);
    user.setToken(r.data);
    const me: any = await authApi.me();
    user.setUser(me.data);
    ElMessage.success("登录成功");
    router.push("/");
  } catch {} finally { loading.value = false; }
}
</script>
<style scoped>
.auth-page { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: var(--bg-page); }
.card { background: var(--bg-card); padding: 40px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.08); width: 380px; }
.card h2 { text-align: center; margin-bottom: 24px; font-size: 24px; }
.tip { text-align: center; font-size: 14px; color: var(--text-muted); }
</style>

