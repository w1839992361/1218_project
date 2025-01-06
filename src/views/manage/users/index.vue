<script setup>
import { ref, reactive } from 'vue';
import { message } from 'ant-design-vue';

const columns = [
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email',
  },
  {
    title: '角色',
    dataIndex: 'role',
    key: 'role',
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
  },
];

const dataSource = ref([
  { key: 1, username: 'admin', email: 'admin@example.com', role: 'xxx' },
  { key: 2, username: 'user1', email: 'user1@example.com', role: 'xxx' },
  { key: 3, username: 'user2', email: 'user2@example.com', role: 'xxx' },
]);

const visible = ref(false);
const formState = reactive({
  key: null,
  username: '',
  email: '',
  role: '',
});

const showModal = () => {
  visible.value = true;
};

const handleOk = () => {
  if (formState.username && formState.email && formState.role) {
    if (formState.key) {
      // 编辑现有用户
      const index = dataSource.value.findIndex(item => item.key === formState.key);
      dataSource.value[index] = { ...formState };
    } else {
      // 添加新用户
      const newKey = dataSource.value.length + 1;
      dataSource.value.push({ ...formState, key: newKey });
    }
    message.success('保存成功');
    handleCancel();
  } else {
    message.error('请填写所有字段');
  }
};

const handleCancel = () => {
  visible.value = false;
  formState.key = null;
  formState.username = '';
  formState.email = '';
  formState.role = '';
};

const editItem = (record) => {
  formState.key = record.key;
  formState.username = record.username;
  formState.email = record.email;
  formState.role = record.role;
  showModal();
};

const deleteItem = (key) => {
  dataSource.value = dataSource.value.filter(item => item.key !== key);
  message.success('删除成功');
};
</script>

<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">用户管理</h1>
    <a-button type="primary" class="mb-4" @click="showModal">添加用户</a-button>
    <a-table :columns="columns" :dataSource="dataSource">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-button type="link" @click="editItem(record)">编辑</a-button>
          <a-button type="link" danger @click="deleteItem(record.key)">删除</a-button>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:visible="visible"
      :title="formState.key ? '编辑用户' : '添加用户'"
      @ok="handleOk"
    >
      <a-form :model="formState" layout="vertical">
        <a-form-item
          name="username"
          label="用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
        >
          <a-input v-model:value="formState.username" />
        </a-form-item>
        <a-form-item
          name="email"
          label="邮箱"
          :rules="[{ required: true, message: '请输入邮箱' }, { type: 'email', message: '请输入有效的邮箱地址' }]"
        >
          <a-input v-model:value="formState.email" />
        </a-form-item>
        <a-form-item
          name="role"
          label="角色"
          :rules="[{ required: true, message: '请选择角色' }]"
        >
          <a-select v-model:value="formState.role">
            <a-select-option value="管理员">管理员</a-select-option>
            <a-select-option value="普通用户">普通用户</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
/* 可以添加额外的自定义样式 */
</style>