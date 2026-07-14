import { defineStore } from "pinia";
import { ref, watch } from "vue";

export const useThemeStore = defineStore("theme", () => {
  const stored = localStorage.getItem("theme");
  const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
  const isDark = ref(stored ? stored === "dark" : prefersDark);

  function apply(dark: boolean) {
    document.documentElement.classList.toggle("dark", dark);
  }

  function toggle() {
    isDark.value = !isDark.value;
    localStorage.setItem("theme", isDark.value ? "dark" : "light");
  }

  watch(isDark, apply, { immediate: true });

  // 监听浏览器主题变化（仅在用户未手动设置时生效）
  window.matchMedia("(prefers-color-scheme: dark)").addEventListener("change", (e) => {
    if (!localStorage.getItem("theme")) {
      isDark.value = e.matches;
    }
  });

  return { isDark, toggle };
});
