export function formatPrice(price: number): string {
  return "¥" + (price ?? 0).toFixed(2);
}

export function parseApiDate(value: string): Date | null {
  if (!value) return null;

  const trimmed = value.trim();
  const hasTimezone = /(?:Z|[+-]\d{2}:?\d{2})$/i.test(trimmed);
  const normalized = trimmed.includes("T")
    ? trimmed
    : trimmed.replace(" ", "T");
  const isoValue = hasTimezone ? normalized : `${normalized}Z`;
  const date = new Date(isoValue);

  return Number.isNaN(date.getTime()) ? null : date;
}

export function formatDate(date: string): string {
  const parsed = parseApiDate(date);
  return parsed ? parsed.toLocaleString("zh-CN") : "";
}

export function getConditionLabel(c: number): string {
  return ({ 1: "全新", 2: "几乎全新", 3: "有使用痕迹" } as any)[c] ?? "未知";
}

export function getOrderStatusLabel(s: number): string {
  return ({ 0: "待付款", 1: "待发货", 2: "待收货", 3: "已完成", 4: "已取消" } as any)[s] ?? "未知";
}

export function getOrderStatusType(
  s: number,
): "warning" | "primary" | "info" | "success" | "danger" {
  return ({
    0: "warning",
    1: "primary",
    2: "info",
    3: "success",
    4: "danger",
  } as any)[s] ?? "info";
}

export function getProductStatusLabel(s: number): string {
  return ({ 0: "待审核", 1: "在售", 2: "已售出", 3: "已下架" } as any)[s] ?? "未知";
}

export function getProductStatusType(
  s: number,
): "warning" | "success" | "info" | "danger" {
  return ({
    0: "warning",
    1: "success",
    2: "info",
    3: "danger",
  } as any)[s] ?? "info";
}

export function formatDateTime(value: string): string {
  const date = parseApiDate(value);
  if (!date) return "";

  const pad = (number: number) => String(number).padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(
    date.getDate(),
  )} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
}
