import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import WindiCSS from 'vite-plugin-windicss'
import dotenv from 'dotenv';

dotenv.config(); // 加载 .env 文件
// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    WindiCSS()
  ],

  server: {
    proxy: {
      [`^${process.env.VITE_API_BASE_URL}`]: {
        target: process.env.VITE_API_TARGET_URL, // 目标地址
        changeOrigin: true,
        rewrite: (path) =>
          path.replace(new RegExp(`^${process.env.VITE_API_BASE_URL}`), ''),
      },
    },
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  }
})
