import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import WindiCSS from 'vite-plugin-windicss'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())

  return {
    // base 路径不影响开发环境
    base: mode === "production" ?  "/" : "/",

    plugins: [
      vue(),
      vueDevTools(),
      WindiCSS()
    ],

    server: {
      proxy: {
        // 仅代理以 VITE_API_BASE_URL 开头的请求
        [`${env.VITE_API_BASE_URL}`]: {
          target: env.VITE_API_TARGET_URL, // 目标地址
          changeOrigin: true, // 允许跨域
          secure: false, // 如果使用的是 HTTPS 并且自签证书，设为 false
          rewrite: (path) => path.replace(new RegExp(`^${env.VITE_API_BASE_URL}`), ''),
        },
      },
    },

    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      },
    }
  }
})
