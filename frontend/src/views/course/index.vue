<script setup>
import {ref, watch, onMounted, nextTick} from "vue";
import {useRouter, useRoute} from "vue-router";
import {PlayCircleOutlined, EyeOutlined, DownloadOutlined} from "@ant-design/icons-vue";
import {message} from "ant-design-vue";
import {getTagsByParentId, getTagsByLevel, getTagsById, getUpdateZip} from "@/api/other";

import TreeSelectComponent from '@/components/TreeSelectComponent.vue'

import {getAllTree, getVideoUUID, getVideoInfo} from "@/api/admin/content";
import {useVideosInfo} from "@/stores/video.js";

const videoInfo = useVideosInfo();
const router = useRouter();
const route = useRoute();

const selectedKeys = ref(["course"]);
const courseTitle = ref([]);
const currentKeys = ref(1);
onMounted(async () => {
  const {data, code} = await getTagsByParentId(route.query.id);
  courseTitle.value = data.map((item) => {
    return {
      key: item.name,
      ...item,
    };
  });
  let courseName = route.query?.courseName || courseTitle.value[0].name;
  selectedKeys.value = [courseName];
});

const sections = ref([]);

const downloadLoading = ref([]);
const handleDownload = async (item) => {
  downloadLoading.value.push(item.id);
  // console.log(item.id)
  let response = await getUpdateZip(item.id);
  let blob = new Blob([response], {type: "application/zip"});
  let url = URL.createObjectURL(blob);
  let a = document.createElement("a");
  a.href = url;
  a.download = `export_${item.name}.zip`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
  message.success("success!");
  downloadLoading.value = downloadLoading.value.filter(v => v.id !== item.id);
}

function handleSingleClick(info) {
  router.push({name: "courseDetails", query: {id: info.id, resourceUuid: info.resourceUuid}});
}

const baseUrl = import.meta.env.VITE_API_BASE_URL;
const imgUrl = baseUrl + "/api/covers/stream/";


const videos = ref();
const pageLoading = ref(true);
const isLoading = ref(true);


async function getPages(id) {
  pageLoading.value = true;
  const {data, code} = await getTagsById(id);
  if (code === 200 && data.length) {
    sections.value = data[0]?.children ?? [];
    videoInfo.incrementViews(sections.value[0]?.id);
    pageLoading.value = false;
  }
}

const treeSelectRef = ref();
const treeId = ref();


const selectClass = ref([]);
treeId.value = courseTitle.value.find(v => v.name === route.query.courseName)?.id;

if (route.query.select) {
  selectClass.value = JSON.parse(route.query.select);
}
function handleMenuChange(item) {
  treeId.value = courseTitle.value.find(v => v.name === item.key)?.id;
  selectClass.value = [];
  router.push({name: 'course', query: {id: 1, courseName: item.key}});
}

const handleChange = (v) => {
  sections.value = [];
  if (v.length > 0) {
    let arr = [v[0], v[1]].filter(vv=>vv);
    router.push({
      name: 'course',
      query: {select: JSON.stringify(arr), id: route.query.id, courseName: route.query.courseName}
    });
    getPages(v[v.length - 1]?.id);
  }
}

function handleExtensions(item) {
  router.push({name: "courseExtendDetail", query: {...item}});
}

const currentUtil = ref([]);

async function handleCollapseChange(id) {
  if (!id) return (isLoading.value = true);
  isLoading.value = true;
  currentUtil.value = [];
  let {data} = await getTagsById(id);
  let arr = data[0]?.children ?? [];

  let videosMapping = await Promise.all(
      arr.map(async (item) => {
        let videos = await getVideos(item.resourceUuid);

        // 使用 Promise.all 解析每个视频的信息
        videos = await Promise.all(
            videos.map(async (v) => {
              let {data} = await getVideoInfo(v.id);
              return {
                ...data,
              };
            })
        );

        return {
          ...item,
          videos,
        };
      })
  );
  isLoading.value = false;
  currentUtil.value = videosMapping;
}

async function getVideos(uuid) {
  const {data, code} = await getVideoUUID(uuid);
  if (code === 200 && data.length) {
    let videos = await Promise.all(
        data.map(async (item) => {
          return (await getVideoInfo(item)).data;
        })
    );
    return videos;
  } else {
    isLoading.value = false;
    return [];
  }
}


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


function handleCourseClick(item, child) {
  // console.log(item, child)

  // router.push({name: 'course', query: {select: JSON.stringify(arr),id:1}});
}
</script>

