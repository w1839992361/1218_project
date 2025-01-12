<script setup>
import {ref} from 'vue';
import {getList} from '@/api/logs';

const columns = [
  {
    title: '时间',
    dataIndex: 'actionTime',
    key: 'actionTime',
    sorter: (a, b) => new Date(a.timestamp) - new Date(b.timestamp),
  },
  {
    title: '请求方式',
    dataIndex: 'actionMethod',
    key: 'actionMethod'
  },
  {
    title: '请求路径',
    dataIndex: 'actionUrl',
    key: 'actionUrl',
  },
  {
    title: '请求参数',
    dataIndex: 'actionParams',
    key: 'actionParams',
  },
];

const dataSource = ref([]);


const queryParams = ref({
  actionUser: '',
  actionUrl: '',
  page: 1,
  size: 10,
  total: null
});
const _getList = async () => {
  let ojb = Object.assign(queryParams.value);
  if (ojb.actionUser === '') {
    ojb.actionUser = ' ';
  }
  if (ojb.actionUrl === '') {
    ojb.actionUrl = ' ';
  }
  const {data, code} = await getList(queryParams.value);
  if (code === 200) {
    dataSource.value = data.records;
    queryParams.value.total = data.total;
  }
}

_getList();


const handleTableChange = (pagination, filters, sorter) => {
  console.log('Various parameters', pagination, filters, sorter);
  // 这里可以根据需要处理分页、筛选和排序
};


const pageChange = (page,size)=>{
  queryParams.value.page = page;
  queryParams.value.size = size;
  _getList();
}
const getLogLevelColor = (level) => {
  switch (level) {
    case 'GET':
      return 'green';
    case 'POST':
      return 'orange';
    case 'DELETE':
      return 'red';
    default:
      return 'default';
  }
};
</script>
<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-6">日志管理</h1>
    <a-form layout="inline"  class="mb-2" :model="queryParams">
      <a-form-item label="日志操作人">
        <a-input v-model:value="queryParams.actionUser" placeholder="请输入用户名"/>
      </a-form-item>
      <a-form-item label="日志请求地址">
        <a-input v-model:value="queryParams.actionUrl" placeholder="请输入用户名"/>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="_getList">查询</a-button>
      </a-form-item>
    </a-form>
    <a-table
        :columns="columns"
        :dataSource="dataSource"
        :pagination="false"
        @change="handleTableChange"
    >
      <template #bodyCell="{ column, text }">
        <template v-if="column.key === 'actionMethod'">
          <a-tag :color="getLogLevelColor(text)">{{ text }}</a-tag>
        </template>
      </template>
    </a-table>
    <a-pagination v-model:current="queryParams.page"  size="small"  :total="queryParams.total" @change="pageChange"/>
  </div>
</template>

