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

router.beforeEach(async (to, from, next) => {
   const user = await getInfo();
   userStore.setUserInfo(user.data);
   next();
});
</script>

<template>
  <a-config-provider :locale="locale">
    <RouterView />
  </a-config-provider>
</template>

<style scoped></style>
