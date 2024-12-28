<script setup>
import { ref, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { PlayCircleOutlined } from "@ant-design/icons-vue";
import { message } from "ant-design-vue";
import { getTagsByParentId, getTagsByLevel, getTagsById } from "@/api/other";
import { getAllTree } from "@/api/admin/content";
import { getPreview } from "@/api/preview";
import Classify from "@/components/classify/index.vue";

const router = useRouter();
const route = useRoute();

const selectedKeys = ref(["course"]);
const courseTitle = ref([
  // {
  //     name: '学科课程',
  //     key: 'course',
  //     src: new URL('@/assets/images/course/hat.png', import.meta.url).href
  // },
  // {
  //     name: '拓展课程',
  //     key: 'extensions',
  //     src: new URL('@/assets/images/course/dashboard.png', import.meta.url).href
  // }
]);
const currentKeys = ref(1);

onMounted(async () => {
  const { data, code } = await getTagsByParentId(route.query.course);
  courseTitle.value = data.map((item) => {
    return {
      key: item.name,
      ...item,
    };
  });
  selectedKeys.value = [courseTitle.value[0].name];
  if (code === 200) {
    getClassifyData(courseTitle.value[0].id);
  }
});

const sections = ref([
 
]);

function download(item) {
  message.success("success!");
}

function handleAllClick(item) {
  message.success("all!");
  router.push({ name: "courseDetails" });
}

function handleSingleClick(item) {
  message.success("single!");
  router.push({ name: "courseDetails" });
}

const baseUrl = import.meta.env.VITE_API_BASE_URL;
const imgUrl = baseUrl + "/api/covers/stream/";

const selectedId = ref();
const currentClassify = ref();
const classifyData = ref([]);

async function getClassifyData(id) {
  const { data, code } = await getTagsById(id);
  if (code === 200) {
    classifyData.value = data[0].children;
  }
}

async function getPages(id) {
  const { data, code } = await getTagsById(id);
  if (code === 200 && data.length) {
    sections.value = data[0].children;
  }
}

function handleClassifyChange(item) {
  currentClassify.value = item;
  item?.id && getPages(item?.id);
}

function handleMenuChange(item) {
  let id = courseTitle.value.find((c) => c.name === item.key)?.id;
  id && getClassifyData(id);
}

function handleExtensions(item) {
  router.push({ name: "courseExtendDetail" });
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

  <a-card class="tag">
    <Classify
      :data="classifyData"
      :selectedId="selectedId"
      v-if="classifyData.length"
      @change="handleClassifyChange"
    />
  </a-card>

  <template v-if="selectedKeys[0] === '学科课程'">
    <a-card title="目录" class="pages">
      <a-collapse
        v-model:activeKey="currentKeys"
        :bordered="false"
        accordion
        expand-icon-position="end"
      >
        <a-collapse-panel
          v-for="collapse in sections"
          :key="collapse.key"
          :header="collapse.name"
        >
          <template #extra>
            <a-button type="primary" style="pointer-events: none" shape="round">
              10课时
            </a-button>
          </template>
          <!-- 遍历每个部分 -->
          <a-row :gutter="16">
            <!-- 遍历每个部分 -->
            <a-col
              :span="24"
              v-for="section in collapse.children"
              :key="section.key"
              class="section"
            >
              <!-- 部分标题 -->
              <a-row :gutter="16">
                <a-col :span="12">
                  <h3>
                    {{ section.name }}
                  </h3>
                </a-col>
                <a-col :span="12" style="text-align: right">
                  <a-button type="link" @click="download(section)">下载</a-button>
                </a-col>
              </a-row>
              <!-- <a-typography-title level="6" class="section-title"></a-typography-title> -->

              <!-- 子项列表 -->
              <a-row
                @click="handleAllClick(item)"
                style="background-color: #fafafa; margin: 20px 0; cursor: pointer"
                v-for="item in section.children"
                :key="item.key"
                class="section-item"
              >
                <!-- 左侧标题 -->
                <a-col :span="17" :offset="1">
                  <template v-if="item.children.length === 1">
                    <a-button
                      type="text"
                      @click.stop="handleSingleClick(content)"
                      v-for="(content, contentIndex) in item.children"
                      :key="contentIndex"
                      style="margin-left: 0; padding-left: 0px"
                      class="content-button"
                    >
                      {{ content }}
                      <template #icon>
                        <PlayCircleOutlined class="play" />
                      </template>
                    </a-button>
                  </template>
                  <template v-else>
                    <span>{{ item.name }}</span>

                    <a-button
                      @click.stop="handleSingleClick(content)"
                      v-for="(content, contentIndex) in item.children"
                      :key="contentIndex"
                      class="content-button"
                    >
                      {{ content }}
                      <template #icon>
                        <PlayCircleOutlined class="play" />
                      </template>
                    </a-button>
                  </template>
                </a-col>

                <!-- 右侧下载 -->
                <a-col :span="6" class="download-button">
                  <a-button type="link" @click.stop="download(item)">下载</a-button>
                </a-col>
              </a-row>
            </a-col>
          </a-row>
        </a-collapse-panel>
      </a-collapse>

    </a-card>
  </template>

  <template v-if="selectedKeys[0] === '拓展课程'">
    <a-card class="pages">
      <a-row :gutter="16">
        <a-col :span="6" @click="handleExtensions(item)" v-for="item in 10">
          <a-card class="page" hoverable>
            <template #cover>
              <img
                alt="example"
                class="max-h-[150px]"
                src="https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png"
              />
            </template>
            <a-card-meta title="Europe Street beat">
              <template #description>www.instagram.com</template>
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
