<template>
  <div class="page"><AppHeader /><div class="page-container chat-container">
      <div class="chat-sidebar">
        <h3>会话</h3>
        <div v-for="c in contacts" :key="c.contactId" class="contact-item" :class="{ active: c.contactId === activeContact }" @click="openChat(c)">
          <span>{{ c.contactName }}</span>
          <el-badge v-if="c.unreadCount" :value="c.unreadCount" />
        </div>
      </div>
      <div class="chat-main">
        <div v-if="activeContact" class="msg-list" ref="msgListRef">
          <div v-for="m in messages" :key="m.id" class="msg" :class="{ mine: m.senderId !== activeContact }">
            <div class="msg-content">{{ m.content }}</div>
            <div class="msg-time">{{ formatDate(m.createdAt) }}</div>
          </div>
        </div>
        <div v-else class="no-chat">选择一个会话开始聊天</div>
        <div v-if="activeContact" class="input-area">
          <el-input v-model="msgText" placeholder="输入消息..." @keyup.enter="sendMsg" /><el-button type="primary" @click="sendMsg">发送</el-button>
        </div>
      </div>
    </div><AppFooter /></div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from "vue";
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
import { chatApi } from "@/api/chat";
import { formatDate } from "@/utils";

const contacts = ref<any[]>([]);
const activeContact = ref<number | null>(null);
const messages = ref<any[]>([]);
const msgText = ref("");
const msgListRef = ref<any>(null);

onMounted(async () => { const res: any = await chatApi.contacts(); contacts.value = res.data || []; });

async function openChat(c: any) {
  activeContact.value = c.contactId;
  await chatApi.markRead(c.contactId);
  c.unreadCount = 0;
  const res: any = await chatApi.messages(c.contactId, 1, 100);
  messages.value = res.data || [];
  nextTick(() => { if (msgListRef.value) msgListRef.value.scrollTop = msgListRef.value.scrollHeight; });
}

async function sendMsg() {
  if (!msgText.value.trim() || !activeContact.value) return;
  await chatApi.send({ receiverId: activeContact.value, content: msgText.value });
  messages.value.push({ id: Date.now(), senderId: 0, content: msgText.value, createdAt: new Date().toISOString() });
  msgText.value = "";
  nextTick(() => { if (msgListRef.value) msgListRef.value.scrollTop = msgListRef.value.scrollHeight; });
}
</script>

<style scoped>
.chat-container { display: flex; height: calc(100vh - 200px); gap: 0; }
.chat-sidebar { width: 200px; border-right: 1px solid #ebeef5; padding: 12px; overflow-y: auto; }
.contact-item { padding: 10px; cursor: pointer; border-radius: 4px; display: flex; justify-content: space-between; align-items: center; }
.contact-item:hover, .contact-item.active { background: #ecf5ff; }
.chat-main { flex: 1; display: flex; flex-direction: column; }
.msg-list { flex: 1; overflow-y: auto; padding: 16px; }
.msg { margin-bottom: 12px; }
.msg.mine { text-align: right; }
.msg-content { display: inline-block; background: #ecf5ff; padding: 8px 12px; border-radius: 8px; max-width: 70%; }
.msg.mine .msg-content { background: #409eff; color: #fff; }
.msg-time { font-size: 11px; color: #c0c4cc; margin-top: 4px; }
.no-chat { flex: 1; display: flex; align-items: center; justify-content: center; color: #909399; }
.input-area { display: flex; gap: 8px; padding: 12px; border-top: 1px solid #ebeef5; }
</style>
