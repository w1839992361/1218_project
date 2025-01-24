<script setup>
import { reactive, h, onMounted, ref } from 'vue';
import { RouterView, useRouter, useRoute } from 'vue-router';
import {
    FileAddOutlined,
    UploadOutlined,UnorderedListOutlined,UserOutlined,ContainerOutlined,AreaChartOutlined,DownloadOutlined
} from '@ant-design/icons-vue';
const state = reactive({
    selectedKeys: ['Contents'],
});

const route = useRoute();

const router = useRouter();
const items = ref([
    {
        key: 'Statistics',
        icon: () => h(AreaChartOutlined),
        label: '资源统计',
        title: 'Statistics',
    },
    {
        key: 'Contents',
        icon: () => h(FileAddOutlined),
        label: '资源管理',
        title: 'Contents',
    },
    {
      key: 'Column',
      icon: () => h(UnorderedListOutlined),
      label: '栏目管理',
      title: 'Column',
    },
    {
      key: 'Users',
      icon: () => h(UserOutlined),
      label: '用户管理',
      title: 'Users',
    },
    {
      key: 'Logs',
      icon: () => h(ContainerOutlined),
      label: '操作日志',
      title: 'Logs',
    },
    {
        key: 'DataGet',
        icon: () => h(DownloadOutlined),
        label: '下载增量',
        title: 'DataGet',
    },
    {
      key: 'DataUpdate',
      icon: () => h(UploadOutlined),
      label: '更新增量',
      title: 'DataUpdate',
    },
]);

onMounted(() => {
    state.selectedKeys = [route.name];
})

function handleClick({ item }) {
    router.push({ name: item.title })
}
</script>
<template>
    <div class="w-[100%] h-[100%] flex">
        <div class="w-[226px] h-[100%]">
            <div @click="router.push({ name: 'home' })"
                class="box-border p-5 h-[60px] cursor-pointer flex items-center justify-center bg-[#FFF] text-[#3c3c3c]">
                <SvgIcon class="mr-2" icon="logo" width="60" height="60" />
                <h2 class="text-[#242936] font-bold">后台管理系统</h2>
            </div>
            <a-menu class="h-[calc(100%-60px)]" @click="handleClick" v-model:selectedKeys="state.selectedKeys"
                mode="inline" :items="items"></a-menu>
        </div>

        <div class="scrollbar w-[calc(100%-226px)] m-3 p-3 bg-[#fff]  overflow-y-scroll">
            <Suspense>
                <RouterView />
            </Suspense>
        </div>
    </div>
</template>

<style scoped>

</style>