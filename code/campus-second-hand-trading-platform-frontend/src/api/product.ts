import request from "./request";

export const productApi = {
  list: (params: any) => request.get("/product", { params }),
  detail: (id: number) => request.get(`/product/${id}`),
  create: (data: any, images: string[]) => {
    const qs = images.length ? "?" + images.map((u) => "images=" + encodeURIComponent(u)).join("&") : "";
    return request.post("/product" + qs, data);
  },
  update: (id: number, data: any, images: string[]) => {
    const qs = images.length ? "?" + images.map((u) => "images=" + encodeURIComponent(u)).join("&") : "";
    return request.put("/product/" + id + qs, data);
  },
  updateStatus: (id: number, status: number) => request.put(`/product/${id}/status`, null, { params: { status } }),
  myList: (page: number, pageSize: number) => request.get("/product/my", { params: { page, pageSize } }),
};
