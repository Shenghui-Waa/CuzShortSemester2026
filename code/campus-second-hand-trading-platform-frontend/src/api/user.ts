import request from "./request";

export const userApi = {
  getById: (id: number) => request.get(`/user/${id}`),
  updateProfile: (data: any) => request.put("/user/profile", data),
  changePassword: (data: { oldPassword: string; newPassword: string }) => request.put("/user/password", data),
  updateAvatar: (imageUrl: string) => request.post("/user/avatar", null, { params: { image: imageUrl } }),
};
