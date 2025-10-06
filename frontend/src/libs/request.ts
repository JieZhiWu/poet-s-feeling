import axios from "axios";

// 判断是否在浏览器环境
const isClient = typeof window !== "undefined";

// 按需引入 message（只在客户端用）
let message: any = null;
if (isClient) {
  // 只有浏览器环境才引入 antd 的 message
  // @ts-ignore
  message = require("antd").message;
}

const DEV_BASE_URL : string = "http://localhost:8101";
// const PROD_BASE_URL : string = "http://124.223.189.45";

// 创建 Axios 示例
const myAxios = axios.create({
  baseURL: DEV_BASE_URL,
  timeout: 60000,
  withCredentials: true,
});

// 请求拦截器
myAxios.interceptors.request.use(
  function (config) {
    // 请求执行前执行
    return config;
  },
  function (error) {
    // 处理请求错误
    return Promise.reject(error);
  },
);

// 响应拦截器
myAxios.interceptors.response.use(
  function (response) {
    const { data } = response;

    // 未登录
    if (data.code === 40100 && isClient) {
      const requestURL = response.request?.responseURL;
      const isGetLoginApi =
        typeof requestURL === "string" && requestURL.includes("user/get/login");
      const isLoginPage = window.location.pathname.includes("/user/login");

      if (!isGetLoginApi && !isLoginPage) {
        window.location.href = `/user/login?redirect=${encodeURIComponent(
          window.location.href
        )}`;
      }
    } else if (data.code !== 0) {
      if (isClient && message) {
        message.error(data.message ?? "服务器错误");
      }
      throw new Error(data.message ?? "服务器错误");
    }
    return data;
  },
  function (error) {
    if (isClient && !error.response && message) {
      // 网络层错误（如服务未启动、CORS、超时）
      console.error("网络错误，请检查后端是否启动:", error.message);
      // message.error("连接失败，请检查后端服务是否运行（如 localhost:8101）");
    }
    return Promise.reject(error);
  },
);

export default myAxios;
export const request = myAxios;