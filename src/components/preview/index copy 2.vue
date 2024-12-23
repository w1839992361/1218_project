<script setup>
import { onMounted, ref } from 'vue';
import JSZip from 'jszip';
import { parseStringPromise } from 'xml2js';
import * as pdfjsLib from 'pdfjs-dist';
import PptxGenJS from 'pptxgenjs';
import mammoth from 'mammoth'; // 用于解析 Word 文件
import * as XLSX from 'xlsx'; // 用于解析 Excel 文件
pdfjsLib.GlobalWorkerOptions.workerSrc = '/pdf.worker.min.js'

const props = defineProps({
  fileUrl: {
    type: String,
    required: true,
    default: 'http://150.109.233.199/assets/1.doc',
  },
});

const fileType = ref('');
const previewContent = ref('');


const loadFilePreview = async () => {
  const pptPreview = ref();
  const fileExtension = props.fileUrl.split('.').pop().toLowerCase();
  fileType.value = fileExtension;

  if (fileExtension === 'pdf') {
    await renderPdf(props.fileUrl);
  } else if (['ppt', 'pptx'].includes(fileExtension)) {
    await renderPptx(props.fileUrl, pptPreview);
  } else if (['doc', 'docx'].includes(fileExtension)) {
    await renderDocx(props.fileUrl);
  } else if (['xls', 'xlsx'].includes(fileExtension)) {
    await renderExcel(props.fileUrl);
  } else if (['txt'].includes(fileExtension)) {
    await renderTxt(props.fileUrl);
  } else if (['html'].includes(fileExtension)) {
    previewContent.value = props.fileUrl;
  } else if (['mp4'].includes(fileExtension)) {
    previewContent.value = props.fileUrl;
  } else {
    previewContent.value = '不支持的文件类型';
  }
};

// 渲染 PDF 文件
const renderPdf = async (url) => {

};

// 渲染 PPT/PPTX 文件
const renderPptx = async (url, dom) => {

};

// 渲染 DOC/DOCX 文件
const renderDocx = async (url) => {

};

// 渲染 Excel 文件
const renderExcel = async (url) => {

};

// 渲染 TXT 文件
const renderTxt = async (url) => {
  const response = await fetch(url);
  previewContent.value = await response.text();
};

onMounted(loadFilePreview);
</script>

<template>
  <div class="preview">
    <!-- <h3>文件预览：{{ props.fileUrl }}</h3> -->
    <div v-if="fileType === 'html'">
      html
      <!-- <iframe :src="props.fileUrl" width="100%" class="h-[100%]" /> -->
    </div>

    <div v-else-if="fileType ==='pdf'" class="h-[100%]">
      pdf
      <div ref="pptPreview"></div>
    </div>
    <div v-else-if="fileType ==='doc' || fileType ==='docx'" class="h-[100%]">
      docx
    </div>
    <div v-else-if="fileType ==='xls' || fileType ==='xlsx'" class="h-[100%]">
      xlsx
    </div>
    <div v-else-if="fileType ==='mp4'" class="h-[100%]">
      <video controls width="100%" class="h-[100%]">
        <source :src="previewContent" />
        您的浏览器不支持视频播放。
      </video>
    </div>
    <div v-else>
      other
      <pre>{{ previewContent }}</pre>
    </div>
  </div>
</template>

<style scoped>
.preview {
  width: 100%;
  height: 500px;
  overflow-x: auto;
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
}
iframe {
  border: none;
}
</style>
