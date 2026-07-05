import request from "./request";

export const authApi = {
  login: (data: any) => request.post("/auth/login", data),
  register: (data: any) => request.post("/auth/register", data),
  me: () => request.get("/auth/me"),
};
