import 'virtual:windi.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import Antd from 'ant-design-vue';
import './assets/main.css';

import SvgIcon from "@/components/icon/index.vue"; // 引入插件

const app = createApp(App)

app.use(createPinia())
app.component("SvgIcon", SvgIcon);
app.use(router)
app.use(Antd);

app.mount('#app')
