import './assets/main.css'
import 'virtual:windi.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import Antd from 'ant-design-vue';
import JSZip from 'jszip';

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(JSZip)
app.use(Antd);

app.mount('#app')
