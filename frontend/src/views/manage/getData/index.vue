<script setup>
import { ref, computed } from 'vue';
import { message } from 'ant-design-vue';
import { DownloadOutlined, UploadOutlined, DeleteOutlined, CheckCircleOutlined } from '@ant-design/icons-vue';
import { updateZip, updateSqlFile, getUpdateVersion } from '@/api/data';

const current = ref(0);
const loading = ref(false);
const sqlFile = ref(null);
const zipFiles = ref([]);
const uploadRef = ref(null); // 新增：上传组件的引用

const next = () => {
  current.value++;
};
const prev = () => {
  current.value--;
};

const steps = [
  { title: '下载增量包', icon: DownloadOutlined },
  { title: '更新增量包', icon: UploadOutlined },
];

const handleDownload = async () => {
  try {
    loading.value = true;
    const version = await getUpdateVersion();
    message.success(`增量包下载成功，版本: ${version}`);
    next();
  } catch (error) {
    message.error('增量包下载失败');
  } finally {
    loading.value = false;
  }
};

const handleFileUpload = (info) => {
  const { file, fileList } = info;

  // 过滤出当前有效的文件列表
  const currentFiles = fileList.filter(f =>
      (f.name.endsWith('.sql') && (!sqlFile.value || f.uid === sqlFile.value.uid)) ||
      f.name.endsWith('.zip')
  );

  if (file.name.endsWith('.sql')) {
    if (sqlFile.value && file.uid !== sqlFile.value.uid) {
      message.warning('只能上传一个 SQL 文件，之前的文件将被替换');
    }
    sqlFile.value = file;
    // 确保 ZIP 文件保持不变
    zipFiles.value = currentFiles.filter(f => f.name.endsWith('.zip'));
  } else if (file.name.endsWith('.zip')) {
    zipFiles.value = currentFiles.filter(f => f.name.endsWith('.zip'));
  } else {
    message.error(`不支持的文件类型: ${file.name}`);
  }
};

const removeFile = (file) => {
  if (file === sqlFile.value) {
    sqlFile.value = null;
    // 清除上传组件中的 SQL 文件
    if (uploadRef.value) {
      uploadRef.value.fileList = uploadRef.value.fileList.filter(f => f.name.endsWith('.zip'));
    }
  } else {
    zipFiles.value = zipFiles.value.filter(f => f !== file);
    // 更新上传组件的文件列表
    if (uploadRef.value) {
      uploadRef.value.fileList = uploadRef.value.fileList.filter(f =>
          f.uid !== file.uid && (f === sqlFile.value || zipFiles.value.includes(f))
      );
    }
  }
};

const uploadFiles = async () => {
  loading.value = true;
  try {
    if (sqlFile.value) {
      await updateSqlFile(sqlFile.value);
      message.success(`SQL文件 ${sqlFile.value.name} 上传成功`);
    }

    for (const zipFile of zipFiles.value) {
      await updateZip(zipFile);
      message.success(`ZIP文件 ${zipFile.name} 上传成功`);
    }

    // 清空所有文件列表
    sqlFile.value = null;
    zipFiles.value = [];
    if (uploadRef.value) {
      uploadRef.value.fileList = [];
    }
    message.success('所有文件上传完成');
  } catch (error) {
    message.error('文件上传过程中发生错误');
  } finally {
    loading.value = false;
  }
};

const isUploadDisabled = computed(() => !sqlFile.value && zipFiles.value.length === 0);
</script>

<template>
  <div class="file-upload-container">
    <a-steps :current="current">
      <a-step v-for="step in steps" :key="step.title" :title="step.title">
        <template #icon>
          <component :is="step.icon" />
        </template>
      </a-step>
    </a-steps>

    <div class="content-area">
      <template v-if="current === 0">
        <a-button type="primary" @click="handleDownload" :loading="loading" size="large">
          <template #icon><DownloadOutlined /></template>
          下载增量包
        </a-button>
      </template>

      <template v-if="current === 1">
        <a-upload
            ref="uploadRef"
            :multiple="true"
            :beforeUpload="() => false"
            @change="handleFileUpload"
            class="upload-area"
        >
          <a-button size="large">
            <template #icon><UploadOutlined /></template>
            选择文件
          </a-button>
          <p class="upload-tip !text-[red]">请选择一个 SQL 文件和 ZIP 文件(上传过程中，请不要关闭此页面)</p>
        </a-upload>

        <div v-if="sqlFile || zipFiles.length > 0" class="file-list">
          <a-list item-layout="horizontal">
            <a-list-item v-if="sqlFile">
              <a-list-item-meta :description="sqlFile.name">
                <template #title>SQL 文件</template>
                <template #avatar><CheckCircleOutlined style="color: #52c41a;" /></template>
              </a-list-item-meta>
              <template #extra>
                <a-button @click="removeFile(sqlFile)" type="text" danger>
                  <DeleteOutlined />
                </a-button>
              </template>
            </a-list-item>
            <a-list-item v-for="zipFile in zipFiles" :key="zipFile.uid">
              <a-list-item-meta :description="zipFile.name">
                <template #title>ZIP 文件</template>
                <template #avatar><CheckCircleOutlined style="color: #52c41a;" /></template>
              </a-list-item-meta>
              <template #extra>
                <a-button @click="removeFile(zipFile)" type="text" danger>
                  <DeleteOutlined />
                </a-button>
              </template>
            </a-list-item>
          </a-list>
        </div>

        <a-button
            type="primary"
            @click="uploadFiles"
            :disabled="isUploadDisabled"
            :loading="loading"
            size="large"
            class="upload-button"
        >
          开始上传
        </a-button>
      </template>
    </div>

    <div class="steps-action">
      <a-button
          v-if="current < steps.length - 1"
          type="primary"
          @click="next"
          :disabled="current === 0"
      >
        下一步{{ current === 0 ? '(请先下载增量包)' : '' }}
      </a-button>
      <a-button v-if="current > 0" @click="prev">
        上一步
      </a-button>
    </div>
  </div>
</template>

<style scoped>
.file-upload-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 24px;
}

.content-area {
  margin-top: 24px;
  padding: 24px;
  background-color: #f5f5f5;
  border-radius: 8px;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-area {
  text-align: center;
  padding: 20px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  background-color: #fafafa;
  transition: border-color 0.3s;
  width: 100%;
}

.upload-area:hover {
  border-color: #40a9ff;
}

.upload-tip {
  margin-top: 8px;
  color: #888;
}

.file-list {
  width: 100%;
  margin-top: 16px;
}

.upload-button {
  margin-top: 16px;
}

.steps-action {
  margin-top: 24px;
  display: flex;
  justify-content: space-between;
}

@media (max-width: 480px) {
  .file-upload-container {
    padding: 16px;
  }

  .content-area {
    padding: 16px;
  }
}
</style>