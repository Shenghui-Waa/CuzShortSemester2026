import request from "./request";

export const orderApi = {
  create: (data: { productId: number; remark?: string }) => request.post("/order", data),
  list: (params: any) => request.get("/order", { params }),
  detail: (id: number) => request.get(`/order/${id}`),
  pay: (id: number) => request.put(`/order/${id}/pay`),
  ship: (id: number) => request.put(`/order/${id}/ship`),
  confirm: (id: number) => request.put(`/order/${id}/confirm`),
  cancel: (id: number) => request.put(`/order/${id}/cancel`),
};
