<script setup>
import breadcrumb from "@/components/breadcrumb/index.vue";
import Preview from "@/components/preview/index.vue";
import { ref } from "vue";
import { h } from "vue";
import { message } from "ant-design-vue";
import {
    DownloadOutlined,
    ShareAltOutlined,
    EyeOutlined,
    StarOutlined,
    LikeOutlined,
} from "@ant-design/icons-vue";

const rateValue = ref(2.5);

function handleResClick(item) {
    message.success("all!");
    router.push({ name: "courseDetails" });
}

function handleDownClick(item) {
    message.success("single!");
    router.push({ name: "courseDetails" });
}
</script>

<template>
    <a-row :gutter="16">
        <!-- <a-col :span="10"> -->
        <!-- <breadcrumb :navs="[]" /> -->
        <!-- </a-col> -->
        <a-col :span="24">
            <a-row :gutter="16">
                <h1 class="text-2xl">文心实践</h1>
            </a-row>
            <a-row :gutter="16" class="mt-2">
                <a-col :span="4" v-for="item in 6">
                    课程负责人：wangcaibin
                </a-col>
            </a-row>
        </a-col>
    </a-row>

    <a-row :gutter="16" class="mt-10">
        <a-col :span="16">
            <div class="warp">
                <Preview />
            </div>

            <a-typography class="p-5 bg-[#fff] mt-5 rounded-xl ">
                <a-typography-title>Introduction</a-typography-title>
                <a-typography-paragraph>
                    In the process of internal desktop applications development, many different
                    design specs and implementations would be involved, which might cause designers
                    and developers difficulties and duplication and reduce the efficiency of
                    development.
                </a-typography-paragraph>
            </a-typography>
        </a-col>
        <a-col :span="8">
            <a-card :title="'学习清单 (目录)'" class="mt-5 border-none">
                <template #extra>
                    <a-button type="text">打包下载</a-button>
                </template>
                <a-row :gutter="16">
                    <a-col :span="24">
                        <div class="list-container">
                            <!-- 理论课程 -->
                            <div class="list-item active" @click="handleResClick">
                                <div class="item-left">
                                    <div class="circle-icon">
                                        <SvgIcon icon="video" />
                                    </div>
                                    <span class="item-text">视频课程</span>
                                </div>
                                <DownloadOutlined @click.stop="handleDownClick" class="download-icon" />
                            </div>
                        </div>
                    </a-col>

                </a-row>
            </a-card>
        </a-col>
    </a-row>
</template>

<style scoped>
.warp {
    background: #fff;
    padding: 10px;
}

.list-container {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.list-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px;
    background-color: #f5f5f5;
    transition: all 0.3s ease;
    cursor: pointer;
    margin-top: 10px;
}

.list-item:hover {
    background-color: #cfe5fc;
    color: #2b8cfd;
}

.list-item.active {
    background-color: #cfe5fc;
    color: #2b8cfd;
}

.item-left {
    display: flex;
    align-items: center;
    gap: 12px;
}

.item-text {
    font-size: 14px;
}

.download-icon {
    width: 18px;
    height: 18px;
    cursor: pointer;
    transition: color 0.3s ease;
    color: #2b8cfd;
}

.download-icon:hover {
    color: #034591;
}
</style>
