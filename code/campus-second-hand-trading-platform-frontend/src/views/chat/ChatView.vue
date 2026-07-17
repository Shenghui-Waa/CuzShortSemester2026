<template>
  <div class="page" style="height:100vh;overflow-y: hidden;">
    <AppHeader />
    <div class="chat-container">
      <div class="contacts-panel">
        <div class="panel-title">消息</div>
        <div class="search-box">
          <el-input v-model="searchText"
                    placeholder="搜索联系人" clearable prefix-icon="Search" size="small" />
        </div>
        <div class="contact-list" v-if="filteredContacts.length">
          <div
            v-for="c in filteredContacts"
            :key="c.contactId"
            class="contact-item"
            :class="{ active: selectedId === c.contactId }"
            @click="selectContact(c)"
          >
            <el-avatar :size="40" :src="c.contactAvatar">{{ (c.contactName || "")[0] }}</el-avatar>
            <div class="contact-info">
              <div class="contact-name">
                <span class="contact-name-text">{{ c.contactName }}</span>
                <span class="contact-time">{{ formatFullTime(c.lastTime) }}</span>
              </div>
              <div class="contact-last-row">
                <span class="contact-last">{{ c.lastMessage || "" }}</span>
                <el-badge v-if="c.unreadCount" :value="c.unreadCount" class="unread" />
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-contacts">{{ searchText ? "无匹配联系人" : "暂无联系人" }}</div>
      </div>

      <div class="chat-area" v-if="selectedId != null">
        <div class="chat-header">
          <span>{{ selectedName }}</span>
          <el-button size="small" text @click="showProfile">•••</el-button>
        </div>
        <div class="messages" ref="msgRef">
          <template v-for="(m, idx) in messages" :key="m.id">
            <div v-if="showTimeSep(idx)" class="time-sep">{{ formatFullTime(m.createdAt) }}</div>
            <div class="msg-row" :class="{ mine: m.senderId === userId }">
              <div class="msg-bubble">{{ m.content }}</div>
            </div>
          </template>
          <div v-if="messages.length === 0" class="empty-msg">暂无消息，发送第一条吧</div>
        </div>
        <div class="input-area">
          <el-input v-model="inputText" placeholder="输入消息..." @keyup.enter="send" maxlength="500" show-word-limit />
          <el-button type="primary" :disabled="!inputText.trim()" @click="send">发送</el-button>
        </div>
      </div>

      <div class="chat-placeholder" v-else>
        <el-icon :size="48" color="#c0c4cc"><ChatDotRound /></el-icon>
        <p>选择一个联系人开始聊天</p>
      </div>

      <el-dialog v-model="userDialogVisible" width="380px" center>
        <div class="profile-content" v-if="profileUser">
          <div class="profile-avatar">
            <el-image
              :src="profileUser.avatar"
              :preview-src-list="[profileUser.avatar]"
              fit="cover"
              style="width:80px;height:80px;border-radius:50%"
            >
              <template #error>
                <el-avatar :size="80">{{ (profileUser.nickname || "")[0] }}</el-avatar>
              </template>
            </el-image>
          </div>
          <div class="profile-info">
            <div class="profile-row"><span class="profile-label">昵称</span><span>{{ profileUser.nickname }}</span></div>
            <div class="profile-row"><span class="profile-label">学校</span><span>{{ profileUser.school || "-" }}</span></div>
            <div class="profile-row"><span class="profile-label">校区</span><span>{{ profileUser.campus || "-" }}</span></div>
            <div class="profile-row">
              <span class="profile-label">手机</span><span>{{ profileUser.phone || "-" }}</span>
              <el-button v-if="profileUser.phone" size="small" text @click="copyText(profileUser.phone)">
                <el-icon><CopyDocument /></el-icon>
              </el-button>
            </div>
            <div class="profile-row">
              <span class="profile-label">邮箱</span><span>{{ profileUser.email || "-" }}</span>
              <el-button v-if="profileUser.email" size="small" text @click="copyText(profileUser.email)">
                <el-icon><CopyDocument /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </el-dialog>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, onUnmounted } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { ChatDotRound, CopyDocument } from "@element-plus/icons-vue";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import { chatApi } from "@/api/index";
