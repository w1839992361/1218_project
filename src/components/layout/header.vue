<script setup>
import { ref, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { onMounted } from "vue";
import { getAllTree } from '@/api/admin/content';

const current = ref(["home"]);

const navs = ref([
  {
    key: "home",
    name: "首页",
  },
]);


async function getHeader() {
  const { data, code } = await getAllTree();
  if (code !== 200) return;
  let keys = {
    '课程教学': 'course',
  };
  let arr = data.map(item => {
    return {
      key: keys[item.name] ? (keys[item.name] + '?course=' + item.id) : ('subject?subject=' + item.id),
      ...item
    }
  });
  navs.value = navs.value.concat(arr);

  const matchedNav = navs.value.find((nav) => route.fullPath.includes(nav.key));
  if (matchedNav) {
    console.log(matchedNav)
    current.value = [matchedNav.key];
    route.meta.title = matchedNav.name; // 更新 meta.title
  }

}

getHeader();

const keyword = ref('');

const router = useRouter();
const route = useRoute();

watch(
  () => route.name,
  (newName) => {
    const matchedNav = navs.value.find((nav) => nav.key === newName);

    if (matchedNav) {
      current.value = [matchedNav.key];
    }
  }
);

onMounted(() => {
  const matchedNav = navs.value.find((nav) => route.fullPath.includes(nav.key));
  current.value = matchedNav ? [matchedNav.key] : [];
});

function selectedKeys({ key }) {
  let [routeName, queryString] = key.split('?');
  let query = queryString
    ? Object.fromEntries(new URLSearchParams(queryString))
    : {};
  router.push({ name: routeName, query });
}

function handleSearch() {
  console.log(keyword.value)
}

</script>

<template>
  <div class="warp">
    <div class="top">
      <a-row :gutter="16" class="w-[100%]">
        <a-col :span="18">
          <a-menu @click="selectedKeys" class="w-[100%]" v-model:selectedKeys="current" mode="horizontal">
            <a-menu-item :key="item.key" v-for="item in navs">
              <h3>{{ item.name }}</h3>
            </a-menu-item>
          </a-menu>

        </a-col>

        <a-col :span="5" class="flex items-center">
          <a-input-search v-model:value="keyword" placeholder="请输入关键字" enter-button @search="handleSearch" />
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<style scoped>
.warp {
  width: 100%;
  background: #fff;
}

.top {
  width: 100%;
  height: 50px;
  background-color: #fff;
  position: relative;
  display: flex;
  align-items: end;
  width: 1368px;
  margin: 0 auto;
}
</style>
