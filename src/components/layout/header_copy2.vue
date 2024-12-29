<script setup>
import {ref, watchEffect} from "vue";
import {useRouter, useRoute} from "vue-router";
import {getAllTree} from '@/api/admin/content';

const navs = ref([{key: "home", name: "首页"}]);
const current = ref([]);

async function fetchNavs() {
  const {data, code} = await getAllTree();
  if (code === 200) {
    const keys = {'课程教学': 'course'};
    const items = data.map(item => ({
      key: keys[item.name] ? `${keys[item.name]}?course=${item.id}` : `subject?subject=${item.id}`,
      name: item.name
    }));
    navs.value = [{key: "home", name: "首页"}, ...items];
  }
}

fetchNavs();

const router = useRouter();
const route = useRoute();

watchEffect(() => {
  const match = navs.value.find(nav => route.fullPath.includes(nav.key));
  if (match) {
    current.value = [match.key];
  }
});

function handleNavSelection(nav) {
  const [path, params] = nav.key.split("?");
  const query = Object.fromEntries(new URLSearchParams(params));
  router.push({name: path, query});
}

const keyword = ref('');

function handleSearch(keyword) {
  console.log(keyword)
}
</script>

<template>
  <div class="warp">
    <div class="top">
      <a-row :gutter="16" class="w-[100%]">
        <a-col :span="18">
          <a-menu @click="handleNavSelection" class="w-[100%]" v-model:selectedKeys="current" mode="horizontal">
            <a-menu-item :key="item.key" v-for="item in navs">
              <h3>{{ item.name }}</h3>
            </a-menu-item>
          </a-menu>
        </a-col>
        <a-col :span="5" class="flex items-center">
          <a-input-search v-model="keyword" placeholder="请输入关键字" enter-button @search="handleSearch"/>
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
  height: 50px;
  background-color: #fff;
  position: relative;
  display: flex;
  align-items: end;
  width: 1368px;
  margin: 0 auto;
}
</style>
