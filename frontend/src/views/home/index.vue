<script setup>
import {LeftCircleOutlined, RightCircleOutlined} from '@ant-design/icons-vue';
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import {getTagInfoById, getTagsByParentId, getTagsById, getTagsByLevel} from "@/api/other";

const router = useRouter();

const banner = ref([{}, {}, {}, {}, {},]);

function handleBannerClick(item) {
  console.log(item)
}

const subjects = ref([]);
const subjectsId = ref(2);
const baseUrl = import.meta.env.VITE_API_BASE_URL;
const imgUrl = baseUrl + "/api/covers/stream/";

async function getSubject() {
  const {data, code} = await getTagsById(subjectsId.value);
  if (code === 200) {
    subjects.value = data[0]?.children ?? [];
    // console.log(subjects.value)
  }
}

getSubject();

function gotoUrl(name, query) {
  router.push({name, query});
}


// getList()


const course = ref(
    [
      {
        name: '小学',
        src: new URL('@/assets/images/home/small.png', import.meta.url).href,
        children: []
      },

      {
        name: '初中',
        src: new URL('@/assets/images/home/mid.png', import.meta.url).href,
        children: []
      },

      {
        name: '高中',
        id: '',
        src: new URL('@/assets/images/home/bigmid.png', import.meta.url).href,
        children: []
      }
    ]
);

// 专题点击
function handleSubjectClick(id) {
  router.push({name: 'SubjectColumn', query: {id: subjectsId.value, selectId: id}})
}

// 课程教学点击
const treeSelectRef = ref();
function transformNode(node, level = 1, maxLevel = 4) {
  const {children, ...rest} = node;
  return {
    ...rest,
    children: (children && level < maxLevel)
        ? children.map((child) => transformNode(child, level + 1, maxLevel))
        : null,
  };
}
function handleCourseClick(item, child) {
  let arr = [transformNode(item), transformNode(child)];
  router.push({name: 'course', query: {select: JSON.stringify(arr),id:1}});
}

const getGradeData = async () => {
  let d = await getTagsByParentId(3);
  let arr = d.data.filter(v => v.name === '小学' || v.name === '初中' || v.name === '高中');

  arr = await Promise.all(arr.map(async v => {
    let item = await getTagsById(v.id);
    return item.data[0]; // 直接返回对象
  }));
  let names = {
    '小学': new URL('@/assets/images/home/small.png', import.meta.url).href,
    '初中': new URL('@/assets/images/home/mid.png', import.meta.url).href,
    '高中': new URL('@/assets/images/home/bigmid.png', import.meta.url).href,
  };
  course.value = arr.map(v => {
    return {
      ...v,
      src: names[v.name]
    }
  });
}
getGradeData();

</script>

<template>
  <a-carousel arrows effect="fade">
    <template #prevArrow>
      <div class="custom-slick-arrow" style="left: 10px; z-index: 1">
        <left-circle-outlined/>
      </div>
    </template>
    <template #nextArrow>
      <div class="custom-slick-arrow" style="right: 10px">
        <right-circle-outlined/>
      </div>
    </template>
    <div class="banner" @click="handleBannerClick(item)" v-for="item in banner">

    </div>
  </a-carousel>

  <h2 class="title">专题教育</h2>

  <a-row :gutter="16">
    <a-col v-for="item in subjects" :key="item.id" :span="8">
      <div class="subject" @click="handleSubjectClick(item.id)">
        <img :src="imgUrl+item.coverUuid" class="object-cover" alt="subject_preview">
      </div>
    </a-col>
  </a-row>

  <h2 class="title">课程教育</h2>

  <a-row :gutter="16">
    <a-col v-for="item in course" :span="24">
      <a-card class="card">
        <a-row :gutter="16">
          <a-col :span="4">
            <img :src="item.src" alt="" class="preview">
          </a-col>
          <a-col :span="2" v-for="child in item.children">
            <a-button @click="handleCourseClick(item,child)" style="width: 100%;height:100%" type="link">{{
                child.name
              }}
            </a-button>
          </a-col>
        </a-row>
      </a-card>
    </a-col>
  </a-row>

  <!--
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
      </a-row> -->

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
  height: 90px;
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