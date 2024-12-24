<script setup>
import { LeftCircleOutlined, RightCircleOutlined } from '@ant-design/icons-vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { getList } from '@/api/home'

const router = useRouter();

const banner = ref([{}, {}, {}, {}, {},]);

function handleBannerClick(item) {
    console.log(item)
}


function gotoUrl(name, query) {
    router.push({
        name,
        query
    })
}

getList()


const data = ref({
    subject: [
        { src: new URL('@/assets/images/home/hscc.png', import.meta.url).href },
        { src: new URL('@/assets/images/home/fzjy.png', import.meta.url).href },
        { src: new URL('@/assets/images/home/pdjy.png', import.meta.url).href },
        { src: new URL('@/assets/images/home/gkk.png', import.meta.url).href },
        { src: new URL('@/assets/images/home/xljk.png', import.meta.url).href },
        { src: new URL('@/assets/images/home/aqjy.png', import.meta.url).href },
        { src: new URL('@/assets/images/home/kxj.png', import.meta.url).href },
        { src: new URL('@/assets/images/home/pplm.png', import.meta.url).href },
        { src: new URL('@/assets/images/home/ls.jpg', import.meta.url).href },
    ],
    course: [
        {
            name: '小学',
            src: new URL('@/assets/images/home/small.png', import.meta.url).href,
            children: [
                {
                    name: 1
                },
                {
                    name: 1
                },
                {
                    name: 1
                },
                {
                    name: 1
                },
                {
                    name: 1
                },
            ]
        },

        {
            name: '初中',
            src: new URL('@/assets/images/home/mid.png', import.meta.url).href,
            children: [
                {
                    name: 1
                },
                {
                    name: 1
                },
                {
                    name: 1
                }
            ]
        },

        {
            name: '高中',
            src: new URL('@/assets/images/home/bigmid.png', import.meta.url).href,
            children: [
                {
                    name: 1
                },
                {
                    name: 1
                },
                {
                    name: 1
                }
            ]
        },

    ],
    practice: [
        {
            name: '1',
            src: '1'
        }, {
            name: '1',
            src: '1'
        }, {
            name: '1',
            src: '1'
        }, {
            name: '1',
            src: '1'
        }, {
            name: '1',
            src: '1'
        }, {
            name: '1',
            src: '1'
        }, {
            name: '1',
            src: '1'
        },
        {
            name: '1',
            src: '1'
        }
    ]
});

// 专题点击
function handleSubjectClick(item) {
    console.log(item)
}

// 课程教学点击
function handleCourseClick(item) {
    gotoUrl('course', { id: 1 })
}

function handlePracticeClick(item) {
    console.log(item)
}
</script>

<template>
    <a-carousel arrows effect="fade">
        <template #prevArrow>
            <div class="custom-slick-arrow" style="left: 10px; z-index: 1">
                <left-circle-outlined />
            </div>
        </template>
        <template #nextArrow>
            <div class="custom-slick-arrow" style="right: 10px">
                <right-circle-outlined />
            </div>
        </template>
        <div class="banner" @click="handleBannerClick(item)" v-for="item in banner">

        </div>
    </a-carousel>

    <h2 class="title">专题教育</h2>

    <a-row :gutter="16">
        <a-col v-for="item in data.subject" :key="item.id" :span="8">
            <div class="subject" @click="handleSubjectClick(item)">
                <img :src="item.src" alt="">
            </div>
        </a-col>
    </a-row>


    <h2 class="title">课程教育</h2>

    <a-row :gutter="16">
        <a-col v-for="item in data.course" :span="24">
            <a-card class="card">
                <a-row :gutter="16">
                    <a-col :span="4">
                        <img :src="item.src" alt="" class="preview">
                    </a-col>
                    <a-col :span="4" v-for="child in item.children">
                        <a-button @click="handleCourseClick(item)" style="width: 100%;height:100%" type="link">{{
            child.name
        }}</a-button>
                    </a-col>
                </a-row>
            </a-card>
        </a-col>
    </a-row>


    <h2 class="title">课后服务</h2>

    <a-row :gutter="16">
        <a-col :span="6" v-for="item in data.practice" style="margin-top: 10px;">
            <a-card @click="handlePracticeClick(item)" hoverable style="max-height: 220px;">
                <template #cover>
                    <img alt="" class="img" src="https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png" />
                </template>
                <a-card-meta title="Europe Street beat">
                </a-card-meta>
            </a-card>
        </a-col>
    </a-row>

</template>

<style scoped>
.banner {
    width: 100%;
    height: 250px;
    background-color: #0356ca;
    border-radius: 9px;
    cursor: pointer;
}


.ant-carousel :deep(.slick-arrow.custom-slick-arrow) {
    width: 25px;
    height: 25px;
    font-size: 25px;
    color: #fff;
    background-color: rgba(31, 45, 61, 0.11);
    opacity: 0.3;
}

.ant-carousel :deep(.custom-slick-arrow:before) {
    display: none;
}

.ant-carousel :deep(.custom-slick-arrow:hover) {
    opacity: 0.5;
}

.ant-carousel :deep(.slick-slide h3) {
    color: #fff;
}

.title {
    text-align: center;
    margin: 30px 0;
    font-weight: normal;
}

.subject {
    width: 100%;
    margin-top: 10px;
}

.subject img {
    width: 100%;
    min-height: 90px;
    border-radius: 9px;
    cursor: pointer;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    /* 添加过渡效果 */
}

.subject img:hover {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
    /* 添加阴影效果 */
}


.card {
    min-height: 50px;
    border-radius: 0px;
}

.card .preview {
    width: 127px;
}

.img {
    height: 158px;
    cursor: pointer;
    object-position: top;
    object-fit: cover;
}
</style>