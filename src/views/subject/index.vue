<script setup>
import breadcrumb from '@/components/breadcrumb/index.vue';
import { RightOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';



function handlePageClick(item) {
    message.success('success!');
}

function handlePageBannerClick(item) {
    message.success('success!');
}
</script>

<template>
    <breadcrumb />

    <template v-for="item in 3">
        <a-row>
            <a-col :span="12">
                <a-typography-title style="margin:30px 0 20px 0" :level="4">红色传承</a-typography-title>
            </a-col>
            <a-col :span="12" style="text-align: right;display:flex;align-items: center;justify-content: end;">
                <a-button type="text">更多
                    <RightOutlined />
                </a-button>
            </a-col>
        </a-row>
        <a-card class="page">
            <a-row :gutter="16">
                <a-col :span="12" style="cursor: pointer;" @click="handlePageBannerClick(1)">
                    <img width="100%" style="min-height: 340px;"
                        src="https://mxjkoss.oss-cn-hangzhou.aliyuncs.com/coursezjer/2024/5/16/khg1715849133217.png?x-oss-process=image/resize,m_fill,w_600,h_337"
                        alt="">
                </a-col>
                <a-col :span="12" style="cursor: pointer;" @click="handlePageClick(1)">
                    <a-row @click="" :gutter="16" v-for="item in 3">
                        <a-col :span="8">
                            <img src="https://mxjkoss.oss-cn-hangzhou.aliyuncs.com/coursezjer/2024/5/30/VNe1717057447431.jpg?x-oss-process=image/resize,m_fill,w_285,h_160"
                                width="100%" style="min-height: 113px;" alt="">
                        </a-col>
                        <a-col :span="12">
                            <a-card-meta
                                title="Europe Street beatEurope Street beatEurope Street beatEurope Street beat">
                                <template #description>
                                    <div class="description">
                                        www.instagram.Europe Street beatEurope Street beatEurope Street beatEurope
                                        Street
                                        beatEurope Street beatEurope Street beat
                                    </div>
                                </template>
                            </a-card-meta>

                            <span style="display:block;margin-top: 10px;color:#ccc;">浏览量: {{ 11111 }}</span>
                        </a-col>
                    </a-row>
                </a-col>
            </a-row>
        </a-card>
    </template>
</template>

<style scoped>
.page {
    min-height: 300px;
    margin: 20px 0;
}

img {
    border-radius: 9px;
}

.description {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    /* 限制最多显示两行 */
    overflow: hidden;
    /* 超出部分隐藏 */
    text-overflow: ellipsis;
    /* 超出部分显示省略号 */
}
</style>