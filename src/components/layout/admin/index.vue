<script setup>
import { reactive, watch, h } from 'vue';
import { RouterView, useRouter } from 'vue-router';
import {
    FileAddOutlined,
    UserOutlined,
    DatabaseOutlined,
} from '@ant-design/icons-vue';
const state = reactive({
    selectedKeys: ['1'],
});

const router = useRouter();
const items = reactive([
    {
        key: '1',
        icon: () => h(FileAddOutlined),
        label: '内容管理',
        title: 'Contents',
    },
    {
        key: '2',
        icon: () => h(UserOutlined),
        label: '用户管理',
        title: 'Users',
    },
    {
        key: '3',
        icon: () => h(DatabaseOutlined),
        label: '数据同步',
        title: 'DataUpdate',
    },
    // {
    //     key: 'sub1',
    //     icon: () => h(MailOutlined),
    //     label: 'Navigation One',
    //     title: 'Navigation One',
    //     children: [
    //         {
    //             key: '5',
    //             label: 'Option 5',
    //             title: 'Option 5',
    //         },
    //         {
    //             key: '6',
    //             label: 'Option 6',
    //             title: 'Option 6',
    //         },
    //         {
    //             key: '7',
    //             label: 'Option 7',
    //             title: 'Option 7',
    //         },
    //         {
    //             key: '8',
    //             label: 'Option 8',
    //             title: 'Option 8',
    //         },
    //     ],
    // },

]);

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
            <RouterView />
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