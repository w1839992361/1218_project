import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import WindiCSS from 'vite-plugin-windicss'
// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    WindiCSS()
  ],

  server: {
    proxy: {
      [`^${import.meta.env.VITE_API_BASE_URL}`]: {
        target: import.meta.env.VITE_API_BASE_URL, // 使用环境变量中的 API 基础 URL
        changeOrigin: true,
        rewrite: (path) => path.replace(new RegExp(`^${import.meta.env.VITE_API_BASE_URL}`), ''),
      },
    },
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  }
})
