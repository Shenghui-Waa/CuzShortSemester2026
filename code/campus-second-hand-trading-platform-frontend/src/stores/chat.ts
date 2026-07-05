import { defineStore } from "pinia";
import { ref } from "vue";

export const useChatStore = defineStore("chat", () => {
  const unreadTotal = ref(0);
  function setUnreadTotal(n: number) { unreadTotal.value = n; }
  return { unreadTotal, setUnreadTotal };
});
