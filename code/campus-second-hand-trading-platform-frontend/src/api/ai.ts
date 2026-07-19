import request from "./request";

export const aiApi = {
  chat: (message: string) => request.post("/ai/chat", { message }),
};
