<script setup>
import breadcrumb from "@/components/breadcrumb/index.vue";
import {ref,watch} from "vue";
import {RightOutlined} from "@ant-design/icons-vue";
import {message} from "ant-design-vue";
import { useRouter, useRoute } from "vue-router";

import { getTagsById} from "@/api/other";
import {useVideosInfo} from "@/stores/video.js";
// import {getDocsInfo, getVideoInfo, getVideoUUID} from "@/api/admin/content";

const baseUrl = import.meta.env.VITE_API_BASE_URL;
const imgUrl = baseUrl + "/api/covers/stream/";

const route = useRoute();
const router = useRouter();

const videoInfo = useVideosInfo();
const subjects = ref([]);

function handlePageClick(info) {
  router.push({ name: "SubjectDetail" ,query:{...info}});
}

function handlePageBannerClick(id = route.query.id,selectId) {
  router.push({name:'SubjectColumn',query:{id,selectId}})
}


async function getList() {
  const {data, code} = await getTagsById(route.query.id);
  if (code === 200) {
    subjects.value = data[0]?.children ?? [];
    console.log(data)
  }
}

watch(
    () => route.query,
    (newName) => {
      getList();
    }
);
getList();
</script>

<template>
  <!-- <breadcrumb /> -->
  <template v-for="item in subjects" :key="item.id" v-if="subjects.length">
    <a-row>
      <a-col :span="12">
        <a-typography-title style="margin: 30px 0 20px 0" :level="4"
        >{{ item.name }}
        </a-typography-title
        >
      </a-col>
      <a-col
          :span="12"
          style="
          text-align: right;
          display: flex;
          align-items: center;
          justify-content: end;
        "
      >
        <a-button @click="handlePageBannerClick(undefind,item.id)" type="text"
        >更多
          <RightOutlined/>
        </a-button>
      </a-col>
    </a-row>
    <a-card class="page border-transparent">
      <a-row :gutter="16">
        <a-col :span="12" class="cursor-pointer" @click="handlePageBannerClick(undefind,item.id)">
          <img
              class="h-[340px] w-[100%] object-cover object-top"
              :src="imgUrl+item.coverUuid"
              alt=""
          />
        </a-col>
        <a-col :span="12" class="cursor-pointer" >
          <a-row :gutter="16" v-for="child in item.children" :key="child.id" @click="handlePageClick(child)">
            <a-col :span="9">
              <img
                  alt="preview"
                  :src="imgUrl+child.coverUuid"
                  class="w-[100%] h-[110px] mb-1 object-cover object-center"
              />
            </a-col>
            <a-col :span="11">
              <a-card-meta
                  :title="child.name"
              >
                <template #description>
                  <div class="description">
                    {{item.description}}
                  </div>
                </template>
              </a-card-meta>

              <span style="display: block; margin-top: 10px; color: #ccc"
              >浏览量: {{  videoInfo.getVideoData(child.id)?.views }}</span
              >
            </a-col>
          </a-row>
        </a-col>
      </a-row>
    </a-card>
  </template>
  <a-empty  v-else :description="null" />
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
