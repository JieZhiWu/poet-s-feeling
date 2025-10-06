"use client";
import React, { useCallback, useEffect } from "react";
import { AntdRegistry } from "@ant-design/nextjs-registry";
import "./globals.css";
import BasicLayout from "@/layouts/BasicLayout";
import { Provider, useDispatch } from "react-redux";
import store, { AppDispatch } from "@/stores";
import { setLoginUser } from "@/stores/loginUser";
import { getLoginUserUsingGet } from "@/api/userController";
import AccessLayout from "@/access/AccessLayout";
import ACCESS_ENUM from "@/access/accessEnum";

/**
 * 初始化布局（多封装一层，使得能调用 useDispatch）
 * @param children
 * @constructor
 */
const InitLayout: React.FC<
  Readonly<{
    children: React.ReactNode;
  }>
> = ({ children }) => {
  const dispatch = useDispatch<AppDispatch>();
  // 初始化全局用户状态
  const doInitLoginUser = useCallback(async () => {
    // 获取用户信息
    const res = await getLoginUserUsingGet();
    if (res.data) {
      // 更新全局用户状态
      // @ts-ignore
      dispatch(setLoginUser(res.data));
    } else {
      // todo 测试代码，实际可删除
      /*
            setTimeout(() => {
              const testUser = {
                userName: "登录",
                id: 1,
                userRole: ACCESS_ENUM.ADMIN,
              };
              dispatch(setLoginUser(testUser));
            }, 3000);
      */
    }
  }, []);

  useEffect(() => {
    doInitLoginUser();
  }, []);

  return <>{children}</>;
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="zh">
      <head>
        <title>芥雾浮心 - 在线诗歌平台</title>
        <meta name="description" content="品味诗歌意境，感受文字之美，在诗意中静心成长" />
      </head>
      <body>
        <AntdRegistry>
          <Provider store={store}>
            <InitLayout>
              <BasicLayout>
                <AccessLayout>{children}</AccessLayout>
              </BasicLayout>
            </InitLayout>
          </Provider>
        </AntdRegistry>
      </body>
    </html>
  );
}
