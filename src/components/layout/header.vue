<script setup>
import { ref, watch } from "vue";
import { UserOutlined } from "@ant-design/icons-vue";
import { useRouter, useRoute } from "vue-router";
import { onMounted } from "vue";
import { getAllTree } from '@/api/admin/content';
import { getInfo } from '@/api/login';
const current = ref(["home"]);

const navs = ref([
  {
    key: "home",
    name: "首页",
  },
]);

const info = ref();
const isLogin = ref(false);

getInfo().then(res => {
  isLogin.value = res.code === 200;
  info.value = res.data;
})

async function getHeader() {
  const { data ,code} = await getAllTree();
  if(code !== 200) return;
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
const baseUrl = import.meta.env.VITE_API_BASE_URL;
const imgUrl = baseUrl + '/api/avatar/stream/';
</script>

<template>
  <!-- <div class="header">
    <svg t="1734618016348" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
      p-id="4378" width="50" height="50">
      <path d="M512 512m-445 0a445 445 0 1 0 890 0 445 445 0 1 0-890 0Z" fill="#E6F3FF" p-id="4379"></path>
      <path
        d="M703.2 561.5c-4.6 0-8.3-3.7-8.3-8.3v-87.7c0-4.6 3.7-8.3 8.3-8.3s8.3 3.7 8.3 8.3v87.7c0 4.6-3.7 8.3-8.3 8.3z"
        fill="#2398F9" p-id="4380"></path>
      <path d="M723.1 666.4h-39.7V563c0-8.7 7-15.7 15.7-15.7h8.3c8.7 0 15.7 7 15.7 15.7v103.4z" fill="#B8DDFF"
        p-id="4381"></path>
      <path d="M388.9 501.9v136.4c0 29.4 54.8 63.8 122.3 63.8s122.3-34.3 122.3-63.8V501.9" fill="#22D0FF" p-id="4382">
      </path>
      <path
        d="M504.2 560.4l-211.5-99.7c-12.6-5.9-12.7-23.8-0.1-29.8l211.5-102.2c4.5-2.2 9.8-2.2 14.4 0L730 430.9c12.5 6 12.4 23.9-0.1 29.8l-211.5 99.7c-4.6 2.1-9.7 2.1-14.2 0z"
        fill="#148AF4" p-id="4383"></path>
    </svg>
    <h1>XX智慧教育平台</h1>
  </div> -->
  <div class="warp">
    <div class="top">
      <!-- <div class="center">
        <svg t="1734618125872" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
          p-id="12475" width="80" height="80">
          <path
            d="M819.2 695.9H690.3c-78.6 0-171.9 31-171.9 31v52.9h300.8v-83.9zM217.6 695.9h128.9c78.6 0 171.9 31 171.9 31v52.9H217.6v-83.9z"
            fill="#00E7DD" p-id="12476"></path>
          <path
            d="M820.3 259.7h-75.2v-60.2h-15c-2.3 0-4.3 0.6-6.3 1.5-2.8-0.9-5.7-1.5-8.8-1.5h-60.2c-59.4 0-120.3 15-120.3 15-5.5 0-10.6 1.6-15 4.2-4.4-2.6-9.5-4.2-15-4.2 0 0-60.9-15-120.3-15H324c-3.1 0-6 0.6-8.8 1.5-1.9-0.9-4-1.5-6.3-1.5h-15v60.2h-75.2c-8.3 0-15 6.7-15 15v496.4c0 8.3 6.7 15 15 15h122.2c71.4 0 155.9 11.7 174.7 14.5 1.3 0.3 2.6 0.6 3.9 0.6 1.4 0 2.7-0.2 3.9-0.6 18.8-2.7 103.3-14.5 174.7-14.5h122.2c8.3 0 15-6.7 15-15V274.7c0-8.3-6.7-15-15-15z m-586.6 30.1h60.2v376h-60.2v-376z m270.7 481.3s-62.8-15-137.3-15H233.6v-60.2h60.2V726h15c2.3 0 4.3-0.6 6.3-1.5 2.8 0.9 5.7 1.5 8.8 1.5h60.2c60.2 0 120.3 15 120.3 15v30.1z m0-75.2c0 8.3-6.7 15-15 15 0 0-52.6-15-105.3-15H339c-8.3 0-15-6.7-15-15V244.7c0-8.3 6.7-15 15-15h45.1c50.4 0 105.3 15 105.3 15 8.3 0 15 6.7 15 15v436.2z m30.1-436.2c0-8.3 6.7-15 15-15 0 0 54.9-15 105.3-15H700c8.3 0 15 6.7 15 15v436.2c0 8.3-6.7 15-15 15h-45.1c-52.6 0-105.3 15-105.3 15-8.3 0-15-6.7-15-15V259.7z m270.8 496.4H701.9c-74.4 0-167.3 15-167.3 15V741s60.2-15 120.3-15h60.2c3.1 0 6-0.6 8.8-1.5 1.9 0.9 4 1.5 6.3 1.5h15v-30.1h60.2v60.2z m0-90.3h-60.2v-376h60.2v376z"
            fill="#008CFF" p-id="12477"></path>
        </svg>
        <h1>XX智慧教育平台</h1>
      </div> -->

      <div class="login">
        <a-button v-if="!isLogin" @click="$router.push({ name: 'login' })" shape="round" type="primary" :ghost="true">
          <template #icon>
            <UserOutlined />
          </template>
          登录
        </a-button>
        <template v-else>
          <a-avatar :src="imgUrl + info.avatar" />
          <span class="ml-2">{{ info.username }}</span>
        </template>
      </div>

      <a-row :gutter="16" class="w-[100%]">
        <a-col :span="18">
          <a-menu @click="selectedKeys" class="w-[100%]" v-model:selectedKeys="current" mode="horizontal">
            <a-menu-item :key="item.key" v-for="item in navs">
              <h3>{{ item.name }}</h3>
            </a-menu-item>
          </a-menu>

        </a-col>

        <a-col :span="5">
          <a-input-search v-model:value="keyword" placeholder="请输入关键字" enter-button @search="handleSearch" />
        </a-col>
      </a-row>
      <!-- <div class="nav">
    
        <div class="nav_right">
        
        </div>
      </div> -->
    </div>
  </div>
</template>

<style scoped>
.header {
  width: 100%;
  height: 109px;
  background-color: #0356ca;
  display: flex;
  justify-content: start;
  align-items: center;
  padding: 20px 50px;
}

.header h1 {
  color: #fff;
  font-weight: bold;
}

.warp {
  width: 100%;
  background: #fff;
}

.top {
  width: 100%;
  height: 190px;
  background-color: #fff;
  position: relative;
  display: flex;
  align-items: end;
  width: 1368px;
  margin: 0 auto;
}

.center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login {
  position: absolute;
  top: 50%;
  right: 20px;
  transform: translate(-50%, -50%);
}

.nav {
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 0 50px 10px;
  width: 1368px;
}

.nav_left,
.nav_right {
  width: 50%;
}
</style>
