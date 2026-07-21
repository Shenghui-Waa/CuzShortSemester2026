import request from "./request";

export const cartApi = {
  list: () => request.get("/cart"),
  add: (productId: number) => request.post("/cart", null, { params: { productId } }),
  remove: (productId: number) => request.delete(`/cart/${productId}`),
};

export const favoriteApi = {
  list: (page: number, pageSize: number) => request.get("/favorites", { params: { page, pageSize } }),
  add: (productId: number) => request.post("/favorites", null, { params: { productId } }),
  remove: (productId: number) => request.delete(`/favorites/${productId}`),
  check: (productId: number) => request.get(`/favorites/check/${productId}`),
};

export const chatApi = {
  contacts: () => request.get("/chat/contacts"),
  messages: (contactId: number, page: number, pageSize: number) =>
    request.get(`/chat/${contactId}`, { params: { page, pageSize } }),
  send: (data: any) => request.post("/chat/send", data),
  markRead: (contactId: number) => request.put(`/chat/read/${contactId}`),
};

export const reviewApi = {
  create: (data: any) => request.post("/reviews", data),
  getUserReviews: (userId: number, page: number, pageSize: number) =>
    request.get(`/reviews/user/${userId}`, { params: { page, pageSize } }),
};

export const fileApi = {
  upload: (file: FormData) => request.post("/files/upload", file, {
    headers: { "Content-Type": "multipart/form-data" },
  }),
};

export const categoryApi = {
  getAll: () => request.get("/categories"),
  create: (data: any) => request.post("/categories", data),
  update: (id: number, data: any) => request.put(`/categories/${id}`, data),
  delete: (id: number) => request.delete(`/categories/${id}`),
};


export const announcementApi = {
  list: (page: number, pageSize: number) => request.get("/announcements", { params: { page, pageSize } }),
  detail: (id: number) => request.get(`/announcements/${id}`),
  create: (data: any) => request.post("/admin/announcements", data),
  update: (id: number, data: any) => request.put(`/admin/announcements/${id}`, data),
  delete: (id: number) => request.delete(`/admin/announcements/${id}`),
};
export const adminApi = {
  dashboard: () => request.get("/admin/dashboard"),
  userList: (params: any) => request.get("/admin/users", { params }),
  updateUserStatus: (id: number, status: number) => request.put(`/admin/users/${id}/status`, null, { params: { status } }),
  addAdmin: (data: any) => request.post("/admin/users/newadmin", data),
  productList: (params: any) => request.get("/admin/products", { params }),
  updateProductStatus: (id: number, status: number) => request.put(`/admin/products/${id}/status`, null, { params: { status } }),
  orderList: (params: any) => request.get("/admin/orders", { params }),
  categoryCreate: (data: any) => request.post("/admin/categories", data),
  categoryUpdate: (id: number, data: any) => request.put(`/admin/categories/${id}`, data),
  categoryDelete: (id: number) => request.delete(`/admin/categories/${id}`),
};
