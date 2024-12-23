<script setup>
import breadcrumb from '@/components/breadcrumb/index.vue';
import Preview from '@/components/preview/index.vue'
import { ref } from 'vue';
import { DownloadOutlined, ShareAltOutlined, EyeOutlined, StarOutlined, LikeOutlined } from '@ant-design/icons-vue';

const rateValue = ref(2.5);
</script>

<template>
  <a-row :gutter="16">
    <a-col :span="10">
      <breadcrumb :navs="[]" />
    </a-col>
    <a-col :span="14">
      <a-row :gutter="16">
        <a-col :span="11">
          <a-rate v-model:value="rateValue" allow-half />
          <span style="margin-left:10px;">{{rateValue}}分 (111)个</span>
          <a-button style="margin-left:10px;" shape="round" type="primary" size="small">去评价</a-button>
        </a-col>

        <a-col :span="13">
          <a-button type="text" shape="round" >
            <template #icon>
              <StarOutlined />
            </template>
            收藏
          </a-button>
          <a-button type="text" shape="round" >
            <template #icon>
              <LikeOutlined />
            </template>
            点赞
          </a-button>
          <a-button type="text" shape="round" >
            <template #icon>
              <EyeOutlined />
            </template>
            (1w)已学
          </a-button>
          <a-button type="text" shape="round" >
            <template #icon>
              <ShareAltOutlined />
            </template>
            分享链接
          </a-button>
        </a-col>
      </a-row>
    </a-col>
  </a-row>

  <a-row :gutter="16" class="mt-5">
    <a-col :span="16">
      <Preview/>
    </a-col>
    <a-col :span="8" style="background:blue;"></a-col>
  </a-row>
</template>


<style scoped></style>