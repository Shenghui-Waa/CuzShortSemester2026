import request from "./request";

export interface ProductImageRequest {
  url: string;
  sortOrder?: number;
}

export interface ProductRequest {
  title: string;
  categoryId: number | null;
  price: number;
  originalPrice: number;
  state: number;
  campus: string;
  description: string;
  images: ProductImageRequest[];
}

export const productApi = {
  list: (params: any) => request.get("/product", { params }),
  detail: (id: number) => request.get(`/product/${id}`),
  create: (data: ProductRequest) => request.post("/product", data),
  update: (id: number, data: ProductRequest) => request.put(`/product/${id}`, data),
  updateStatus: (id: number, status: number) => request.put(`/product/${id}/status`, null, { params: { status } }),
  myList: (page: number, pageSize: number) => request.get("/product/my", { params: { page, pageSize } }),
};
