<template>
  <div class="page"><AppHeader /><div class="page-container" style="max-width:600px">
      <h2>个人中心</h2>
      <div class="back-row"><el-button class="back-btn" @click="$router.back()" text>&lt; 返回</el-button></div>
      <div class="av">
        <el-avatar :src="user.userInfo?.avatar" :size="80"><el-icon :size="40" v-if="!user.userInfo?.avatar"><User /></el-icon></el-avatar>
        <div class="av-btns">
          <el-upload :action="`/api/files/upload`" :headers="{Authorization:`Bearer ${user.token}`}" :show-file-list="false"
            :on-success="onAvatarOk" accept="image/*">
            <el-button size="small" type="primary" plain>更换头像</el-button>
          </el-upload>
          <el-button size="small" plain @click="resetAvatar" v-if="user.userInfo?.avatar">还原默认值</el-button>
        </div>
      </div>
      <el-form :model="f" label-width="80px" @submit.prevent="save" style="margin-top:20px">
        <el-form-item label="昵称"><el-input v-model="f.nickname" /></el-form-item>
        <el-form-item label="手机号">
          <div class="phone-wrap">
            <span class="phone-prefix">+86</span>
            <el-input v-model="f.phone" placeholder="1xx xxxx xxxx" maxlength="11" />
          </div>
        </el-form-item>
        <el-form-item label="邮箱">
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
        <el-form-item label="学校">
          <el-select v-if="!user.isAdmin()" v-model="f.school" style="width:100%"><el-option label="浙江传媒学院" value="浙江传媒学院" /></el-select>
          <el-input v-else v-model="f.school" />
        </el-form-item>
        <el-form-item label="校区">
          <el-select v-if="!user.isAdmin()" v-model="f.campus" style="width:100%"><el-option label="下沙校区" value="下沙校区" /><el-option label="桐乡校区" value="桐乡校区" /></el-select>
          <el-input v-else v-model="f.campus" />
        </el-form-item>
        <el-form-item><el-button type="primary" native-type="submit">保存资料</el-button></el-form-item>
      </el-form>
      <h3>修改密码</h3>
      <el-form :model="pw" label-width="100px" @submit.prevent="changePw">
        <el-form-item label="旧密码"><el-input v-model="pw.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pw.newPassword" type="password" show-password /></el-form-item>
        <el-form-item label="确认新密码"><el-input v-model="pw.confirmPassword" type="password" show-password /></el-form-item>
        <el-form-item><el-button type="warning" native-type="submit">修改密码</el-button></el-form-item>
      </el-form>
    </div><AppFooter /></div>
</template>
<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { User, Back } from "@element-plus/icons-vue";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import { userApi } from "@/api/user";
import { useUserStore } from "@/stores/user";
const user = useUserStore();
const f = reactive({ nickname: "", phone: "", email: "", school: "", campus: "" });
const pw = reactive({ oldPassword: "", newPassword: "", confirmPassword: "" });
const domainOptions = ["qq.com", "163.com", "126.com", "gmail.com", "outlook.com", "其他"];
const emailPrefix = ref("");
const emailDomain = ref("qq.com");
const customDomain = ref("");
const fullEmail = computed(() => {
  if (!emailPrefix.value) return "";
  const domain = emailDomain.value === "其他" ? customDomain.value : emailDomain.value;
  return emailPrefix.value + '@' + domain;
});
watch(fullEmail, (v: string) => { f.email = v; });
onMounted(() => {
  if (user.userInfo) {
    Object.assign(f, user.userInfo);
    if (f.email && f.email.includes('@')) {
      const parts = f.email.split('@');
      emailPrefix.value = parts[0] || '';
      const d = parts[1] || '';
      if (domainOptions.includes(d)) { emailDomain.value = d; }
      else { emailDomain.value = '其他'; customDomain.value = d; }
    }
  }
});
async function save() { await userApi.updateProfile(f); user.setUser({ ...user.userInfo, ...f }); ElMessage.success("已保存"); }
async function changePw() {
  if (!pw.oldPassword || !pw.newPassword) { ElMessage.warning("请填写新旧密码"); return; }
  if (pw.oldPassword === pw.newPassword) { ElMessage.warning("新密码不能与旧密码相同"); return; }
  if (pw.newPassword !== pw.confirmPassword) { ElMessage.warning("两次输入的新密码不一致"); return; }
  await userApi.changePassword({ oldPassword: pw.oldPassword, newPassword: pw.newPassword });
  ElMessage.success("密码已修改"); pw.oldPassword = ""; pw.newPassword = ""; pw.confirmPassword = "";
}
async function resetAvatar() {
  await userApi.updateAvatar("");
  user.setUser({ ...user.userInfo, avatar: "" });
  ElMessage.success("头像已还原为默认值");
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
.av-btns { display: flex; flex-direction: column; gap: 8px; }
.back-row { margin-bottom: 12px; }
.back-btn { cursor: pointer; color: var(--text-secondary); font-size: 14px; transition: color .2s; }
.back-btn:hover { color: var(--el-color-primary); }

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
.domain-back-btn { flex-shrink: 0; margin-left: 4px; }
.domain-area.is-custom :deep(.el-input) > .el-input__wrapper { border-radius: 0; }

html.dark .phone-prefix, html.dark .email-at {
  background: #1e1e1e; border-color: var(--border-color); color: var(--text-muted);
}
html.dark .phone-prefix { border-right: none; }
</style>