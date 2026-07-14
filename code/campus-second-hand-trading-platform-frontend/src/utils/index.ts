export function formatPrice(price: number): string {
  return "¥" + (price ?? 0).toFixed(2);
}
export function formatDate(date: string): string {
  if (!date) return "";
  return new Date(date).toLocaleString("zh-CN");
}
export function getConditionLabel(c: number): string {
  return ({ 0: "全新", 1: "几乎全新", 2: "有使用痕迹" } as any)[c] ?? "未知";
}
export function getOrderStatusLabel(s: number): string {
  return ({ 0: "待付款", 1: "待发货", 2: "待收货", 3: "已完成", 4: "已取消" } as any)[s] ?? "未知";
}
export function getOrderStatusType(s: number): "warning" | "primary" | "info" | "success" | "danger" {
  return ({ 0: "warning", 1: "primary", 2: "info", 3: "success", 4: "danger" } as any)[s] ?? "info";
}
export function getProductStatusLabel(s: number): string {
  return ({ 0: "待审核", 1: "在售", 2: "已售出", 3: "已下架" } as any)[s] ?? "未知";
}
export function getProductStatusType(s: number): "warning" | "success" | "info" | "danger" {
  return ({ 0: "warning", 1: "success", 2: "info", 3: "danger" } as any)[s] ?? "info";
}
export function formatDateTime(d: string): string {
  if (!d) return "";
  const t = new Date(d);
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${t.getFullYear()}-${pad(t.getMonth()+1)}-${pad(t.getDate())} ${pad(t.getHours())}:${pad(t.getMinutes())}`;
}
