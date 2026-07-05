import request from "./request";

export const productApi = {
  list: (params: any) => request.get("/products", { params }),
  detail: (id: number) => request.get(`/products/${id}`),
  create: (data: any) => request.post("/products", data),
  update: (id: number, data: any) => request.put(`/products/${id}`, data),
  updateStatus: (id: number, status: number) => request.put(`/products/${id}/status`, null, { params: { status } }),
  myList: (page: number, pageSize: number) => request.get("/products/my", { params: { page, pageSize } }),
};
