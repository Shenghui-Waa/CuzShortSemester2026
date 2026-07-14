import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore("user", () => {
  const token = ref(localStorage.getItem("token") || "");
  const userInfo = ref<any>(null);
  const role = ref(localStorage.getItem("role") || "0");

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
  return { token, userInfo, role, setToken, setUser, logout, isLogin, isAdmin };
});
