import { existsSync } from "node:fs";
import { resolve } from "node:path";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import svgr from "vite-plugin-svgr";

// https://vite.dev/config/
export default defineConfig(({ command, mode }) => {
  // 构建产物里的后端地址是编译期固化的。缺 .env.<mode> 时 Vite 不报错，
  // VITE_ADMIN_API_BASE 会落到 .env.local（本机地址）或 client.ts 的 localhost:8080 兜底，
  // 打出来的包在服务器上所有接口都连不上——所以 build 前强制要求该文件存在。
  // 校验的是文件而不是变量取值：.env.local 在任何 mode 都会加载，只查变量有值会被它蒙混过去。
  if (command === "build" && !existsSync(resolve(__dirname, `.env.${mode}`))) {
    throw new Error(
      `缺少 love-space-web/.env.${mode}：请先按同目录 .env.${mode}.example 创建并填值，` +
        `否则产物里的 VITE_ADMIN_API_BASE 会指向本机地址。`,
    );
  }

  return {
    base: "/love-space/",
    plugins: [
      react(),
      svgr({
        svgrOptions: {
          icon: true,
          // This will transform your SVG to a React component
          exportType: "named",
          namedExport: "ReactComponent",
        },
      }),
    ],
  };
});
