/** 侧栏菜单的路径匹配规则（纯函数，无 React 依赖，便于验证）。 */

export type NavSubItem = {
  name: string;
  path: string;
  pro?: boolean;
  new?: boolean;
  /** 归属该子菜单、但路径不以 path 开头的路由前缀（如 /ai/prompt-templates/:id 归属提示词管理） */
  matchPaths?: string[];
};

/**
 * 精确匹配或子路径匹配：详情页（/xxx/:id）也要把父级菜单锚定住。
 * 要求 `/` 边界，`/article-categories` 不会误命中 `/articles`。
 */
export function isNavPathActive(pathname: string, path: string): boolean {
  return pathname === path || pathname.startsWith(`${path}/`);
}

/** 子菜单是否命中当前路径：自身 path，或它声明的 matchPaths 前缀。 */
export function isSubNavActive(pathname: string, subItem: NavSubItem): boolean {
  return (
    isNavPathActive(pathname, subItem.path) ||
    (subItem.matchPaths ?? []).some((path) => isNavPathActive(pathname, path))
  );
}

/**
 * 当前路径命中的子菜单分组；无命中返回 null（父级菜单收起）。
 * 侧栏展开态只认这一个结果，详情页 /xxx/:id 才能一直挂在父级菜单上。
 */
export function findOpenSubmenu(
  items: { subItems?: NavSubItem[] }[],
  pathname: string,
): { index: number } | null {
  for (let index = 0; index < items.length; index += 1) {
    if (items[index].subItems?.some((sub) => isSubNavActive(pathname, sub))) {
      return { index };
    }
  }
  return null;
}
