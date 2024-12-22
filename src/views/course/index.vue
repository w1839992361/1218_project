<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { PlayCircleOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
const router = useRouter();
const route = useRoute();

const selectedKeys = ref(['course']);

const courseTitle = ref([
    {
        name: '学科课程',
        key: 'course',
        src: new URL('@/assets/images/course/hat.png', import.meta.url).href
    },
    {
        name: '拓展课程',
        key: 'extensions',
        src: new URL('@/assets/images/course/dashboard.png', import.meta.url).href
    }
]);

const currentKeys = ref(1);
const expandIconPosition = ref('right');
onMounted(() => {
    selectedKeys.value = route.query.type ? [route.query.type] : ['course'];
})


const sections = ref([
    {
        name: '一单元',
        key: 1,
        children: [
            {
                name: "阅读",
                key: 1,
                children: [
                    {
                        name: "1 春",
                        key: 1,
                        children: ["刘红霞[第一课时]", "刘红霞[第二课时]", "王聪聪"],
                    },
                    {

                        name: "2 济南的冬天",
                        key: 2,
                        children: ["夏建慧", "张小英"],
                    },
                ],
            },
            {
                name: "写作",
                key: 2,
                children: [
                    {
                        name: "热爱生活，热爱写作",
                        key: 1,
                        children: ["热爱生活 - 张三"],
                    },
                ],
            },
        ]
    },
    {
        name: '二单元',
        key: 2,
        children: []
    },
]);



function download(item) {
    message.success('success!');
}

</script>

<template>
    <a-menu :inlineCollapsed="false" style="width:100%;" v-model:selectedKeys="selectedKeys"
        mode="horizontal">
        <a-menu-item :key="item.key" v-for="item in courseTitle">
            <template #icon>
                <img :src="item.src" width="50px" alt="">
            </template>
            <h3>{{ item.name }}</h3>
        </a-menu-item>
    </a-menu>

    <a-card class="tag">
        <div class="tag-item">
            <a-button type="like" shape="round">学段:</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="primary" shape="round">一年级</a-button>
        </div>

        <div class="tag-item">
            <a-button type="like" shape="round">学段:</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="primary" shape="round">一年级</a-button>
        </div>

        <div class="tag-item">
            <a-button type="like" shape="round">学段:</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="primary" shape="round">一年级</a-button>
        </div>
        <div class="tag-item">
            <a-button type="like" shape="round">学段:</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="default" shape="round">一年级</a-button>
            <a-button type="primary" shape="round">一年级</a-button>
        </div>
    </a-card>

    <template v-if="selectedKeys[0] === 'course'">
        <a-card title="目录" class="pages">
            <a-collapse v-model:activeKey="currentKeys" :bordered="false" accordion expand-icon-position="end">
                <a-collapse-panel v-for="collapse in sections" :key="collapse.key" :header="collapse.name">
                    <template #extra>
                        <a-button type="primary" style="pointer-events: none;" shape="round">
                            10课时
                        </a-button>
                    </template>
                    <!-- 遍历每个部分 -->
                    <a-row :gutter="16">
                        <!-- 遍历每个部分 -->
                        <a-col :span="24" v-for="section in collapse.children" :key="section.key" class="section">
                            <!-- 部分标题 -->
                            <a-row :gutter="16">
                                <a-col :span="12">
                                    <h3>
                                        {{ section.name }}
                                    </h3>
                                </a-col>
                                <a-col :span="12" style="text-align: right;">
                                    <a-button type="link" @click="download(section)">下载</a-button>
                                </a-col>
                            </a-row>
                            <!-- <a-typography-title level="6" class="section-title"></a-typography-title> -->

                            <!-- 子项列表 -->
                            <a-row style="background-color: #fafafa;margin:20px 0;" v-for="item in section.children"
                                :key="item.key" class="section-item">
                                <!-- 左侧标题 -->
                                <a-col :span="17" :offset="1">
                                    <template v-if="item.children.length === 1">
                                        <a-button type="text" v-for="(content, contentIndex) in item.children"
                                            :key="contentIndex" style="margin-left: 0;padding-left:0px;"
                                            class="content-button">
                                            {{ content }}
                                            <template #icon>
                                                <PlayCircleOutlined class="play" />
                                            </template>
                                        </a-button>
                                    </template>
                                    <template v-else>
                                        <span>{{ item.name }}</span>

                                        <a-button v-for="(content, contentIndex) in item.children" :key="contentIndex"
                                            class="content-button">
                                            {{ content }}
                                            <template #icon>
                                                <PlayCircleOutlined class="play" />
                                            </template>
                                        </a-button>
                                    </template>

                                </a-col>

                                <!-- 右侧下载 -->
                                <a-col :span="6" class="download-button">
                                    <a-button type="link" @click="download(item)">下载</a-button>
                                </a-col>
                            </a-row>
                        </a-col>
                    </a-row>
                </a-collapse-panel>

            </a-collapse>

            <!-- <a-directory-tree v-model:expandedKeys="expandedKeys" v-model:selectedKeys="currentKeys">
                <a-tree-node key="0-0" title="parent 0">
                    <a-tree-node key="0-0-0" title="leaf 0-0" is-leaf />
                    <a-tree-node key="0-0-1" title="leaf 0-1" is-leaf />
                </a-tree-node>
                <a-tree-node key="0-1" title="parent 1">
                    <a-tree-node key="0-1-0" title="leaf 1-0" is-leaf />
                    <a-tree-node key="0-1-1" title="leaf 1-1" is-leaf />
                </a-tree-node>
            </a-directory-tree> -->
        </a-card>
    </template>

    <template v-if="selectedKeys[0] === 'extensions'">
        <a-card class="pages">
            <a-row :gutter="16">
                <a-col :span="6" v-for="item in 10">
                    <a-card class="page" hoverable>
                        <template #cover>
                            <img alt="example" style="height: 200px;"
                                src="https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png" />
                        </template>
                        <a-card-meta title="Europe Street beat">
                            <template #description>www.instagram.com</template>
                        </a-card-meta>
                    </a-card>
                </a-col>
            </a-row>
            <div style="text-align: center;">
                <a-pagination :total="50" :show-total="total => `共 ${total} 条`" show-size-changer show-quick-jumper />
            </div>
        </a-card>
    </template>
</template>

<style scoped>
:deep(.ant-menu-item) {
    width: 350px;
    height: 90px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.tag {
    min-height: 300px;
    margin: 20px 0;
    border-radius: 0;
}

.tag :deep(.ant-btn) {
    margin: 0 20px;
}

.tag :deep(.ant-btn):hover {
    background: #1677ff;
    color: #FFF;
}


.tag .tag-item {
    margin: 20px 0;
}

.pages {
    min-height: 600px;
    border-radius: 0;
}

.page {
    margin: 20px 0;
}

:deep(.ant-collapse-item) .ant-collapse-arrow svg {
    transform: rotate(-90deg) !important;
    width: 1.5em;
    height: 1.5em;
}

:deep(.ant-collapse-item-active) .ant-collapse-arrow svg {
    transform: rotate(90deg) !important;
}

:deep(.ant-collapse-header) {
    position: relative;
    height: 90px;
    flex-wrap: nowrap !important;
    align-items: center !important;
}

:deep(.ant-collapse-header-text) {
    font-size: 25px;
}

:deep(.ant-collapse-content-box) {
    background-color: #FFF !important;
}

.section {
    margin-bottom: 20px;
}


.content-button {
    margin: 10px;
}

.download-button {
    display: flex;
    align-items: center;
    justify-content: flex-end;
}
</style>