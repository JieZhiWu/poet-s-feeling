import { MenuDataItem } from "@ant-design/pro-layout";
import { CrownOutlined, HomeOutlined, BookOutlined, ReadOutlined, EditOutlined } from "@ant-design/icons";
import ACCESS_ENUM from "@/access/accessEnum";

// 菜单列表
export const menus = [
  {
    path: "/",
    name: "主页",
    icon: <HomeOutlined />,
  },
  {
    path: "/poem",
    name: "风之札记",
    icon: <EditOutlined />,
  },
  {
    path: "/banks",
    name: "诗人图谱",
    icon: <ReadOutlined />,
  },
  {
    path: "/questions",
    name: "诗歌宝库",
    icon: <BookOutlined />,
  },
  {
    path: "/admin",
    name: "管理",
    icon: <CrownOutlined />,
    access: ACCESS_ENUM.ADMIN,
    children: [
      {
        path: "/admin/user",
        name: "用户管理",
        access: ACCESS_ENUM.ADMIN,
      },
      {
        path: "/admin/bank",
        name: "诗人分类管理",
        access: ACCESS_ENUM.ADMIN,
      },
      {
        path: "/admin/question",
        name: "诗歌管理",
        access: ACCESS_ENUM.ADMIN,
      },
    ],
  },
] as MenuDataItem[];

// 根据全部路径查找菜单
export const findAllMenuItemByPath = (path: string): MenuDataItem | null => {
  return findMenuItemByPath(menus, path);
};

// 根据路径查找菜单（递归）
export const findMenuItemByPath = (
  menus: MenuDataItem[],
  path: string,
): MenuDataItem | null => {
  for (const menu of menus) {
    if (menu.path === path) {
      return menu;
    }
    if (menu.children) {
      const matchedMenuItem = findMenuItemByPath(menu.children, path);
      if (matchedMenuItem) {
        return matchedMenuItem;
      }
    }
  }
  return null;
};