import { userApi } from "@/api/user";
import { useUserStore } from "@/stores/user";

const user = useUserStore();
const userId = ref<number>(0);
const contacts = ref<any[]>([]);
const selectedId = ref<number | null>(null);
const selectedName = ref("");
const userDialogVisible = ref(false);
const profileUser = ref<any>(null);
const filteredContacts = computed(() => {
  const kw = searchText.value.trim().toLowerCase();
  if (!kw) return contacts.value;
  return contacts.value.filter((c: any) => (c.contactName || "").toLowerCase().includes(kw));
});
const messages = ref<any[]>([]);
const inputText = ref("");
const msgRef = ref<HTMLElement | null>(null);
const route = useRoute();
const searchText = ref("");
let ws: WebSocket | null = null;

onMounted(async () => {
  await user.ensureUserInfo();
  userId.value = user.userInfo?.id;
  if (!userId.value) return;
  await loadContacts();
  connectWS();
  const q = route.query;
  if (q.contactId) {
    selectContact({ contactId: Number(q.contactId), contactName: (q.contactName as string) || "" });
  }
});

onUnmounted(() => { ws?.close(); });

async function loadContacts() {
  try {
    const res: any = await chatApi.contacts();
    if (res.code === 200) contacts.value = res.data || [];
  } catch {}
}

function connectWS() {
  const proto = location.protocol === "https:" ? "wss:" : "ws:";
  ws = new WebSocket(`${proto}//${location.host}/ws/chat?userId=${userId.value}`);
  ws.onmessage = (e) => {
    try {
      const data = JSON.parse(e.data);
      if (data.senderId && data.content) {
        if (selectedId.value === data.senderId || selectedId.value === data.receiverId) {
          messages.value.push(data);
          scrollBottom();
        }
      }
      loadContacts();
    } catch {}
  };
  ws.onclose = () => {
    setTimeout(() => { if (userId.value) connectWS(); }, 3000);
  };
}

async function selectContact(c: any) {
  selectedId.value = c.contactId;
  selectedName.value = c.contactName || "";
  messages.value = [];
  try {
    const res: any = await chatApi.messages(c.contactId, 1, 100);
    if (res.code === 200) messages.value = res.data || [];
    await chatApi.markRead(c.contactId);
    await loadContacts();
  } catch {} finally {
    await nextTick();
    scrollBottom();
  }
}

async function send() {
  const text = inputText.value.trim();
  if (!text || selectedId.value == null) return;
  inputText.value = "";
  const temp: any = {
    id: Date.now(),
    senderId: userId.value,
    receiverId: selectedId.value,
    content: text,
    createdAt: new Date().toISOString(),
  };
  messages.value.push(temp);
  await nextTick();
  scrollBottom();
  try {
    const res: any = await chatApi.send({ receiverId: selectedId.value, content: text });
    if (res.code === 200 && res.data) {
      temp.id = res.data.id;
      temp.createdAt = res.data.createdAt;
    }
  } catch {
    ElMessage.error("发送失败");
    messages.value = messages.value.filter((m) => m.id !== temp.id);
  }
  await loadContacts();
}

async function showProfile() {
  if (!selectedId.value) return;
  try {
    const res: any = await userApi.getById(selectedId.value);
    if (res.code === 200 && res.data) {
      profileUser.value = res.data;
      userDialogVisible.value = true;
    }
  } catch { /* ignore */ }
}

function copyText(text: string) {
  if (text) {
    navigator.clipboard.writeText(text);
    ElMessage.success("已复制");
  }
}

function scrollBottom() {
  nextTick(() => {
    const el = msgRef.value;
    if (el) el.scrollTop = el.scrollHeight;
  });
}

