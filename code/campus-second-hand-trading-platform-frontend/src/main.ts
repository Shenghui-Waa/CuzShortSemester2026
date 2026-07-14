import { createApp } from "vue";
import { createPinia } from "pinia";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import App from "./App.vue";
import router from "./router";
import "./styles/variables.scss";
import "./styles/global.scss";
import { authApi } from "./api/auth";
import { useUserStore } from "./stores/user";

const app = createApp(App);
const pinia = createPinia();
app.use(pinia);
app.use(router);
app.use(ElementPlus);
app.mount("#app");

// 启动后校验已有 token 是否有效，无效则清除，防止自动登录无效账户
async function validateToken() {
  const token = localStorage.getItem("token");
  if (!token) return;
  try {
    const res: any = await authApi.me();
    if (res.code === 200 && res.data) {
      useUserStore().setUser(res.data);
    } else {
      localStorage.removeItem("token");
      localStorage.removeItem("role");
    }
  } catch {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
  }
}
validateToken();
