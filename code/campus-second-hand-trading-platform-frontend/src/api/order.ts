import request from "./request";

export const orderApi = {
  create: (data: any) => request.post("/orders", data),
  list: (params: any) => request.get("/orders", { params }),
  detail: (id: number) => request.get(`/orders/${id}`),
  pay: (id: number) => request.put(`/orders/${id}/pay`),
  ship: (id: number) => request.put(`/orders/${id}/ship`),
  confirm: (id: number) => request.put(`/orders/${id}/confirm`),
  cancel: (id: number) => request.put(`/orders/${id}/cancel`),
};
