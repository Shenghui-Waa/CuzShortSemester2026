import request from "./request";

export const chatApi = {
  contacts: () => request.get("/chat/contacts"),
  messages: (contactId: number, page: number, pageSize: number) =>
    request.get(`/chat/${contactId}`, { params: { page, pageSize } }),
  send: (data: any) => request.post("/chat/send", data),
  markRead: (contactId: number) => request.put(`/chat/read/${contactId}`),
};
