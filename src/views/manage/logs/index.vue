<script setup>
import { ref } from 'vue';

const columns = [
  {
    title: '时间',
    dataIndex: 'timestamp',
    key: 'timestamp',
    sorter: (a, b) => new Date(a.timestamp) - new Date(b.timestamp),
  },
  {
    title: '级别',
    dataIndex: 'level',
    key: 'level',
    filters: [
      { text: 'INFO', value: 'INFO' },
      { text: 'WARNING', value: 'WARNING' },
      { text: 'ERROR', value: 'ERROR' },
    ],
    onFilter: (value, record) => record.level.indexOf(value) === 0,
  },
  {
    title: '来源',
    dataIndex: 'source',
    key: 'source',
  },
  {
    title: '消息',
    dataIndex: 'message',
    key: 'message',
  },
];

const dataSource = ref([
  {
    key: '1',
    timestamp: '2023-05-10 10:30:15',
    level: 'INFO',
    source: '用户服务',
    message: '用户登录成功',
  },
  {
    key: '2',
    timestamp: '2023-05-10 11:15:22',
    level: 'WARNING',
    source: '数据库服务',
    message: '数据库连接缓慢',
  },
  {
    key: '3',
    timestamp: '2023-05-10 12:05:48',
    level: 'ERROR',
    source: '支付服务',
    message: '支付交易失败',
  },
  {
    key: '4',
    timestamp: '2023-05-10 13:20:30',
    level: 'INFO',
    source: '文件服务',
    message: '文件上传完成',
  },
]);


const handleTableChange = (pagination, filters, sorter) => {
  console.log('Various parameters', pagination, filters, sorter);
  // 这里可以根据需要处理分页、筛选和排序
};

const getLogLevelColor = (level) => {
  switch (level) {
    case 'INFO':
      return 'green';
    case 'WARNING':
      return 'orange';
    case 'ERROR':
      return 'red';
    default:
      return 'default';
  }
};
</script>
<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-6">日志管理</h1>

    <a-table 
      :columns="columns" 
      :dataSource="dataSource"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, text }">
        <template v-if="column.key === 'level'">
          <a-tag :color="getLogLevelColor(text)">{{ text }}</a-tag>
        </template>
      </template>
    </a-table>
  </div>
</template>

