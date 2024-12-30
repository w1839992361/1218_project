<script setup>
import { ref, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { PlayCircleOutlined } from "@ant-design/icons-vue";
import { message } from "ant-design-vue";
import { getTagsByParentId, getTagsByLevel, getTagsById } from "@/api/other";

import { getAllTree, getVideoUUID, getVideoInfo } from "@/api/admin/content";
import Classify from "@/components/classify/index.vue";

const router = useRouter();
const route = useRoute();

const selectedKeys = ref(["course"]);

const courseTitle = ref([

]);
const currentKeys = ref(1);

onMounted(async () => {
  const { data, code } = await getTagsByParentId(route.query.id);
  courseTitle.value = data.map((item) => {
    return {
      key: item.name,
      ...item,
    };
  });
  let courseName = route.query?.courseName || courseTitle.value[0].name;
  selectedKeys.value = [courseName];
  if (code === 200) {
    let id = courseTitle.value.find(i=>i.key === courseName)?.id
    getClassifyData(id);
  }
});

const sections = ref([]);

function download(item) {
  message.success("success!");
}

function handleSingleClick(info) {
  router.push({ name: "courseDetails" ,query:{...info}});
}

const baseUrl = import.meta.env.VITE_API_BASE_URL;
const imgUrl = baseUrl + "/api/covers/stream/";

const selectedId = ref();


const currentClassify = ref();
const classifyData = ref([]);
const videos = ref();
const allresourceUuid = ref();
const pageLoading = ref(true);
const classLoading = ref(true);
const isLoading = ref(true);
async function getClassifyData(id) {
  const { data, code } = await getTagsById(id);
  if (code === 200) {
    classifyData.value = data[0].children;
    selectedId.value = (+route?.query?.courseClass) ?? null;
    classLoading.value = false;
  }
}

async function getPages(id) {
  const { data, code } = await getTagsById(id);
  if (code === 200 && data.length) {
    sections.value = data[0]?.children || [];
    if (sections.value.length){
      allresourceUuid.value = sections.value.map((unit) => ({
        id: unit.id,
        name: unit.name,
        resourceUuids: extractResourceUuids(unit.children),
      }));
      pageLoading.value = false;
    }
  }
}
function extractResourceUuids(nodes) {
  let uuids = [];
  function traverse(node) {
    if (node.resourceUuid) {
      uuids.push(node.resourceUuid);
    }
    if (node.children && node.children.length > 0) {
      node.children.forEach((child) => traverse(child));
    }
  }
  if (nodes && nodes.length) {
    nodes.forEach((node) => traverse(node));
  }
  return uuids;
}

function handleClassifyChange(item) {
  sections.value = [];
  pageLoading.value = true;
  currentClassify.value = item;
  console.log(item)
  item?.id && getPages(item?.id);
  router.push({name:'course',query:{id:route.query.id,courseClass: item?.id,courseName:route.query.courseName}})
}

function handleMenuChange(item) {
  // selectedId.value = null;
  classLoading.value = true;
  router.push({name:'course',query:{id:route.query.id,courseName:item.key}})
  let id = courseTitle.value.find((c) => c.name === item.key)?.id;
  id && getClassifyData(id);
}

function handleExtensions(item) {
  router.push({ name: "courseExtendDetail",query:{...item} });
}

async function handleCollapseChange(id) {
  isLoading.value = true;
  let currentResourceUuids = allresourceUuid.value.find((u) => u.id == id)?.resourceUuids;
  if (currentResourceUuids && currentResourceUuids.length) {
    let videosMapping = await Promise.all(
      currentResourceUuids.map(async (uuid) => {
        let videos = await getVideos(uuid);
        isLoading.value = false;
        return { [uuid]: videos };
      })
    );

    videos.value = Object.assign({}, ...videosMapping);
  } else {
    isLoading.value = false;
    videos.value = {};
  }
}

async function getVideos(uuid) {
  const { data, code } = await getVideoUUID(uuid);
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

function getVideosByUid(uid) {
  if (videos.value?.[uid]) {
    return videos.value[uid];
  } else {
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
        <img :src="imgUrl + item.coverUuid" width="50px" alt="" />
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
    <a-card v-if="sections.length" :loading="pageLoading" title="目录" class="pages">
      <a-collapse
        v-model:activeKey="currentKeys"
        :bordered="false"
        accordion

        @change="handleCollapseChange"
        expand-icon-position="end"
      >
        <a-collapse-panel
          v-for="collapse in sections"
          :key="collapse.id"
          :header="collapse.name"
        >
          <template #extra>
            <a-button type="primary" style="pointer-events: none" shape="round">
              10课时
            </a-button>
          </template>
          <!-- 遍历每个部分 -->
          <a-row  :gutter="16">
            <!-- 遍历每个部分 -->
            <a-col
              :span="24"
              v-for="section in collapse.children"
              :key="section.key"
              class="section"
            >
              <!-- 部分标题 -->
              <a-row  :gutter="16" class="flex justify-between items-center">
                <a-col>
                  <h3 class="text-xl">
                    {{ section.name }}
                  </h3>
                </a-col>
                <a-col>
                  <a-button type="text" @click="download(section)"> 打包下载 </a-button>
                </a-col>
              </a-row>

              <a-spin v-if="isLoading" :spinning="isLoading"></a-spin>

              <div
                v-else
                v-for="page in section.children"
                :key="page.id"
                class="flex justify-between bg-gray-50 px-4 py-2"
              >
                <!-- 左侧内容区域 - 允许换行 -->
                <div class="flex flex-wrap items-center gap-4 flex-1">
                  <div class="flex items-center">
                    {{ page.name }}
                  </div>

                  <a-button
                    class="flex items-center"
                    @click.stop="handleSingleClick(page)"
                    v-for="video in getVideosByUid(page.resourceUuid)"
                    :key="video.id"
                  >
                    {{ video.title }}
                    <PlayCircleOutlined class="ml-1 text-gray-400" />
                  </a-button>
                </div>

                <!-- 右侧下载按钮 - 固定位置 -->
                <a-button @click="download(section)" type="text" class="text-gray-500 ml-4 shrink-0 flex items-center">下载</a-button>
              </div>
            </a-col>
          </a-row>
        </a-collapse-panel>
      </a-collapse>
    </a-card>
    <a-empty v-else   :description="null" />

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
              <template #description>{{item.description}}</template>
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
    <a-empty v-else   :description="null" />

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
</style>
