<script setup>
import breadcrumb from "@/components/breadcrumb/index.vue";
import Preview from "@/components/preview/index.vue";
import {ref} from "vue";
import {h} from "vue";
import {message} from "ant-design-vue";
import {getTagsById} from "@/api/other";
import {getDocsInfo, getVideoInfo, getVideoUUID} from "@/api/admin/content";

import {
  DownloadOutlined,
  ShareAltOutlined,
  EyeOutlined,
  StarOutlined,
  LikeOutlined,
} from "@ant-design/icons-vue";

import {useRoute} from "vue-router";

const route = useRoute();

const rateValue = ref(5);

const detail = ref();
const currentInfo = ref();
const activeId = ref();

const baseUrl = import.meta.env.VITE_API_BASE_URL;

const allVideos = ref([]);
const allDocs = ref([]);
detail.value = route.query;
const localData = ref({});

// initLocalData();

// 初始化本地存储
function initLocalData() {
  const storedData = localStorage.getItem("myVideo");
  if (storedData) {
    localData.value = JSON.parse(storedData);
    if (localData.value[detail.value.id]) {
      Object.assign(currentInfo.value, localData.value[detail.value.id]);
    }
  }
}

// 点赞、收藏事件
function handleOperate(key, num) {
  const currentId = detail.value.id;
  const updatedNum = num + 1;

  if (!localData.value[currentId]) {
    localData.value[currentId] = {};
  }
  localData.value[currentId][key] = updatedNum;
  currentInfo.value[key] = updatedNum;

  localStorage.setItem("myVideo", JSON.stringify(localData.value));
  message.success(`${key === "favoriteNum" ? "收藏" : "点赞"}成功！`);
}
console.log(detail);


const fileUrl = ref('');
const fileType = ref('');
const refreshKey = ref(1)

function handleResClick(item) {
  fileUrl.value = false;
  fileType.value = false;
  if (item?.fileName) {
    const parts = item?.fileName.split('.');
    if (parts.length > 1) {
      fileType.value = parts.pop();
      fileUrl.value = baseUrl + '/api/docs/stream/' + item.id;
    }
  } else {
    fileType.value = "mp4";
    fileUrl.value = baseUrl + '/api/videos/stream/' + item.id;
  }
  activeId.value = item.id;
  refreshKey.value = Date.now();
}

function handleDownClick(item) {
  // console.log(item)
  // console.log(item.id)
  // console.log(typeof item.id)
  // return
  if (!item?.fileName) {
    window.open(baseUrl + '/api/videos/stream/' + item.id);
  } else {
    const fileUrl = baseUrl + '/api/docs/stream/' + item.id;
    const a = document.createElement('a');
    a.href = fileUrl;
    a.download = item.fileName; //
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }
  console.log(item)
  message.success("single!");
}

async function getInfo() {
  const {data, code} = await getTagsById(detail.value.id);
  if (code === 200) {
    currentInfo.value = data[0];
    if (detail.value?.resourceUuid) {
      let docInfo = await getDocsInfo(detail.value?.resourceUuid);
      let videoInfo = [];
      let videos = await getVideoUUID(detail.value?.resourceUuid);
      if (videos.code === 200 && videos.data.length) {
        videoInfo = await Promise.all(
            videos.data.map(async (item) => {
              return (await getVideoInfo(item)).data;
            })
        );
        activeId.value = videoInfo[0]?.id;
        handleResClick(videoInfo[0])
        allVideos.value = videoInfo;
        allDocs.value = docInfo.data;
      }
    }
  }
}

getInfo();

function handleCopy() {
  const currentUrl = window.location.href;
  navigator.clipboard
      .writeText(currentUrl)
      .then(() => {
        message.success("已复制分享链接");
      })
      .catch((err) => {
        message.warning("复制分享链接失败");
      });
}


</script>

<template>
  <a-row :gutter="16">
    <!-- <a-col :span="10"> -->
    <!-- <breadcrumb :navs="[]" /> -->
    <!-- </a-col> -->
    <a-col :span="24" class="flex items-center justify-end">
      <a-rate v-model:value="rateValue" allow-half/>
      <span style="margin-left: 10px">{{ rateValue }}分</span>

      <a-button type="text" :icon="h(StarOutlined)" @click="handleOperate('favoriteNum',currentInfo?.favoriteNum)" shape="round"> 收藏({{ currentInfo?.favoriteNum }})</a-button>
      <a-button type="text" shape="round" @click="handleOperate('subNum',currentInfo?.subNum)" >
        <template #icon>
          <LikeOutlined/>
        </template>
        点赞({{ currentInfo?.subNum }})
      </a-button>
      <a-button type="text" shape="round">
        <template #icon>
          <EyeOutlined/>
          {{ currentInfo?.viewNum }}
        </template>
      </a-button>
      <a-button type="text" shape="round" @click="handleCopy">
        <template #icon>
          <ShareAltOutlined />
        </template>
        分享链接
      </a-button>
    </a-col>
  </a-row>

  <a-row :gutter="16" class="mt-10">
    <a-col :span="16">
      <div class="warp h-[500px] flex items-center justify-center">
        <Preview :key="refreshKey" v-if="fileUrl && fileType" :fileUrl="fileUrl" :fileType="fileType"/>
        <a-spin v-else :spinning="true"></a-spin>

      </div>

      <a-typography class="pt-5">
        <a-typography-title>{{ currentInfo?.name }}</a-typography-title>
        <a-typography-paragraph>
          {{ currentInfo?.description }}
        </a-typography-paragraph>
      </a-typography>
    </a-col>
    <a-col :span="8">
      <a-card title="授课老师">
        <a-row :gutter="16">
          <a-col>
            <a-image
                :width="100"
                :src="`https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png`"
            >
            </a-image>
          </a-col>
          <a-col>
            <div>
              <h3>XXXXX</h3>
              <p>1111</p>
            </div>
          </a-col>
        </a-row>
      </a-card>

      <a-card title="学习清单" class="mt-5 border-none">
        <template #extra>
          <a-button type="text" @click.stop="handleDownClick(detail)">打包下载</a-button>
        </template>
        <a-row :gutter="16">
          <a-col :span="24" v-for="item in allVideos" :key="item.id">
            <div class="list-container">
              <!-- 理论课程 -->
              <div class="list-item" :class="[activeId === item.id ? 'active':'']" @click="handleResClick(item)">
                <div class="item-left">
                  <div class="circle-icon">
                    <SvgIcon icon="video"/>
                  </div>
                  <span class="item-text">视频课程( {{ item.title }} )</span>
                </div>
                <DownloadOutlined @click.stop="handleDownClick(item)" class="download-icon"/>
              </div>
            </div>
          </a-col>

          <a-col :span="24" v-for="item in allDocs" :key="item.id">
            <div class="list-container">
              <!-- 理论课程 -->
              <div class="list-item" @click="handleResClick(item)">
                <div class="item-left">
                  <div class="circle-icon blue">
                    <SvgIcon icon="paper" width="20" height="20"/>
                  </div>
                  <span class="item-text">{{ (item.fileName) }}</span>
                </div>
                <DownloadOutlined @click.stop="handleDownClick(item)" class="download-icon"/>
              </div>
            </div>
          </a-col>
          <a-col :span="24" v-if="allVideos.length ==0 && allDocs.length ==0">
            <a-empty   :description="null" />
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
