<script setup>
import { reactive, h, onMounted, ref } from 'vue';
import { RouterView, useRouter, useRoute } from 'vue-router';
import {
  FileAddOutlined,
  UploadOutlined,
  UnorderedListOutlined,
  UserOutlined,
  ContainerOutlined,
  AreaChartOutlined,
  DownloadOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined
} from '@ant-design/icons-vue';

const state = reactive({
  selectedKeys: ['Contents'],
  collapsed: true
});

const route = useRoute();
const router = useRouter();

const items = ref([
  { key: 'Statistics', icon: () => h(AreaChartOutlined), label: '资源统计', title: 'Statistics' },
  { key: 'Contents', icon: () => h(FileAddOutlined), label: '资源管理', title: 'Contents' },
  { key: 'Users', icon: () => h(UserOutlined), label: '用户管理', title: 'Users' },
  { key: 'Logs', icon: () => h(ContainerOutlined), label: '操作日志', title: 'Logs' },
  { key: 'DataGet', icon: () => h(DownloadOutlined), label: '更新增量', title: 'DataGet' },
  { key: 'DataUpdate', icon: () => h(UploadOutlined), label: '上传增量', title: 'DataUpdate' },
  { key: 'Column', icon: () => h(UnorderedListOutlined), label: '栏目管理', title: 'Column' },
]);

onMounted(() => {
  state.selectedKeys = [route.name];
})

function handleClick({ item }) {
  router.push({ name: item.title })
}

function toggleCollapsed() {
  state.collapsed = !state.collapsed;
}
</script>

<template>
  <div class="flex h-screen bg-gray-100">
    <!-- Sidebar -->
    <div :class="['bg-white shadow-lg transition-all duration-300', state.collapsed ? 'w-20' : 'w-64']">
      <!-- Logo -->
      <div @click="router.push({ name: 'home' })"
           class="h-16 flex items-center justify-center bg-gradient-to-r from-blue-500 to-indigo-600 cursor-pointer transition-all duration-300 hover:from-blue-600 hover:to-indigo-700">
        <SvgIcon class="mr-2" icon="logo" width="40" height="40" />
        <h2 v-if="!state.collapsed" class="text-white font-bold text-xl">后台管理系统</h2>
      </div>
      <!-- Menu -->
      <a-menu
          class="h-[calc(100%-4rem)] border-r-0"
          @click="handleClick"
          v-model:selectedKeys="state.selectedKeys"
          mode="inline"
          :inline-collapsed="state.collapsed"
          :items="items"
      >
        <template #icon="{ icon }">
          <component :is="icon" class="text-gray-600" />
        </template>
      </a-menu>
    </div>

    <!-- Main Content -->
    <div class="flex-1 overflow-hidden">
      <!-- Top Bar -->
      <div class="h-16 bg-white shadow-sm flex items-center px-6">
        <button @click="toggleCollapsed" class="mr-4 text-gray-600 hover:text-blue-600 transition-colors duration-200">
          <MenuUnfoldOutlined v-if="state.collapsed" />
          <MenuFoldOutlined v-else />
        </button>
        <h1 class="text-xl font-semibold text-gray-800">{{ route.name }}</h1>
      </div>
      <!-- Content Area -->
      <div class="p-6 h-[calc(100%-4rem)] overflow-y-auto">
        <div class="bg-white rounded-lg shadow-sm p-6">
          <Suspense>
            <RouterView />
            <template #fallback>
              <div class="flex justify-center items-center h-64">
                <a-spin size="large" />
              </div>
            </template>
          </Suspense>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.a-menu {
  @apply text-gray-600;
}

.a-menu-item {
  @apply transition-colors duration-200;
}

.a-menu-item:hover {
  @apply bg-blue-50 text-blue-600;
}

.a-menu-item-selected {
  @apply bg-blue-100 text-blue-600 font-medium;
}

/* Custom scrollbar styles */
.overflow-y-auto {
  scrollbar-width: thin;
  scrollbar-color: #CBD5E0 #EDF2F7;
}

.overflow-y-auto::-webkit-scrollbar {
  width: 8px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #EDF2F7;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: #CBD5E0;
  border-radius: 4px;
}
</style>