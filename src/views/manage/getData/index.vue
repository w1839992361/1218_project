<script setup>
import {ref} from 'vue';
import {message} from 'ant-design-vue';
import {getSqlFile, getUpdateZip, getUpdateVersion,createAUpdate} from '@/api/data';

const exporting = ref(false);
const version = ref();

const handleExport = async () => {
  // 将版本转为数字，自增1，然后转换为字符串
  let newVersion = (parseFloat(version.value) + 1).toFixed(1);

// 调用函数并传递新的版本号
  const { data, code } = await createAUpdate({ version: newVersion });
  if(code === 200){
    await getVersion();
    exporting.value = true;
    try {
      // 执行 SQL 文件导出
      let response = await getSqlFile();
      let blob = new Blob([response], {type: 'application/sql'});
      let url = URL.createObjectURL(blob);
      let a = document.createElement('a');
      a.href = url;
      a.download = 'export.sql';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);

      message.success('SQL文件导出成功！');

      // 执行 ZIP 文件导出
      response = await getUpdateZip();
      blob = new Blob([response], { type: 'application/zip' });
      url = URL.createObjectURL(blob);
      a = document.createElement('a');
      a.href = url;
      a.download = 'export.zip';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);

      message.success('ZIP文件导出成功！');
    } catch (error) {
      console.error('Error exporting files:', error);
      message.error('文件导出失败！');
    } finally {
      exporting.value = false;
    }
  }
};

async function getVersion() {
  const {data, code} = await getUpdateVersion()
  console.log(data)
  if (code == 200) {
    version.value = data;
  }
}

getVersion();
</script>

<template>
  <div class="container">
    <h1 class="title">数据导出</h1>
    <div class="flex justify-center items-center">
      <a-card :loading="!version" title="文件导出" bordered class="text-center min-w-[300px]">
        <p>当前版本: {{ version }}</p>
        <a-button
            class="mt-5"
            :loading="exporting"
            @click="handleExport"
        >
          {{ exporting ? '导出中...' : '创建新版本并导出当前版本（zip和sql）' }}
        </a-button>
      </a-card>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 16px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
  text-align: center;
}

.grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

@media (min-width: 768px) {
  .grid {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
