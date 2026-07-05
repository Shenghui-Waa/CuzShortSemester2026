import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", name: "home", component: () => import("@/views/HomeView.vue") },
    { path: "/login", name: "login", component: () => import("@/views/LoginView.vue") },
    { path: "/register", name: "register", component: () => import("@/views/RegisterView.vue") },
    { path: "/products", name: "products", component: () => import("@/views/product/ProductList.vue") },
    { path: "/products/:id", name: "product-detail", component: () => import("@/views/product/ProductDetail.vue") },
    { path: "/publish", name: "publish", component: () => import("@/views/product/ProductPublish.vue"), meta: { auth: true } },
    { path: "/orders", name: "orders", component: () => import("@/views/order/OrderList.vue"), meta: { auth: true } },
    { path: "/orders/:id", name: "order-detail", component: () => import("@/views/order/OrderDetail.vue"), meta: { auth: true } },
    { path: "/profile", name: "profile", component: () => import("@/views/user/ProfileView.vue"), meta: { auth: true } },
    { path: "/my-products", name: "my-products", component: () => import("@/views/user/MyProducts.vue"), meta: { auth: true } },
    { path: "/favorites", name: "favorites", component: () => import("@/views/favorite/FavoriteList.vue"), meta: { auth: true } },
    { path: "/chat", name: "chat", component: () => import("@/views/chat/ChatView.vue"), meta: { auth: true } },
    { path: "/admin", name: "admin", component: () => import("@/views/admin/AdminLayout.vue"), meta: { auth: true, admin: true },
      children: [
        { path: "", name: "dashboard", component: () => import("@/views/admin/DashboardView.vue") },
        { path: "users", name: "admin-users", component: () => import("@/views/admin/UserManage.vue") },
        { path: "products", name: "admin-products", component: () => import("@/views/admin/ProductManage.vue") },
        { path: "orders", name: "admin-orders", component: () => import("@/views/admin/OrderManage.vue") },
      ],
    },
  ],
});

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem("token");
  if (to.meta.auth && !token) return next("/login");
  if (to.meta.admin) {
    const role = localStorage.getItem("role");
    if (role !== "1") return next("/");
  }
  next();
});

export default router;
