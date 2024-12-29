<script setup>
import {ref} from 'vue';
import {message} from 'ant-design-vue';
import {UploadOutlined, ReloadOutlined} from '@ant-design/icons-vue';

import {updateZip, updateSqlFile, getUpdateVersion} from '@/api/data';

// 状态变量
const version = ref('');
const sqlLastSync = ref(null);
const zipLastSync = ref(null);
const sqlProgress = ref(0);
const zipProgress = ref(0);
const sqlError = ref(null);
const zipError = ref(null);
const uploadingSql = ref(false);
const uploadingZip = ref(false);

// SQL文件上传处理
const uploadSql = async ({file, onProgress}) => {
  try {
    uploadingSql.value = true;
    sqlError.value = null;
    sqlProgress.value = 0;

    if (!file.name.endsWith('.sql')) {
      throw new Error('请上传SQL文件');
    }

    const formData = new FormData();
    formData.append('sqlFile', file);

    const response = await updateSqlFile(formData);
    if (response.code!==200) {
      throw new Error('SQL文件上传失败');
    }

    getVersion();
    message.success('SQL文件上传成功');
  } catch (error) {
    sqlError.value = error.message;
    message.error(error.message);
  } finally {
    uploadingSql.value = false;
    sqlProgress.value = 100;
    setTimeout(() => {
      sqlProgress.value = 0;
    }, 1000);
  }
};

// ZIP文件上传处理
const uploadZip = async ({file, onProgress}) => {
  try {
    uploadingZip.value = true;
    zipError.value = null;
    zipProgress.value = 0;

    if (!file.name.endsWith('.zip')) {
      throw new Error('请上传ZIP文件');
    }

    const formData = new FormData();
    formData.append('zipFile', file);
    const response = await updateZip(formData);
    if (response.code!==200) {
      throw new Error('ZIP文件上传失败');
    }

    message.success('ZIP文件上传成功');
    getVersion();
  } catch (error) {
    zipError.value = error.message;
    message.error(error.message);
  } finally {
    uploadingZip.value = false;
    zipProgress.value = 100;
    setTimeout(() => {
      zipProgress.value = 0;
    }, 1000);
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
  <div class="file-upload-container">
    <a-row :gutter="[16, 16]">
      <a-col :span="24">
        <h1 class="page-title">数据上传(<span class="text-[red]">上传过程中请勿关闭该页面</span>)</h1>
      </a-col>

      <!-- SQL文件上传卡片 -->
      <a-col :xs="24" :md="12">
        <a-card title="SQL文件上传">
          <!-- <template #extra>
              <a-button type="link" @click="refreshPage">
                  <template #icon>
                      <ReloadOutlined />
                  </template>
</a-button>
</template> -->
          <a-row :gutter="[16, 16]">
            <a-col :span="12">
              <p>当前版本: {{ version }}</p>
            </a-col>
            <!--                        <a-col :span="12">-->
            <!--                            <p>上次同步时间: {{ sqlLastSync || '从未同步' }}</p>-->
            <!--                        </a-col>-->
          </a-row>
          <a-upload :customRequest="uploadSql" :showUploadList="false" accept=".sql" :disabled="uploadingSql">
            <a-button :loading="uploadingSql">
              <upload-outlined/>
              上传SQL文件
            </a-button>
          </a-upload>
          <a-progress v-if="sqlProgress > 0" :percent="sqlProgress" size="small" status="active"
                      class="upload-progress"/>
          <a-alert v-if="sqlError" type="error" :message="sqlError" class="upload-error" showIcon/>
        </a-card>
      </a-col>

      <!-- ZIP文件上传卡片 -->
      <a-col :xs="24" :md="12">
        <a-card title="ZIP文件上传">
          <!-- <template #extra>
              <a-button type="link" @click="refreshPage">
                  <template #icon>
                      <ReloadOutlined />
                  </template>
              </a-button>
          </template> -->
          <a-row :gutter="[16, 16]">
            <a-col :span="12">
              <p>当前版本: {{ version }}</p>
            </a-col>
            <!--                        <a-col :span="12">-->
            <!--                            <p>上次同步时间: {{ zipLastSync || '从未同步' }}</p>-->
            <!--                        </a-col>-->
          </a-row>
          <a-upload :customRequest="uploadZip" :showUploadList="false" accept=".zip" :disabled="uploadingZip">
            <a-button :loading="uploadingZip">
              <upload-outlined/>
              上传ZIP文件
            </a-button>
          </a-upload>
          <a-progress v-if="zipProgress > 0" :percent="zipProgress" size="small" status="active"
                      class="upload-progress"/>
          <a-alert v-if="zipError" type="error" :message="zipError" class="upload-error" showIcon/>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>


<style scoped>
.file-upload-container {
  padding: 24px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 24px;
}

.upload-progress {
  margin-top: 16px;
}

.upload-error {
  margin-top: 16px;
}
</style>