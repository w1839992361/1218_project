<script setup>
import { ref } from 'vue';

const columns = [
  {
    title: '栏目',
    dataIndex: 'category',
    key: 'category',
  },
  {
    title: '资源个数',
    dataIndex: 'resourceCount',
    key: 'resourceCount',
    sorter: (a, b) => a.resourceCount - b.resourceCount,
  },
  {
    title: '单位资源贡献数',
    dataIndex: 'unitContribution',
    key: 'unitContribution',
    sorter: (a, b) => a.unitContribution - b.unitContribution,
  },
  {
    title: '下载量',
    dataIndex: 'downloads',
    key: 'downloads',
    sorter: (a, b) => a.downloads - b.downloads,
  },
  {
    title: '平均评星',
    dataIndex: 'averageRating',
    key: 'averageRating',
    sorter: (a, b) => a.averageRating - b.averageRating,
  },
];

const dataSource = ref([
  {
    key: '1',
    category: '学科课程',
    resourceCount: 1000,
    unitContribution: 50,
    downloads: 5000,
    averageRating: 4.5,
  },
  {
    key: '2',
    category: '拓展课程',
    resourceCount: 500,
    unitContribution: 30,
    downloads: 2500,
    averageRating: 4.2,
  },
  {
    key: '3',
    category: '专题教育',
    resourceCount: 300,
    unitContribution: 20,
    downloads: 1500,
    averageRating: 4.7,
  },
]);

const totalResources = ref(0);
const totalDownloads = ref(0);
const averageRating = ref(0);

const animateValues = () => {
  const targetResources = dataSource.value.reduce((acc, curr) => acc + curr.resourceCount, 0);
  const targetDownloads = dataSource.value.reduce((acc, curr) => acc + curr.downloads, 0);
  const targetAverageRating = (dataSource.value.reduce((acc, curr) => acc + curr.averageRating, 0) / dataSource.value.length).toFixed(1);

  let step = 0;
  const steps = 50;
  const interval = setInterval(() => {
    step++;
    totalResources.value = Math.round((targetResources / steps) * step);
    totalDownloads.value = Math.round((targetDownloads / steps) * step);
    averageRating.value = (targetAverageRating * step / steps).toFixed(1);

    if (step >= steps) clearInterval(interval);
  }, 20);
};

animateValues();

const handleTableChange = (pagination, filters, sorter) => {
  console.log('Various parameters', pagination, filters, sorter);
};
</script>

<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-6">课程统计</h1>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <a-card>
        <template #title>总资源数</template>
        <template #extra><a-tag color="blue">总计</a-tag></template>
        <p class="text-2xl font-bold">{{ totalResources }}</p>
      </a-card>
      <a-card>
        <template #title>总下载量</template>
        <template #extra><a-tag color="green">总计</a-tag></template>
        <p class="text-2xl font-bold">{{ totalDownloads }}</p>
      </a-card>
      <a-card>
        <template #title>平均评星</template>
        <template #extra><a-tag color="orange">平均</a-tag></template>
        <p class="text-2xl font-bold">{{ averageRating }}</p>
      </a-card>
    </div>

    <a-table 
      :columns="columns" 
      :dataSource="dataSource"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, text }">
        <template v-if="column.key === 'averageRating'">
          <a-rate :value="text" disabled allow-half />
        </template>
      </template>
    </a-table>
  </div>
</template>

<style scoped>
/* 使用 Windicss 类，无需额外的 CSS */
</style>
