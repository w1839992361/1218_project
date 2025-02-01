<script setup>

import Preview from "@/components/preview/index.vue";
import {ref} from "vue";

const fileUrl = ref('');
const fileType = ref('');
const refreshKey = ref(1);
import { useRoute} from 'vue-router';

const baseUrl = import.meta.env.VITE_API_BASE_URL;
const route = useRoute();
handleResClick(route.query)

function handleResClick(item) {
  fileUrl.value = false;
  fileType.value = false;
  fileType.value = "mp4";
  fileUrl.value = baseUrl + '/api/videos/stream/' + item.id;
  refreshKey.value = Date.now();
}

</script>


<template>
  <h1 class="text-2xl">标题:{{route.query.title}}</h1>
  <Preview :key="refreshKey" v-if="fileUrl && fileType" :fileUrl="fileUrl" :fileType="fileType"/>
</template>


<style scoped>

</style>