<script setup>
import { reactive, h, onMounted, ref } from 'vue';
import { RouterView, useRouter, useRoute } from 'vue-router';
import {
    FileAddOutlined,
    DatabaseOutlined,
} from '@ant-design/icons-vue';
const state = reactive({
    selectedKeys: ['Contents'],
});

const route = useRoute();

const router = useRouter();
const items = ref([
    {
        key: 'Contents',
        icon: () => h(FileAddOutlined),
        label: '内容管理(教育局使用)',
        title: 'Contents',
    },
    {
        key: 'DataUpdate',
        icon: () => h(DatabaseOutlined),
        label: '数据上传(学校使用)',
        title: 'DataUpdate',
    },
    {
        key: 'DataGet',
        icon: () => h(DatabaseOutlined),
        label: '数据同步(教育局使用)',
        title: 'DataGet',
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
                class="h-[60px] cursor-pointer flex items-center justify-center bg-[#FFF] text-[#3c3c3c]">
                <SvgIcon icon="logo" width="60" height="60" />
                <h2>后台管理系统</h2>
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
/* 创建复杂的自定义滚动条 */
.scrollbar::-webkit-scrollbar {
    width: 10px;
}

/* 定义滚动条轨道 */
.scrollbar::-webkit-scrollbar-track {
    background: transparent;

}

/* 定义滚动条滑块 */
.scrollbar::-webkit-scrollbar-thumb {
    background: #ccc;
    border-radius: 9px;
}

/* 利用伪元素添加高光效果 */
.scrollbar::-webkit-scrollbar-thumb::after {
    content: "";
}
</style>