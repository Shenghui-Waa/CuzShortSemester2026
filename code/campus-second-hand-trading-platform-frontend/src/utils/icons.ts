import {
  Goods, Monitor, Phone, Headset, ShoppingBag, MagicStick,
  Watch, Reading, Notebook, School, Basketball, Burger,
  Van, House, Present, MoreFilled
} from "@element-plus/icons-vue";

const iconMap: Record<string, any> = {
  "商品": Goods, "电子": Monitor, "手机": Phone, "数码": Headset,
  "服饰": ShoppingBag, "美妆": MagicStick, "手表": Watch, "图书": Reading,
  "文具": Notebook, "学习": School, "运动": Basketball, "食品": Burger,
  "交通": Van, "家居": House, "礼品": Present, "其他": MoreFilled,
};

export function getCategoryIcon(name?: string) {
  if (!name) return Goods;
  return iconMap[name] || Goods;
}
