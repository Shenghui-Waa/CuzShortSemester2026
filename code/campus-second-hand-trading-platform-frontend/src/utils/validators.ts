export function validatePhone(_rule: any, value: string, cb: any) {
  if (!value) return cb();
  /^1[3-9]\d{9}$/.test(value) ? cb() : cb(new Error("手机号格式不正确"));
}
export function validateEmail(_rule: any, value: string, cb: any) {
  if (!value) return cb();
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) ? cb() : cb(new Error("邮箱格式不正确"));
}
