<script setup>
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getAllTree } from '@/api/admin/content';
import { Input } from 'ant-design-vue';
import { SearchOutlined } from '@ant-design/icons-vue';

const navs = ref([]);

async function fetchNavs() {
  const { data, code } = await getAllTree();
  if (code === 200) {
    const items = data.map(item => ({
      id: item.id,
      name: item.name,
      routeName: item.name === '课程教学' ? 'course' : 'subject'
    }));
    navs.value = [{ id: 0, name: "首页", routeName: 'home' }, ...items];
  }
}

fetchNavs();

const router = useRouter();
const route = useRoute();

const currentId = computed(() => parseInt(route.query.id || 0));

const isActive = (navId) => navId === currentId.value;

function navigateTo(nav) {
  router.push({ name: nav.routeName, query: { id: nav.id }});
}

const keyword = ref('');

function handleSearch() {
  console.log(keyword.value);
  // Implement your search logic here
}
</script>

<template>
  <nav class="bg-white shadow-md">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <div class="flex">
          <div class="flex-shrink-0 flex items-center">
            <!-- You can add your logo here -->
<!--            <img class="h-8 w-auto" src="/your-logo.png" alt="Your Logo" />-->
          </div>
          <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
            <a
                v-for="item in navs"
                :key="item.id"
                :class="[
                'inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium',
                isActive(item.id)
                  ? 'border-blue-500 text-gray-900'
                  : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
              ]"
                @click="navigateTo(item)"
            >
              {{ item.name }}
            </a>
          </div>
        </div>
        <div class="flex items-center">
          <a-input-search
              v-model:value="keyword"
              placeholder="请输入关键字"
              style="width: 200px"
              @search="handleSearch"
          >
            <template #enterButton>
              <SearchOutlined />
            </template>
          </a-input-search>
        </div>
      </div>
    </div>

    <!-- Mobile menu, show/hide based on menu state. -->
    <div class="sm:hidden" id="mobile-menu">
      <div class="pt-2 pb-3 space-y-1">
        <a
            v-for="item in navs"
            :key="item.id"
            :class="[
            'block pl-3 pr-4 py-2 border-l-4 text-base font-medium',
            isActive(item.id)
              ? 'bg-blue-50 border-blue-500 text-blue-700'
              : 'border-transparent text-gray-500 hover:bg-gray-50 hover:border-gray-300 hover:text-gray-700'
          ]"
            @click="navigateTo(item)"
        >
          {{ item.name }}
        </a>
      </div>
    </div>
  </nav>
</template>

<style scoped>
/* Any additional custom styles can be added here */
</style>