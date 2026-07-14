import request from "./request";

export const authApi = {
  login: (data: { username: string; password: string }) => request.post("/auth/login", data),
  register: (data: any) => request.post("/auth/register", data),
  logout: () => request.post("/auth/logout"),
  me: () => request.get("/auth/me"),
};
