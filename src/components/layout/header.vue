<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getAllTree } from '@/api/admin/content';
import { Input } from 'ant-design-vue';
import { SearchOutlined, DownOutlined, MenuOutlined } from '@ant-design/icons-vue';

const navs = ref([]);
const visibleItems = ref([]);
const overflowItems = ref([]);
const navRef = ref(null);
const mobileMenuOpen = ref(false);

async function fetchNavs() {
  const { data, code } = await getAllTree();
  if (code === 200) {
    const items = data.map(item => ({
      id: item.id,
      name: item.name,
      routeName: item.name === '课程教学' ? 'course' : 'subject'
    }));
    navs.value = [{ id: 0, name: "首页", routeName: 'home' }, ...items];
    updateNavItems();
  }
}

onMounted(() => {
  fetchNavs();
  window.addEventListener('resize', updateNavItems);
});

onUnmounted(() => {
  window.removeEventListener('resize', updateNavItems);
});

function updateNavItems() {
  if (!navRef.value) return;

  const navWidth = navRef.value.offsetWidth;
  let totalWidth = 0;
  const visible = [];
  const overflow = [];

  navs.value.forEach((item, index) => {
    const itemWidth = 100; // Approximate width of each item
    if (totalWidth + itemWidth < navWidth - 200) { // Reserve space for "More" dropdown and search
      visible.push(item);
      totalWidth += itemWidth;
    } else {
      overflow.push(item);
    }
  });

  visibleItems.value = visible;
  overflowItems.value = overflow;
}

const router = useRouter();
const route = useRoute();

const currentId = computed(() => parseInt(route.query.id || 0));

const isActive = (navId) => navId === currentId.value;

function navigateTo(nav) {
  router.push({ name: nav.routeName, query: { id: nav.id }});
  showOverflowMenu.value = false;
  mobileMenuOpen.value = false;
}

const keyword = ref('');

function handleSearch() {
  console.log(keyword.value);
  // Implement your search logic here
}

const showOverflowMenu = ref(false);

function toggleOverflowMenu() {
  showOverflowMenu.value = !showOverflowMenu.value;
}

function toggleMobileMenu() {
  mobileMenuOpen.value = !mobileMenuOpen.value;
}
</script>

<template>
  <nav class="bg-white shadow-md" ref="navRef">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <div class="flex">
          <div class="flex-shrink-0 flex items-center">
            <!-- You can add your logo here -->
            <!-- <img class="h-8 w-auto" src="/your-logo.png" alt="Your Logo" /> -->
          </div>
          <div class="hidden md:ml-6 md:flex md:space-x-4">
            <a
              v-for="item in visibleItems"
              :key="item.id"
              :class="[
                'inline-flex items-center px-3 py-2 cursor-pointer  text-sm font-medium transition-colors duration-150 ease-in-out border-b-2',
                isActive(item.id)
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-700 hover:bg-blue-50 hover:text-blue-600'
              ]"
              @click="navigateTo(item)"
            >
              {{ item.name }}
            </a>
            <div v-if="overflowItems.length > 0" class="relative">
              <button
                @click="toggleOverflowMenu"
                class="inline-flex min-w-[90px] h-[100%] items-center px-3 py-2 rounded-md text-sm font-medium text-gray-700 hover:bg-blue-50 hover:text-blue-600 transition-colors duration-150 ease-in-out"
              >
                更多
                <DownOutlined class="ml-1" />
              </button>
              <div
                v-if="showOverflowMenu"
                class="absolute z-10 mt-2 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5"
              >
                <div class="py-1" role="menu" aria-orientation="vertical" aria-labelledby="options-menu">
                  <a
                    v-for="item in overflowItems"
                    :key="item.id"
                    :class="[
                      'inline-block w-[100%] text-center items-center cursor-pointer px-3 py-2 text-sm font-medium transition-colors duration-150 ease-in-out border-b-2',
                      isActive(item.id)
                        ? 'border-blue-500 text-blue-600'
                        : 'border-transparent text-gray-700 hover:bg-blue-50 hover:text-blue-600'
                    ]"
                    @click="navigateTo(item)"
                    role="menuitem"
                  >
                    {{ item.name }}
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="flex items-center">
          <div class="hidden md:block">
            <a-input-search
              v-model:value="keyword"
              placeholder="请输入关键字"
              class="w-64"
              @search="handleSearch"
            >
              <template #enterButton>
                <SearchOutlined />
              </template>
            </a-input-search>
          </div>
          <button
            @click="toggleMobileMenu"
            class="md:hidden inline-flex items-center justify-center p-2 rounded-md text-gray-700 hover:text-blue-500 hover:bg-blue-50 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-blue-500"
          >
            <MenuOutlined class="h-6 w-6" />
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile menu -->
    <div
      :class="['md:hidden', mobileMenuOpen ? 'block' : 'hidden']"
      id="mobile-menu"
    >
      <div class="px-2 pt-2 pb-3 space-y-1">
        <a
          v-for="item in navs"
          :key="item.id"
          :class="[
            'block px-3 py-2 rounded-md text-base font-medium cursor-pointer transition-colors duration-150 ease-in-out',
            isActive(item.id)
              ? 'bg-blue-500 text-white'
              : 'text-gray-700 hover:bg-blue-50 hover:text-blue-600'
          ]"
          @click="navigateTo(item)"
        >
          {{ item.name }}
        </a>
      </div>
      <div class="px-2 pt-2 pb-3">
        <a-input-search
          v-model:value="keyword"
          placeholder="请输入关键字"
          class="w-full"
          @search="handleSearch"
        >
          <template #enterButton>
            <SearchOutlined />
          </template>
        </a-input-search>
      </div>
    </div>
  </nav>
</template>

<style scoped>
/* Prevent text wrapping */
.md\:flex > a {
  white-space: nowrap;
}
</style>