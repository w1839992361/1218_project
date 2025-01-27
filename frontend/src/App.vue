<script setup>
import { RouterView, useRouter } from 'vue-router';
import { ref, watch, onMounted } from "vue";
import {getInfo } from '@/api/login';
import zhCN from 'ant-design-vue/es/locale/zh_CN';
import { useUserStore } from "@/stores/user";
const router = useRouter();
const locale = zhCN;
const userStore = useUserStore();
const userInfo = ref(userStore.userInfo);

const protectedRoutes = [
    '/manage/contents',
    '/manage/column',
    '/manage/users',
    '/manage/logs',
    '/manage/dataGet',
    '/manage/statistics',
    '/manage/dataUpdate'
];

router.beforeEach(async (to, from, next) => {
   try {
       if (to.path === '/login') {
           next();
           return;
       }
     // 记得删除 TODO
     next();
       // const user = await getInfo();
       // userStore.setUserInfo(user.data);
       // const userRole = user.data.role;
       // if (protectedRoutes.includes(to.path) && !(userRole === 'root' || userRole === 'admin')) {
       //     next('/login');
       // } else {
       //     next();
       // }
   } catch (error) {
       if (to.path !== '/login') {
           next('/login');
       } else {
           next();
       }
   }
});
</script>

<template>
  <a-config-provider :locale="locale">
    <RouterView />
  </a-config-provider>
</template>

<style scoped></style>
