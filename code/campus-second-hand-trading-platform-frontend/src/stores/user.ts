import { defineStore } from "pinia";
import { ref } from "vue";
import { authApi } from "@/api/auth";

export const useUserStore = defineStore("user", () => {
  const token = ref(localStorage.getItem("token") || "");
  const userInfo = ref<any>(null);
  const role = ref(localStorage.getItem("role") || "0");
  const loading = ref(false);

  function setToken(t: string) { token.value = t; localStorage.setItem("token", t); }
  function setUser(u: any) {
    userInfo.value = u;
    const r = String(u?.role ?? "0");
    role.value = r;
    localStorage.setItem("role", r);
  }
  function logout() {
    token.value = ""; userInfo.value = null; role.value = "0";
    localStorage.removeItem("token"); localStorage.removeItem("role");
  }
  const isLogin = () => !!token.value;
  const isAdmin = () => role.value === "1";

  // 确保用户信息已加载，用于订单列表等需要用户ID的场景
  async function ensureUserInfo() {
    if (userInfo.value || !token.value) return;
    if (loading.value) return;
    loading.value = true;
    try {
      const res: any = await authApi.me();
      if (res.code === 200 && res.data) {
        setUser(res.data);
      } else {
        logout();
      }
    } catch {
      logout();
    } finally {
      loading.value = false;
    }
  }

  return { token, userInfo, role, loading, setToken, setUser, logout, isLogin, isAdmin, ensureUserInfo };
});
