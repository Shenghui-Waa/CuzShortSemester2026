import request from "./request";

export const cartApi = {
  list: () => request.get("/cart"),
  add: (productId: number) => request.post("/cart", null, { params: { productId } }),
  remove: (id: number) => request.delete(`/cart/${id}`),
};
