import request from "./request";

export const cartApi = {
  list: () => request.get("/cart"),
  add: (productId: number) => request.post("/cart", null, { params: { productId } }),
  remove: (productId: number) => request.delete(`/cart/${productId}`),
};

export const favoriteApi = {
  list: (page: number, pageSize: number) => request.get("/favorite", { params: { page, pageSize } }),
  add: (productId: number) => request.post("/favorite", null, { params: { productId } }),
  remove: (productId: number) => request.delete(`/favorite/${productId}`),
  check: (productId: number) => request.get(`/favorite/check/${productId}`),
};

export const chatApi = {
  contacts: () => request.get("/chat/contacts"),
  messages: (contactId: number, page: number, pageSize: number) =>
    request.get(`/chat/${contactId}`, { params: { page, pageSize } }),
  send: (data: any) => request.post("/chat/send", data),
  markRead: (contactId: number) => request.put(`/chat/read/${contactId}`),
};

export const reviewApi = {
  create: (data: any) => request.post("/review", data),
  getUserReviews: (userId: number, page: number, pageSize: number) =>
    request.get(`/review/user/${userId}`, { params: { page, pageSize } }),
};

export const fileApi = {
  upload: (file: FormData) => request.post("/files/upload", file, {
    headers: { "Content-Type": "multipart/form-data" },
  }),
};

export const categoryApi = {
  getAll: () => request.get("/category"),
  create: (data: any) => request.post("/admin/category", data),
  update: (id: number, data: any) => request.put(`/admin/category/${id}`, data),
  delete: (id: number) => request.delete(`/admin/category/${id}`),
};


export const announcementApi = {
  list: (page: number, pageSize: number) => request.get("/announcement", { params: { page, pageSize } }),
  detail: (id: number) => request.get(`/announcement/${id}`),
  create: (data: any) => request.post("/admin/announcement", data),
  update: (id: number, data: any) => request.put(`/admin/announcement/${id}`, data),
  delete: (id: number) => request.delete(`/admin/announcement/${id}`),
};
export const adminApi = {
  dashboard: () => request.get("/admin/dashboard"),
  userList: (params: any) => request.get("/admin/user", { params }),
  updateUserStatus: (id: number, status: number) => request.put(`/admin/user/${id}/status`, null, { params: { status } }),
  addAdmin: (data: any) => request.post("/admin/user/newadmin", data),
  deleteUser: (id: number) => request.delete(`/admin/user/${id}`),
  resetUserPassword: (id: number, data: any) => request.put(`/admin/user/${id}/reset-password`, data),
  productList: (params: any) => request.get("/admin/product", { params }),
  updateProductStatus: (id: number, status: number) => request.put(`/admin/product/${id}/status`, null, { params: { status } }),
  orderList: (params: any) => request.get("/admin/order", { params }),
  categoryCreate: (data: any) => request.post("/admin/category", data),
  categoryUpdate: (id: number, data: any) => request.put(`/admin/category/${id}`, data),
  categoryDelete: (id: number) => request.delete(`/admin/category/${id}`),
};
