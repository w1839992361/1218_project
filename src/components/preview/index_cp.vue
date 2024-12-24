<script setup>
import { onMounted, ref, nextTick } from 'vue';
import { getFile } from '@/api/preview';

// pdf
import VueOfficePdf from '@vue-office/pdf'

// excel 
import VueOfficeExcel from '@vue-office/excel'
import '@vue-office/excel/lib/index.css'

// docx
import VueOfficeDocx from '@vue-office/docx'
import '@vue-office/docx/lib/index.css'

import { renderAsync } from 'docx-preview'

// pptx
import VueOfficePptx from '@vue-office/pptx'

const files = [
  'http://150.109.233.199/assets/1.doc',
  // 'http://150.109.233.199/assets/1.docx',
  'http://150.109.233.199/assets/1.ppt',
  // 'http://150.109.233.199/assets/1.pptx',
  // 'http://150.109.233.199/assets/1.xlsx',
  // 'http://150.109.233.199/assets/1.xls',
  // 'http://150.109.233.199/assets/1.mp4',
  // 'http://150.109.233.199/assets/1.html',
  // 'http://150.109.233.199/assets/1.txt',
  // 'http://150.109.233.199/assets/1.pdf',
];

const props = defineProps({
  fileUrl: {
    type: String,
    // default: 'http://150.109.233.199/assets/1.doc'
    default: '1.doc'
  },
});

const fileType = ref('');
const previewContent = ref('');
const previewUrl = ref('');
const docxDiv = ref();
// const xlsxDom = ref();
const loadFilePreview = async () => {

  const fileExtension = props.fileUrl.split('.').pop().toLowerCase();
  fileType.value = fileExtension;
  if (fileExtension === 'pdf') {
    await renderPdf(props.fileUrl);
  } else if (['ppt', 'pptx'].includes(fileExtension)) {
    await renderPptx(props.fileUrl);
  } else if (['doc', 'docx'].includes(fileExtension)) {
    if (fileExtension === 'doc') {
      nextTick(() => {
        renderDocx(props.fileUrl, docxDiv);
      })
    }
  } else if (['xls', 'xlsx'].includes(fileExtension)) {
    if (fileExtension === 'xls') {
      await renderXls(props.fileUrl, xlsxDom);
    }
  } else if (['txt'].includes(fileExtension)) {
    await renderTxt(props.fileUrl);
  } else if (['html'].includes(fileExtension)) {
  } else if (['mp4'].includes(fileExtension)) {
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

const renderDocx = async (url, dom) => {
  // 渲染文件内容
  getFile(url).then(res => {
    renderAsync(res, document.getElementById('docxDiv'), null, {
      inWrapper: true,
      ignoreWidth: false,
      ignoreHeight: false,
      ignoreFonts: false,
      breakPages: true,
      ignoreLastRenderedPageBreak: true,
      experimental: false,
      trimXmlDeclaration: true,
      debug: false,
    });
  })
  // renderAsync(url, dom)

};

const renderXls = async (url, dom) => {

}

const renderExcel = async (url, dom) => {

}

// 渲染 TXT 文件
const renderTxt = async (url) => {
  const response = await fetch(url);
  previewContent.value = await response.text();
};

onMounted(loadFilePreview);
</script>

<template>
  <div class="preview">
    <div ref="docxDiv" id="docxDiv" class="h-[100%] w-[100%]">

    </div>
    <!-- <h3>文件预览：{{ props.fileUrl }}</h3> -->
    <iframe v-if="fileType === 'html'" :src="props.fileUrl" class="w-[100%] h-[500px]" />

    <vue-office-pdf v-else-if="fileType === 'pdf'" :src="previewUrl" class="h-[100%] w-[100%]" />

    <vue-office-docx v-else-if="fileType === 'docx'" :src="props.fileUrl" class="h-[100%] w-[100%]" />

    <!-- <vue-doc-viewer v-else-if="fileType === 'doc'" :src="props.fileUrl" class="h-[100%] w-[100%]" /> -->
    <!-- <div ref="docxDiv" id="docxDiv" v-else-if="fileType === 'doc'" class="h-[100%] w-[100%]">

    </div> -->

    <vue-office-pptx v-else-if="fileType === 'pptx'" :src="props.fileUrl" class="h-[100%] w-[100%]" />

    <div v-else-if="fileType === 'ppt'" class="h-[100%] w-[100%]">
      pptx
    </div>

    <vue-office-excel :options="{ xls: fileType === 'xls' }" v-else-if="fileType === 'xlsx' || fileType === 'xls'"
      :src="props.fileUrl" class="h-[100%] w-[100%]" />


    <div v-else-if="fileType === 'mp4'" class="h0-[100%]">
      <video controls width="100%" class="h-[100%]">
        <source :src="props.fileUrl" />
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
</style>
