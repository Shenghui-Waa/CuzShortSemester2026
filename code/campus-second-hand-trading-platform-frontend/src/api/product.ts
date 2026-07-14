import request from "./request";

export const productApi = {
  list: (params: any) => request.get("/products", { params }),
  detail: (id: number) => request.get(`/products/${id}`),
  create: (data: any, images: string[]) => {
    const qs = images.length ? "?" + images.map((u) => "images=" + encodeURIComponent(u)).join("&") : "";
    return request.post("/products" + qs, data);
  },
  update: (id: number, data: any, images: string[]) => {
    const qs = images.length ? "?" + images.map((u) => "images=" + encodeURIComponent(u)).join("&") : "";
    return request.put("/products/" + id + qs, data);
  },
  updateStatus: (id: number, status: number) => request.put(`/products/${id}/status`, null, { params: { status } }),
  myList: (page: number, pageSize: number) => request.get("/products/my", { params: { page, pageSize } }),
};
