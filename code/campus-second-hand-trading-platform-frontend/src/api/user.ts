import request from "./request";

export const userApi = {
  getById: (id: number) => request.get(`/user/${id}`),
  updateProfile: (data: any) => request.put("/user/profile", data),
  changePassword: (data: any) => request.put("/user/password", data),
  uploadAvatar: (file: FormData) => request.post("/user/avatar", file, { headers: { "Content-Type": "multipart/form-data" } }),
};
