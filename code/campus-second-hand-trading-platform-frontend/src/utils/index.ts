export function formatPrice(price: number): string {
  return "¥" + (price ?? 0).toFixed(2);
}
export function formatDate(date: string): string {
  if (!date) return "";
  return new Date(date).toLocaleString("zh-CN");
}
export function getConditionLabel(condition: number): string {
  const map: Record<number, string> = { 1: "全新", 2: "几乎全新", 3: "有使用痕迹" };
  return map[condition] ?? "未知";
}
export function getOrderStatusLabel(status: number): string {
  const map: Record<number, string> = { 0: "待付款", 1: "待发货", 2: "待收货", 3: "已完成", 4: "已取消" };
  return map[status] ?? "未知";
}
export function getOrderStatusType(status: number): string {
  const map: Record<number, string> = { 0: "warning", 1: "primary", 2: "info", 3: "success", 4: "danger" };
  return map[status] ?? "info";
}
