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

// 启动后校验已有 token 是否有效，无效则清除
const token = localStorage.getItem("token");
if (token) {
  authApi.me().then((res: any) => {
    if (res.code === 200) useUserStore().setUser(res.data);
  }).catch(() => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
  });
}