<template>
  <a-menu
      :inlineCollapsed="false"
      @click="handleMenuChange"
      style="width: 100%"
      v-model:selectedKeys="selectedKeys"
      mode="horizontal"
  >
    <a-menu-item :key="item.key" v-for="item in courseTitle">
      <template #icon>
        <img :src="imgUrl + item.coverUuid" width="50px" alt=""/>
      </template>
      <h3>{{ item.name }}</h3>
    </a-menu-item>
  </a-menu>
  <!--  <a-row :gutter="16">-->
  <!--    <a-col v-for="item in course" :span="24">-->
  <!--      <a-card class="card">-->
  <!--        <a-row :gutter="16">-->
  <!--          <a-col :span="4">-->
  <!--            <img :src="item.src" alt="" class="preview">-->
  <!--          </a-col>-->
  <!--          <a-col :span="2" v-for="child in item.children">-->
  <!--            <a-button @click="handleCourseClick(item,child)" style="width: 100%;height:100%" type="link">{{-->
  <!--                child.name-->
  <!--              }}-->
  <!--            </a-button>-->
  <!--          </a-col>-->
  <!--        </a-row>-->
  <!--      </a-card>-->
  <!--    </a-col>-->
  <!--  </a-row>-->
  <!--  {{JSON.parse(route.query.select).length}}-->
  <!--      {{route.query.select}}-->
  <TreeSelectComponent ref="treeSelectRef" :selected="selectClass"  :tag-id="treeId"
                       @change="handleChange"/>

  <template v-if="selectedKeys[0] === '学科课程'">
    <a-card v-if="sections.length" :loading="pageLoading" class="course-card">
      <template #title>
        <h2 class="text-2xl font-bold">课程目录</h2>
      </template>

      <div class="course-header">
        <h3 class="text-xl font-semibold mb-4">基础课程</h3>
        <a-row :gutter="50">
          <a-col :span="4">
            <a-image
                :width="200"
                :src="imgUrl + sections[0]?.coverUuid"
                :preview="false"
                class="rounded-lg shadow-md"
            />
          </a-col>
          <a-col :span="16">
            <h4 class="text-2xl font-bold mb-2">{{ sections[0].name }}</h4>
            <p class="text-lg text-gray-600 mb-4">{{ sections[0].description }}</p>
            <eye-outlined/>
            <span class="ml-1 text-lg text-gray-600">{{ videoInfo.getVideoData(sections[0].id).views }} 浏览量</span>
          </a-col>
        </a-row>
      </div>

      <a-collapse
          v-if="sections[0]?.children?.length > 0"
          v-model:activeKey="currentKeys"
          :bordered="false"
          accordion
          @change="handleCollapseChange"
          expand-icon-position="end"
          class="mt-8"
      >
        <a-collapse-panel
            v-for="collapse in sections[0].children"
            :key="collapse.id"
            :header="collapse.name"
        >
          <template #extra>
            <a-button type="primary" :loading="downloadLoading.includes(collapse.id)"
                      @click.stop="handleDownload(collapse)" class="download-btn">
              <download-outlined/>
              打包下载
            </a-button>
          </template>

          <a-spin :spinning="isLoading">
            <template v-if="!isLoading">
              <div v-for="section in currentUtil" :key="section.id" class="section-item">
                <h5 class="text-lg font-semibold mb-2">{{ section.name }}</h5>
                <a-space wrap>
                  <a-button
                      v-for="video in section.videos"
                      :key="video.id"
                      @click.stop="handleSingleClick(section)"
                      type="default"
                      shape="round"
                      class="video-btn"
                  >
                    <template #icon>
                      <play-circle-outlined/>
                    </template>
                    {{ video.title }}
                  </a-button>
                </a-space>
              </div>
            </template>
          </a-spin>
        </a-collapse-panel>
      </a-collapse>
    </a-card>
    <a-empty v-else/>
  </template>

  <template v-if="selectedKeys[0] === '拓展课程'">
    <a-card v-if="sections.length" :loading="pageLoading" class="pages min-h-[500px]">
      <a-row :gutter="16" class=" min-h-[500px]">
        <a-col :span="6" @click="handleExtensions(item)" v-for="item in sections">
          <a-card class="page" hoverable>
            <template #cover>
              <img
                  alt="preview"
                  class="max-h-[150px] "
                  :src="baseUrl+'/api/covers/stream/'+item?.coverUuid"
              />
            </template>
            <a-card-meta :title="item.name">
              <template #description>{{ item.description }}</template>
            </a-card-meta>
          </a-card>
        </a-col>
      </a-row>
      <div style="text-align: center">
        <a-pagination
            :total="50"
            :show-total="(total) => `共 ${total} 条`"
            show-size-changer
            show-quick-jumper
        />
      </div>
    </a-card>
    <a-empty v-else :description="null"/>

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
  color: #fff;
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
  background-color: #fff !important;
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

.course-card {
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.course-header {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.section-item {
  margin-bottom: 16px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.video-btn {
  margin-bottom: 8px;
}

.download-btn {
  font-weight: 500;
}

:deep(.ant-collapse-header) {
  font-weight: 600;
}

:deep(.ant-collapse-content-box) {
  padding-top: 16px !important;
}
</style>
