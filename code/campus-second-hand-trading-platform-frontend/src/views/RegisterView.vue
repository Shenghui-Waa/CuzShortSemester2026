<template>
  <div class="auth-page">
    <div class="card">
      <h2>注册</h2>
      <el-form :model="f" :rules="rules" ref="rf" @submit.prevent="reg" label-width="80px">
        <el-form-item prop="username" label="用户名">
          <el-input v-model="f.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item prop="password" label="密码">
          <el-input v-model="f.password" type="password" placeholder="6-20位" show-password />
        </el-form-item>
        <el-form-item prop="nickname" label="昵称">
          <el-input v-model="f.nickname" placeholder="选填" />
        </el-form-item>
        <el-form-item label="学校">
          <el-input model-value="浙江传媒学院" disabled />
        </el-form-item>
        <el-form-item prop="campus" label="校区">
          <el-select v-model="f.campus" placeholder="选择校区" style="width:100%">
            <el-option label="下沙校区" value="下沙校区" />
            <el-option label="桐乡校区" value="桐乡校区" />
          </el-select>
        </el-form-item>
        <el-form-item prop="phone" label="手机号">
          <div class="phone-wrap">
            <span class="phone-prefix">+86</span>
            <el-input v-model="f.phone" placeholder="1xx xxxx xxxx" maxlength="11" />
          </div>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <div class="email-wrap">
            <el-input v-model="emailPrefix" placeholder="邮箱前缀" />
            <span class="email-at">@</span>
            <div class="domain-area" :class="{ 'is-custom': emailDomain === '其他' }">
              <template v-if="emailDomain !== '其他'">
                <el-select v-model="emailDomain">
                  <el-option v-for="d in domainOptions" :key="d" :label="d" :value="d" />
                </el-select>
              </template>
              <template v-else>
                <el-input v-model="customDomain" placeholder="如 qq.com" />
                <el-button @click="emailDomain='qq.com'" text size="small" class="domain-back-btn">
                  <el-icon :size="14"><Back /></el-icon>
                </el-button>
              </template>
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" style="width:100%">注册</el-button>
        </el-form-item>
      </el-form>
      <p class="tip">已有账号？<router-link to="/login">立即登录</router-link></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Back } from "@element-plus/icons-vue";
import { authApi } from "@/api/auth";

const router = useRouter();
const rf = ref();
const loading = ref(false);

const f = reactive({
  username: "",
  password: "",
  nickname: "",
  phone: "",
  email: "",
  school: "浙江传媒学院",
  campus: "",
});

const domainOptions = ["qq.com", "163.com", "126.com", "gmail.com", "outlook.com", "其他"];

const emailPrefix = ref("");
const emailDomain = ref("qq.com");
const customDomain = ref("");

const fullEmail = computed(() => {
  if (!emailPrefix.value) return "";
  const domain = emailDomain.value === "其他" ? customDomain.value : emailDomain.value;
  return emailPrefix.value + '@' + domain;
});

watch(fullEmail, (v) => { f.email = v; });

const validatePhone = (_rule: any, value: string, cb: any) => {
  if (!value) return cb();
  if (!/^\d{11}$/.test(value)) return cb(new Error("手机号须为11位纯数字"));
  if (value[0] !== "1") return cb(new Error("手机号须以1开头"));
  cb();
};

const validateEmail = (_rule: any, _value: string, cb: any) => {
  if (!emailPrefix.value) return cb();
  if (emailDomain.value === "其他" && !customDomain.value) return cb(new Error("请输入邮箱域名"));
  if (emailDomain.value === "其他" && !/^[a-zA-Z0-9]+\.[a-zA-Z]+$/.test(customDomain.value))
    return cb(new Error("域名格式如 .xxx.xxx"));
  cb();
};

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码6-20位", trigger: "blur" },
  ],
  phone: [{ validator: validatePhone, trigger: "blur" }],
  email: [{ validator: validateEmail, trigger: "blur" }],
  campus: [{ required: true, message: "请选择校区", trigger: "change" }],
};

async function reg() {
  const ok = await rf.value?.validate().catch(() => false);
  if (!ok) return;
  loading.value = true;
  try {
    await authApi.register({ ...f });
    ElMessage.success("注册成功，请登录");
    router.push("/login");
  } catch {} finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.auth-page {
  display: flex; justify-content: center; align-items: center;
  min-height: 100vh; background: var(--bg-page);
}
.card {
  background: var(--bg-card); padding: 36px 40px;
  border-radius: 10px; box-shadow: var(--shadow); width: 100%; max-width: 460px; margin: 0 16px;
}
.card h2 { text-align: center; margin-bottom: 24px; font-size: 24px; color: var(--text-primary); }
.tip { text-align: center; font-size: 14px; color: var(--text-muted); }

.phone-wrap { display: flex; align-items: stretch; width: 100%; }
.phone-prefix {
  display: flex; align-items: center; padding: 0 12px;
  font-size: 13px; color: var(--text-secondary);
  background: var(--border-light);
  border: 1px solid var(--border-color); border-right: none;
  border-radius: var(--el-input-border-radius, 4px) 0 0 var(--el-input-border-radius, 4px);
  white-space: nowrap; flex-shrink: 0;
}
.phone-wrap :deep(.el-input) { flex: 1; }
.phone-wrap :deep(.el-input__wrapper) {
  border-radius: 0 var(--el-input-border-radius, 4px) var(--el-input-border-radius, 4px) 0;
}

.email-wrap { display: flex; align-items: stretch; width: 100%; }
.email-wrap :deep(.el-input:first-child) { flex: 1; }
.email-wrap > :deep(.el-input) > .el-input__wrapper {
  border-radius: var(--el-input-border-radius, 4px) 0 0 var(--el-input-border-radius, 4px);
}
.email-at {
  display: flex; align-items: center; padding: 0 8px;
  font-size: 13px; color: var(--text-secondary);
  background: var(--border-light);
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
  white-space: nowrap; flex-shrink: 0;
}

/* 域名选择区域：固定宽度 + 平滑过渡 */
.domain-area {
  flex-shrink: 0; display: flex; align-items: stretch;
  width: 120px;
  transition: width 0.3s ease;
  overflow: hidden;
}
.domain-area.is-custom { width: 150px; }

.domain-area :deep(.el-select) { width: 100%; }
.domain-area :deep(.el-select) > .el-input > .el-input__wrapper {
  border-radius: 0 var(--el-input-border-radius, 4px) var(--el-input-border-radius, 4px) 0;
}
.domain-area :deep(.el-input) { flex: 1; min-width: 0; }
.domain-area :deep(.el-input) > .el-input__wrapper {
  border-radius: 0 var(--el-input-border-radius, 4px) var(--el-input-border-radius, 4px) 0;
}
.domain-back-btn {
  flex-shrink: 0; margin-left: 4px;
}
.domain-area.is-custom :deep(.el-input) > .el-input__wrapper {
  border-radius: 0;
}

/* 暗色模式 */
html.dark .phone-prefix,
html.dark .email-at {
  background: #1e1e1e;
  border-color: var(--border-color);
  color: var(--text-muted);
}
html.dark .phone-prefix { border-right: none; }

/* 暗色下 disabled / select 覆盖 */
html.dark :deep(.el-input.is-disabled .el-input__wrapper) {
  background: #1e1e1e; box-shadow: 0 0 0 1px var(--border-color) inset;
}
html.dark :deep(.el-input.is-disabled .el-input__inner) { color: var(--text-muted); }
html.dark :deep(.el-select .el-input__wrapper) { background: #262626; }
</style>

