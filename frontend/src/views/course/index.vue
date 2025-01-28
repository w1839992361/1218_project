<script setup>
import {ref, watch, onMounted} from "vue";
import {useRouter, useRoute} from "vue-router";
import {PlayCircleOutlined, EyeOutlined} from "@ant-design/icons-vue";
import {message} from "ant-design-vue";
import {getTagsByParentId, getTagsByLevel, getTagsById} from "@/api/other";

import {getAllTree, getVideoUUID, getVideoInfo} from "@/api/admin/content";
import Classify from "@/components/classify/index.vue";
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
  if (code === 200) {
    let id = courseTitle.value.find(i => i.key === courseName)?.id
    getClassifyData(id);
  }
});

const sections = ref([]);

function download(item) {
  message.success("success!");
}

function handleSingleClick(info) {
  router.push({name: "courseDetails", query: {id: info.id, resourceUuid: info.resourceUuid}});
}

const baseUrl = import.meta.env.VITE_API_BASE_URL;
const imgUrl = baseUrl + "/api/covers/stream/";

const selectedId = ref();


const currentClassify = ref();
const classifyData = ref([]);
const videos = ref();
const pageLoading = ref(true);
const classLoading = ref(true);
const isLoading = ref(true);

async function getClassifyData(id) {
  const {data, code} = await getTagsById(id);
  if (code === 200) {
    classifyData.value = data[0].children;
    selectedId.value = (+route?.query?.courseClass) ?? null;
    classLoading.value = false;
  }
}

async function getPages(id) {
  const {data, code} = await getTagsById(id);
  if (code === 200 && data.length) {
    sections.value = data[0]?.children || [];
    videoInfo.incrementViews(sections.value[0]?.id)
    pageLoading.value = false;
  }
}

function handleClassifyChange(item) {
  sections.value = [];
  pageLoading.value = true;
  currentClassify.value = item;
  console.log(item)
  item?.id && getPages(item?.id);
  router.push({name: 'course', query: {id: route.query.id, courseClass: item?.id, courseName: route.query.courseName}})
}

function handleMenuChange(item) {
  // selectedId.value = null;
  classLoading.value = true;
  router.push({name: 'course', query: {id: route.query.id, courseName: item.key}})
  let id = courseTitle.value.find((c) => c.name === item.key)?.id;
  id && getClassifyData(id);
}

function handleExtensions(item) {
  router.push({name: "courseExtendDetail", query: {...item}});
}

const currentUtil = ref([]);

async function handleCollapseChange(id) {
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

  <a-card class="tag" :loading="classLoading">
    <Classify
        :data="classifyData"
        :selectedId="selectedId"
        v-if="classifyData.length"
        @change="handleClassifyChange"
    />
  </a-card>

  <template v-if="selectedKeys[0] === '学科课程'">
    <a-card v-if="sections.length" :loading="pageLoading" class="course-card">
      <template #title>
        <h2 class="text-2xl font-bold">课程目录</h2>
      </template>

      <div class="course-header">
        <h3 class="text-xl font-semibold mb-4">基础课程</h3>
        <a-row :gutter="50" >
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
              <eye-outlined />
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
            <a-button type="primary" @click.stop="download(collapse)" class="download-btn">
              <download-outlined /> 打包下载
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
                    <template #icon><play-circle-outlined /></template>
                    {{ video.title }}
                  </a-button>
                </a-space>
              </div>
            </template>
          </a-spin>
        </a-collapse-panel>
      </a-collapse>
    </a-card>
    <a-empty v-else />
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
