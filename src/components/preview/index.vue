<script setup>
import {onMounted, ref, nextTick} from 'vue';
import {getFile} from '@/api/preview';

// pdf
import VueOfficePdf from '@vue-office/pdf'

// excel 
import VueOfficeExcel from '@vue-office/excel'
import '@vue-office/excel/lib/index.css'

// docx
import VueOfficeDocx from '@vue-office/docx'
import '@vue-office/docx/lib/index.css'


// pptx
import VueOfficePptx from '@vue-office/pptx'

const files = [
  'http://150.109.233.199/assets/1.doc',
  'http://150.109.233.199/assets/1.ppt',


  // 'http://150.109.233.199/assets/1.docx',
  // 'http://150.109.233.199/assets/1.pptx',
  // 'http://150.109.233.199/assets/1.xlsx',
  // 'http://150.109.233.199/assets/1.xls',
  // 'http://150.109.233.199/assets/1.mp4',
  // 'http://150.109.233.199/assets/1.html',
  // 'http://150.109.233.199/assets/1.txt',
  // 'http://150.109.233.199/assets/1.pdf',
];

const baseUrl = import.meta.env.VITE_API_BASE_URL;

const props = defineProps({
  fileUrl: {
    type: String,
    // default: baseUrl + '/api/videos/stream/427f9fd6-61b5-46a7-81b4-8698ba3a7633' // video
    default: '/api/docs/stream/ppt/497cc880-e1a5-45d5-80ba-d6ecc67ca448' // video
  },
  fileType: {
    type: String,
    required: true,
    default: 'pptx'
  }
});

const fileType = ref('');
const previewContent = ref('');
const loading = ref(true);
const loadFilePreview = async () => {

  // const fileExtension = props.fileUrl.split('.').pop().toLowerCase();
  // fileType.value = fileExtension ;
  fileType.value = props.fileType;
  const fileExtension = props.fileType;
  // console.log(fileType.value)
  if (fileExtension === 'pdf') {
  } else if (['ppt', 'pptx'].includes(fileExtension)) {
  } else if (['doc', 'docx'].includes(fileExtension)) {
  } else if (['xls', 'xlsx'].includes(fileExtension)) {
  } else if (['txt'].includes(fileExtension)) {
    await renderTxt(props.fileUrl);
  } else if (['html'].includes(fileExtension)) {
    await renderHtml(props.fileUrl);
  } else if (['mp4'].includes(fileExtension)) {
  } else {
    previewContent.value = '不支持的文件类型';
  }
};

// console.log(1111)

// 渲染 TXT 文件
const renderTxt = async (url) => {
  const response = await fetch(url);
  previewContent.value = await response.text();
  loading.value = false;
};

const renderHtml = async (url) => {
  const response = await fetch(url);
  previewContent.value = await response.text();
  loading.value = false;
};

function renderedHandler() {
  loading.value = false;
}

onMounted(loadFilePreview);
</script>

<template>
  <div class="preview">
    <a-spin class="laoding" v-if="loading"/>
    <div v-if="fileType === 'html'"  v-html="previewContent" class="w-[100%] h-[500px]"/>

    <vue-office-pdf @rendered="renderedHandler" v-else-if="fileType === 'pdf'" :src="props.fileUrl"
                    class="h-[100%] w-[100%]"/>

    <vue-office-docx @rendered="renderedHandler" v-else-if="fileType === 'docx'" :src="props.fileUrl"
                     class="h-[100%] w-[100%]"/>

    <vue-office-pptx @rendered="renderedHandler" v-else-if="fileType === 'pptx'" :src="props.fileUrl"
                     class="h-[100%] w-[100%]"/>


    <vue-office-excel @rendered="renderedHandler" :options="{ xls: fileType === 'xls' }"
                      v-else-if="fileType === 'xlsx' || fileType === 'xls'" :src="props.fileUrl"
                      class="h-[100%] w-[100%]"/>

    <div v-else-if="fileType === 'mp4'" class="h0-[100%]">
      <video @canplay="renderedHandler" controls width="100%" class="h-[100%]">
        <source :src="props.fileUrl"/>
        您的浏览器不支持视频播放。
      </video>
    </div>
    <div v-else>
      <pre>{{ previewContent }}</pre>
    </div>
  </div>
</template>

<style scoped>
.preview {
  width: 100%;
  height: 500px;
  overflow-x: auto;
  position: relative;
}

h3 {
  margin-bottom: 10px;
}

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  background-color: #f4f4f4;
  padding: 10px;
  border-radius: 5px;
  font-weight: bold;
}

iframe {
  border: none;
}

:deep(.toolbar) {
  display: none;
  height: 0 !important;
}

:deep(.pdf-app) #viewerContainer {
  top: 0 !important;
}

.laoding {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
