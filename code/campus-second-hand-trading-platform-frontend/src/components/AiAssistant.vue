<template>
  <div class="ai-assistant">
    <!-- 浮动按钮 -->
    <el-button
      v-show="!visible"
      class="ai-float-btn"
      type="primary"
      circle
      size="large"
      @click="open"
    >
      <el-icon :size="22"><ChatDotRound /></el-icon>
    </el-button>

    <!-- 聊天面板 -->
    <transition name="ai-panel">
      <div v-show="visible" class="ai-panel">
        <div class="ai-panel-header">
          <span class="ai-panel-title">AI 助手</span>
          <el-button
            class="ai-close-btn"
            text
            circle
            size="small"
            @click="visible = false"
          >
            <el-icon :size="16"><Close /></el-icon>
          </el-button>
        </div>
        <div class="ai-panel-body" ref="bodyRef">
          <el-scrollbar ref="scrollbarRef" height="100%">
            <div class="ai-message-list">
              <div
                v-for="(msg, i) in messages"
                :key="i"
                :class="['ai-message', msg.role === 'user' ? 'ai-message-user' : 'ai-message-ai']"
              >
                <div class="ai-message-bubble">
                  <span v-if="msg.role === 'ai'" class="ai-avatar">🤖</span>
                  {{ msg.content }}
                </div>
              </div>
              <div v-if="loading" class="ai-message ai-message-ai">
                <div class="ai-message-bubble ai-typing">
                  <span class="ai-avatar">🤖</span>
                  <span class="dot-pulse"></span>
                </div>
              </div>
            </div>
          </el-scrollbar>
        </div>
        <div class="ai-panel-footer">
          <el-input
            v-model="input"
            class="ai-input"
            placeholder="输入你的问题..."
            :disabled="loading"
            @keyup.enter="send"
          >
            <template #append>
              <el-button
                :icon="Promotion"
                :disabled="loading || !input.trim()"
                @click="send"
              />
            </template>
          </el-input>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from "vue";
import { ChatDotRound, Close, Promotion } from "@element-plus/icons-vue";
import { aiApi } from "@/api/ai";

interface Msg {
  role: "user" | "ai";
  content: string;
}

const visible = ref(false);
const input = ref("");
const loading = ref(false);
const messages = ref<Msg[]>([]);
const bodyRef = ref<HTMLElement>();

function open() {
  visible.value = true;
  if (messages.value.length === 0) {
    messages.value.push({
      role: "ai",
      content: "你好！我是校园二手交易平台的AI助手，可以帮你解答使用问题。你可以问我：如何发布商品、如何购买、订单状态等。",
    });
  }
  nextTick(() => scrollToBottom());
}

async function send() {
  const text = input.value.trim();
  if (!text || loading.value) return;
  messages.value.push({ role: "user", content: text });
  input.value = "";
  loading.value = true;
  nextTick(() => scrollToBottom());
  try {
    const res: any = await aiApi.chat(text);
    const reply = res?.data?.reply || res?.reply || "抱歉，我暂时无法回复，请稍后再试。";
    messages.value.push({ role: "ai", content: reply });
  } catch {
    messages.value.push({ role: "ai", content: "网络异常，请稍后再试。" });
  } finally {
    loading.value = false;
    nextTick(() => scrollToBottom());
  }
}

function scrollToBottom() {
  nextTick(() => {
    const scrollbar = (bodyRef.value?.querySelector(".el-scrollbar__wrap") as HTMLElement);
    if (scrollbar) {
      scrollbar.scrollTop = scrollbar.scrollHeight;
    }
  });
}
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 1000;
}

.ai-float-btn {
  width: 52px;
  height: 52px;
  font-size: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transition: box-shadow 0.3s;
}
.ai-float-btn:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.25);
}

.ai-panel {
  width: 360px;
  height: 480px;
  background: var(--el-bg-color);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid var(--el-border-color-light);
}

.ai-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  background: var(--el-color-primary);
  color: #fff;
  border-radius: 12px 12px 0 0;
}
.ai-panel-title {
  font-size: 15px;
  font-weight: 600;
}
.ai-close-btn {
  color: #fff !important;
}
.ai-close-btn:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

.ai-panel-body {
  flex: 1;
  overflow: hidden;
}
.ai-message-list {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.ai-message {
  display: flex;
  max-width: 85%;
}
.ai-message-user {
  align-self: flex-end;
}
.ai-message-ai {
  align-self: flex-start;
}
.ai-message-bubble {
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
  display: flex;
  align-items: flex-start;
  gap: 6px;
}
.ai-message-user .ai-message-bubble {
  background: var(--el-color-primary);
  color: #fff;
  border-bottom-right-radius: 4px;
}
.ai-message-ai .ai-message-bubble {
  background: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
  border-bottom-left-radius: 4px;
}
.ai-avatar {
  flex-shrink: 0;
  font-size: 16px;
  line-height: 1;
}

.ai-panel-footer {
  padding: 8px 12px;
  border-top: 1px solid var(--el-border-color-lighter);
  background: var(--el-bg-color);
}

.ai-input :deep(.el-input__wrapper) {
  border-radius: 20px;
}

/* 加载动画 */
.ai-typing .dot-pulse {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--el-text-color-secondary);
  animation: ai-dot-pulse 1.2s infinite;
  margin-left: 8px;
  align-self: center;
}
@keyframes ai-dot-pulse {
  0%, 100% { opacity: 0.2; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1.2); }
}

/* 面板过渡动画 */
.ai-panel-enter-active,
.ai-panel-leave-active {
  transition: all 0.3s ease;
}
.ai-panel-enter-from,
.ai-panel-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

html.dark .ai-message-ai .ai-message-bubble { color: #fff; }
html.dark .ai-panel { background: #1d1e1f; border-color: #333; }
html.dark .ai-panel-footer { background: #1d1e1f; }</style>
