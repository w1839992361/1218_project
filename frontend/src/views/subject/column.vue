<script setup>
import { ref } from "vue";
import {
  BookOutlined,
  FileTextOutlined,
  BarChartOutlined,
  TeamOutlined,
  SettingOutlined,
  ReadOutlined,
} from "@ant-design/icons-vue";
import { useRouter, useRoute } from "vue-router";

const route = useRoute();
const router = useRouter();

import { getTagsById } from "@/api/other";
import {useVideosInfo} from "@/stores/video.js";

const baseUrl = import.meta.env.VITE_API_BASE_URL;
const imgUrl = baseUrl + "/api/covers/stream/";
const menuItems = ref([]);
const title = ref('');
const resources = ref([]);
const videoInfo = useVideosInfo();

async function getList() {
  const { data, code } = await getTagsById(route.query.id);
  if (code === 200) {
    menuItems.value = data[0]?.children ?? [];
    title.value = data[0]?.name;
    menuItems.value = menuItems.value.map((item) => {
      return {
        ...item,
        active: false,
      };
    });
    resources.value = menuItems.value[0]?.children ?? [];
    selectedKeys.value = route.query.selectId ?[+route.query.selectId] : [menuItems.value[0]?.id];
  }
}

getList();
const currentPage = ref(1);
const selectedKeys = ref([]);

const handleMenuClick = (info) => {
  selectedKeys.value = [info.key];
  resources.value = menuItems.value.find((m) => m.id === info.key)?.children ?? [];
};

const handleCardClick = (re) => {
    router.push({ name: "SubjectDetail" ,query:{...re}});
};
</script>

<template>
  <div class="flex min-h-screen bg-gray-100 overflow-hidden">
    <!-- Sidebar -->
    <div class="w-48 bg-white shadow flex-shrink-0">
      <div class="p-4">
        <h1 class="text-lg font-bold text-gray-800">{{title}}</h1>
      </div>
      <a-menu v-model:selectedKeys="selectedKeys" mode="inline" @click="handleMenuClick">
        <a-menu-item v-for="item in menuItems" :key="item.id">
          <template #icon>
            <component :is="item.icon" />
          </template>
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </div>

    <!-- Main Content -->
    <div class="flex-1 p-6 overflow-auto">
      <div class="grid gap-6">
        <!-- Card -->
        <a-card
          v-for="resource in resources"
          :key="resource.id"
          class="w-full overflow-hidden cursor-pointer"
          v-if="resources.length > 0"
          @click="handleCardClick(resource)"
        >
          <div class="flex flex-wrap w-full overflow-hidden">
            <!-- Image Section -->
            <div class="w-48 h-32 flex-shrink-0 overflow-hidden">
              <img
                :src="imgUrl + resource.coverUuid"
                :alt="resource.title"
                class="w-full h-full object-cover rounded"
              />
            </div>
            <div class="ml-4 flex flex-col flex-1 min-w-0 overflow-hidden">
              <h2 class="text-xl font-semibold text-gray-800 truncate">
                {{ resource.name }}
              </h2>

              <p class="text-gray-600 text-sm flex-1 multiline-ellipsis">
                {{ resource.description }}
              </p>

              <div class="mt-2 text-sm text-gray-500">
                浏览数：{{  videoInfo.getVideoData(resource.id)?.views }}
              </div>
            </div>
          </div>
        </a-card>

        <a-empty v-else :description="null" />
      </div>
    </div>
  </div>
</template>

<style>
.multiline-ellipsis {
  display: block;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  word-wrap: break-word;
  white-space: normal;
  max-height: 4.5em;
  line-height: 1.5em;
}
</style>
