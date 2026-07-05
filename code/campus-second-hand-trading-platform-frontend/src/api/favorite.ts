import request from "./request";

export const favoriteApi = {
  list: (page: number, pageSize: number) => request.get("/favorites", { params: { page, pageSize } }),
  add: (productId: number) => request.post("/favorites", null, { params: { productId } }),
  remove: (productId: number) => request.delete(`/favorites/${productId}`),
  check: (productId: number) => request.get(`/favorites/check/${productId}`),
};
