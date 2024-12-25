<script setup>
import { RouterView, useRouter } from 'vue-router';
import zhCN from 'ant-design-vue/es/locale/zh_CN';
// const router = useRouter();
const locale = zhCN;

// router.beforeEach((to, from, next) => {
//   var Document = document;
//   if (to.meta.title) {
//     Document.title = to.meta.title;
//   }
//   next();
// });
</script>

<template>
  <a-config-provider :locale="locale">
    <RouterView />
  </a-config-provider>
</template>

<style scoped></style>