function formatFullTime(t: string) {
  if (!t) return "";
  const d = new Date(t);
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

function showTimeSep(idx: number) {
  if (idx === 0) return true;
  const prev = messages.value[idx - 1];
  const curr = messages.value[idx];
  if (!prev?.createdAt || !curr?.createdAt) return true;
  const diff = new Date(curr.createdAt).getTime() - new Date(prev.createdAt).getTime();
  return diff > 10 * 60 * 1000;
}
</script>

<style scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 120px);
  width: 800px;
  margin: 0 auto;
  border: 1px solid var(--border-color, #e4e7ed);
  border-radius: 8px;
  overflow: hidden;
  background: var(--bg-page, #fff);
}

.contacts-panel {
  width: 280px;
  border-right: 1px solid var(--border-color, #e4e7ed);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}
.search-box { padding: 8px 12px; }
.panel-title {
  padding: 16px 16px 8px 16px;
  line-height: 20px;
  font-size: 18px;
  font-weight: 700;
  border-bottom: 1px solid var(--border-color, #e4e7ed);
}
.contact-list {
  flex: 1;
  max-height: 80vh;
  overflow-y: auto;

  scrollbar-width: thin;
  scrollbar-color: #bf7683 transparent;
}
.contact-list::-webkit-scrollbar { width: 6px; }
.contact-list::-webkit-scrollbar-thumb { background: #bf7683; border-radius: 3px; }
.contact-list::-webkit-scrollbar-track { background: transparent; }

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background .15s;
  min-height: 64px;
  box-sizing: border-box;
}
.contact-item:hover { background: var(--el-fill-color-light, #f5f7fa); }
.contact-item.active { background: var(--el-color-primary-light-9, #ecf5ff); }
.contact-info { flex: 1; overflow: hidden; min-width: 0; }
.contact-name {
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.contact-name-text { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.contact-time { font-size: 11px; color: #c0c4cc; flex-shrink: 0; margin-left: 4px; }
.contact-last-row { display: flex; align-items: center; margin-top: 2px; min-height: 16px; }
.contact-last {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}
.unread { flex-shrink: 0; }
.empty-contacts {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 14px;
}

.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.chat-header {
  padding: 16px 16px 8px 16px;
  line-height: 20px;
  font-size: 16px;
  font-weight: 700;
  border-bottom: 1px solid var(--border-color, #e4e7ed);
  flex-shrink: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.messages {
  max-height: 75vh;
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;

  scrollbar-width: thin;
  scrollbar-color: #bf7683 transparent;
}
.messages::-webkit-scrollbar { width: 6px; }
.messages::-webkit-scrollbar-thumb { background: #bf7683; border-radius: 3px; }
.messages::-webkit-scrollbar-track { background: transparent; }

.msg-row { display: flex; flex-shrink: 0; }
.msg-row.mine { justify-content: flex-end; }
.msg-bubble {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-all;
  background: #fec7d1;
  color: #303133;
}
.msg-row.mine .msg-bubble {
  background: var(--el-color-primary, #409eff);
  color: #fff;
}
.time-sep {
  text-align: center;
  font-size: 12px;
  color: #c0c4cc;
  padding: 8px 0 4px;
  flex-shrink: 0;
}
.empty-msg {
  text-align: center;
  color: #c0c4cc;
  font-size: 13px;
  margin: auto;
}

.input-area {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  border-top: 1px solid var(--border-color, #e4e7ed);
  flex-shrink: 0;
}

.chat-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  gap: 12px;
  font-size: 15px;
}

.profile-content { text-align: center; }
.profile-avatar { margin-bottom: 16px; }
.profile-info { text-align: left; }
.profile-row {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid var(--border-color, #eee);
  font-size: 14px;
}
.profile-label { width: 50px; color: #909399; flex-shrink: 0; }
.profile-row .el-button { margin-left: auto; }

html.dark .contacts-panel { border-color: #333; }
html.dark .chat-area { border-color: #333; }
html.dark .panel-title, html.dark .chat-header { border-color: #333; }
html.dark .input-area { border-color: #333; }
html.dark .msg-bubble { background: #2a2a2a; color: #e0e0e0; }
html.dark .time-sep { color: #666; }
</style>
