<script setup>
import breadcrumb from "@/components/breadcrumb/index.vue";
import Preview from "@/components/preview/index.vue";
import {ref} from "vue";
import {h} from "vue";
import {message} from "ant-design-vue";
import {
  DownloadOutlined,
  ShareAltOutlined,
  EyeOutlined,
  StarOutlined,
  LikeOutlined,
} from "@ant-design/icons-vue";
import {getDocsInfo, getVideoInfo, getVideoUUID} from "@/api/admin/content";
import {getTagsById, getUpdateZip} from "@/api/other.js";
const baseUrl = import.meta.env.VITE_API_BASE_URL;
const imgUrl = baseUrl + "/api/covers/stream/";
import {useRoute} from "vue-router";
import {useVideosInfo} from "@/stores/video.js";

const route = useRoute();

const detail = ref();
const fileUrl = ref();
const refreshKey = ref(1);
const activeId = ref();
const allVideos = ref([]);
const currentInfo = ref();
detail.value = route.query;
const videoInfo = useVideosInfo();
const videoId = route.query.id; // 获取视频 ID

videoInfo.incrementViews(videoId);
async function getInfo() {
  const {data, code} = await getTagsById(detail.value.id);
  if (code === 200) {
    currentInfo.value = data[0];
    if (detail.value?.resourceUuid) {
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
      }
    }
  }
}

getInfo();

function handleResClick(item) {
  fileUrl.value = false;
  fileUrl.value = baseUrl + '/api/videos/stream/' + item.id;
  activeId.value = item.id;
  refreshKey.value = Date.now();
}

function handleDownClick(item) {
  window.open(baseUrl + '/api/videos/stream/' + item.id);
}

const downloadLoading = ref(false);
const handleDownAllClick = async (v)=>{
  downloadLoading.value = true;
  // console.log(item.id)
  let response = await getUpdateZip(v.id);
  let blob = new Blob([response], {type: "application/zip"});
  let url = URL.createObjectURL(blob);
  let a = document.createElement("a");
  a.href = url;
  a.download = `export_${v.name}.zip`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
  message.success("下载成功!");
  downloadLoading.value = false;
}
</script>

<template>
  <a-row :gutter="16" class="mt-10">
    <a-col :span="16">
      <div class="warp h-[500px] flex items-center justify-center">
        <Preview :key="refreshKey" v-if="fileUrl" :fileUrl="fileUrl" fileType="mp4"/>
        <a-spin v-else :spinning="true"></a-spin>
      </div>

      <a-typography class="p-5 bg-[#fff] mt-5 rounded-xl ">
        <a-typography-title>{{ detail.name }}</a-typography-title>
        <a-typography-paragraph>
          {{
            detail.description
          }}
        </a-typography-paragraph>
      </a-typography>
    </a-col>
    <a-col :span="8">
      <a-card :title="'(目录)'" class="mt-5 border-none">
        <template #extra>
          <a-button type="text" :loading="downloadLoading" @click="handleDownAllClick(currentInfo)">打包下载</a-button>
        </template>
        <a-row :gutter="16">
          <a-col v-if="allVideos.length" :span="24" v-for="item in allVideos" :key="item.id">
            <div class="list-container">
              <!-- 理论课程 -->
              <div class="list-item" :class="[activeId === item.id ? 'active':'']" @click="handleResClick(item)">
                <div class="item-left">
                  <div class="circle-icon">
                    <SvgIcon icon="video"/>
                  </div>
                  <span class="item-text">{{ item.title }}</span>
                </div>
                <DownloadOutlined @click.stop="handleDownClick(item)" class="download-icon"/>
              </div>
            </div>
          </a-col>

          <a-col :span="24" v-else>
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
