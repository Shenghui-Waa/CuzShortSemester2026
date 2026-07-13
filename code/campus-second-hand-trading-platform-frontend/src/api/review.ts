import request from "./request";

export const reviewApi = {
  create: (data: any) => request.post("/reviews", data),
  getUserReviews: (userId: number, page: number, pageSize: number) =>
    request.get(`/reviews/user/${userId}`, { params: { page, pageSize } }),
};

export const fileApi = {
  upload: (file: FormData) => request.post("/files/upload", file, { headers: { "Content-Type": "multipart/form-data" } }),
  uploads: (files: FormData) => request.post("/files/uploads", files, { headers: { "Content-Type": "multipart/form-data" } }),
};

export const adminApi = {
  dashboard: () => request.get("/admin/dashboard"),
  userList: (params: any) => request.get("/admin/users", { params }),
  updateUserStatus: (id: number, status: number) => request.put(`/admin/users/${id}/status`, null, { params: { status } }),
  productList: (params: any) => request.get("/admin/products", { params }),
  updateProductStatus: (id: number, status: number) => request.put(`/admin/products/${id}/status`, null, { params: { status } }),
  orderList: (params: any) => request.get("/admin/orders", { params }),
  createCategory: (data: any) => request.post("/admin/categories", data),
  updateCategory: (id: number, data: any) => request.put(`/admin/categories/${id}`, data),
  deleteCategory: (id: number) => request.delete(`/admin/categories/${id}`),
};
