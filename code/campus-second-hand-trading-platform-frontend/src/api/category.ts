import request from "./request";

export const categoryApi = {
  getAll: () => request.get("/categories"),
};
