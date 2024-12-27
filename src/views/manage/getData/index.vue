<script setup>
import { ref } from 'vue'
import { SyncOutlined } from '@ant-design/icons-vue'
import { getSqlFile, getUpdateZip, updateZip, updateSqlFile, getUpdateVersion } from '@/api/data';


const status = ref({
    version: '',
    lastSync: null,
    isSyncing: false,
    progress: 0
})

const { data, code } = await getUpdateVersion()
if (code == 200) {
    status.value.version = data;
}
if (window.localStorage.getItem('lastSync')) {
    status.value.lastSync = window.localStorage.getItem('lastSync');
}

let fakeProgressInterval = null

const startFakeProgress = (maxProgress = 95, interval = 1000) => {
    return new Promise((resolve) => {
        fakeProgressInterval = setInterval(() => {
            if (status.value.progress >= maxProgress) {
                clearInterval(fakeProgressInterval)
                resolve()
            } else {
                const increment = (Math.random() * 5 + 2).toFixed(2)
                status.value.progress = Math.min(maxProgress, parseFloat((status.value.progress + parseFloat(increment)).toFixed(2)))
            }
        }, interval)
    })
}

const handleSync = async () => {
    try {
        status.value.isSyncing = true
        status.value.progress = 0

        startFakeProgress()

        const sqlResponse = await getSqlFile();
        if (sqlResponse) {

            // 创建 Blob 对象并包装为 FormData
            const sqlBlob = new Blob([sqlResponse], { type: 'text/plain;charset=utf-8' });
            const sqlFormData = new FormData();
            sqlFormData.append('sqlFile', sqlBlob, 'data.sql'); // 给文件命名

            // 自动上传 SQL 文件
            const uploadSqlResponse = await updateSqlFile(sqlFormData);
            if (uploadSqlResponse && uploadSqlResponse.code === 200) {
                console.log('SQL 文件上传成功');
            } else {
                throw new Error('SQL 文件上传失败');
            }
        } else {
            throw new Error('获取 SQL 文件内容失败');
        }

        // 2. 获取 ZIP 文件并保存到本地
        const zipResponse = await getUpdateZip({
            responseType: 'blob',
        });

        if (zipResponse) {
            // 创建 Blob 对象并包装为 FormData
            const zipBlob = new Blob([zipResponse], { type: 'application/zip' });
            const zipFormData = new FormData();
            zipFormData.append('zipFile', zipBlob, 'update.zip'); // 给文件命名

            // 自动上传 ZIP 文件
            const uploadZipResponse = await updateZip(zipFormData);
            if (uploadZipResponse && uploadZipResponse.code === 200) {
                console.log('ZIP 文件上传成功');
            } else {
                throw new Error('ZIP 文件上传失败');
            }
        } else {
            throw new Error('获取 ZIP 文件失败');
        }

        // const sqlResponse = await getSqlFile();
        // if (sqlResponse) {
        //     const sqlBlob = new Blob([sqlResponse], { type: 'text/plain;charset=utf-8' });
        //     const sqlUrl = window.URL.createObjectURL(sqlBlob);
        //     const sqlLink = document.createElement('a');
        //     sqlLink.href = sqlUrl;
        //     sqlLink.download = 'data.sql'; // 设置下载的文件名
        //     sqlLink.click();
        //     window.URL.revokeObjectURL(sqlUrl); // 释放 URL
        // }
        // const zipResponse = await getUpdateZip();

        // if (zipResponse) {
        //     // 创建 Blob 对象
        //     const blob = new Blob([zipResponse.data], { type: 'application/zip' });
        //     const url = window.URL.createObjectURL(blob);
        //     const link = document.createElement('a');
        //     link.href = url;
        //     link.download = 'update.zip'; // 设置下载的文件名
        //     link.click();
        //     window.URL.revokeObjectURL(url); // 释放 URL
        // }


        status.value.progress = 100
        const now = new Date();
        now.setHours(now.getHours() + 8);
        status.value.lastSync = now.toISOString().replace('T', ' ').slice(0, 19);
        window.localStorage.setItem('lastSync', status.value.lastSync);
    } catch (error) {
        console.error('同步失败:', error)
    } finally {
        if (fakeProgressInterval) clearInterval(fakeProgressInterval)
        status.value.isSyncing = false
    }
}
</script>

<template>
    <a-card :bordered="false" class="data-sync-card">
        <template #title>
            <div class="flex items-center justify-between">
                <span class="text-lg font-semibold">数据同步</span>
                <a-button type="primary" shape="circle" :loading="status.isSyncing" @click="handleSync">
                    <template #icon>
                        <SyncOutlined />
                    </template>
                </a-button>
            </div>
        </template>
        <div class="data-sync-content">
            <a-descriptions :column="1">
                <a-descriptions-item label="当前版本">
                    {{ status.version }}
                </a-descriptions-item>
                <a-descriptions-item label="上次同步时间">
                    {{ status.lastSync || '从未同步' }}
                </a-descriptions-item>
            </a-descriptions>
            <a-progress v-if="status.isSyncing" :percent="status.progress" size="small" class="mt-4" />
        </div>
    </a-card>

</template>

<style scoped>
.data-sync-card {
    margin: 24px;
    min-height: 200px;
}

.data-sync-content {
    padding: 0 12px;
}

:deep(.ant-descriptions-item-label) {
    color: rgba(0, 0, 0, 0.45);
    min-width: 120px;
}

:deep(.ant-descriptions-item-content) {
    justify-content: flex-end;
}
</style>
