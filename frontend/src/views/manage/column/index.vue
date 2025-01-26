<script setup>
import { ref, reactive, toRaw } from "vue";
import { message } from "ant-design-vue";
import { getAllTree, addTree, delTree } from "@/api/admin/content";
const columns = [
  {
    title: "栏目名称",
    dataIndex: "name",
    key: "name",
  },
  // {
  //   title: '链接',
  //   dataIndex: 'link',
  //   key: 'link',
  // },
  {
    title: "操作",
    key: "action",
    width: 200,
  },
];

const dataSource = ref([]);
const dataSource1 = ref([]);

async function fetchNavs() {
  const { data, code } = await getAllTree();
  if (code === 200) {
    dataSource1.value = data;
    dataSource.value = data.map((item, index) => ({
      key: index + 1,
      id: item.id,
      name: item.name,
      children:
        item.name === "课程教学" && Array.isArray(item.children)
          ? item.children.map((child) => ({
              key: `${index + 1}-${child.name}`,
              name: child.name,
              id: child.id,
            }))
          : null,
    }));
  }
}

fetchNavs();


const showModal = () => {
  visible.value = true;
};
const visible = ref(false);
const columResFormRef = ref();
const columResForm = reactive({
  level: 0,
  name: "",
  description: "栏目",
});
const handleOk = () => {
  columResFormRef.value.validate().then(async () => {
    const { code } = await addTree(toRaw(columResForm));
    if (code == 200) {
      message.success("添加成功!");
      handleCancel();
      fetchNavs();
    }
  });

  // if (formState.key) {
  //   // 编辑现有栏目
  //   const index = dataSource.value.findIndex((item) => item.key === formState.key);
  //   dataSource.value[index] = { ...formState };
  // } else {
  //   // 添加新栏目
  //   const newKey = dataSource.value.length + 1;
  //   dataSource.value.push({ ...formState, key: newKey });
  // }
  // message.success("保存成功");
  // handleCancel();
};

const handleCancel = () => {
  columResFormRef.value.resetFields();
  visible.value = false;
};

const editItem = (record) => {
  formState.key = record.key;
  formState.name = record.name;
  formState.link = record.link;
  showModal();
};

const deleteItem = async (id) => {
  let target = dataSource1.value.find((item) => id === item.id)?.children || [];
  if (target.length == 0) {
    const { code } = await delTree(id);
    if (code == 200) {
      message.success("删除成功!");
      fetchNavs();
    }
  } else {
    message.error("栏目下有资源,无法删除!");
  }
};
</script>

<template>
  <div class="p-6">
    <a-button type="primary" class="mb-4" @click="showModal">添加栏目</a-button>
    <a-table :columns="columns" :dataSource="dataSource" rowKey="key">
      <template #bodyCell="{ column, record }">
        <template
          v-if="
            column.key === 'action' &&
            record.name !== '课程教学' &&
            record.name !== '学科课程' &&
            record.name !== '拓展课程'
          "
        >
          <a-button type="link" @click="editItem(record)">编辑</a-button>
          <a-popconfirm
            title="确定是否删除该栏目"
            ok-text="删除"
            cancel-text="取消"
            @confirm="deleteItem(record.id)"
          >
            <a-button type="link" danger>删除</a-button>
          </a-popconfirm>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="visible"
      :title="columResForm.id ? '编辑栏目' : '添加栏目'"
      @ok="handleOk"
    >
      <a-form ref="columResFormRef" :model="columResForm" layout="vertical">
        <a-form-item
          name="name"
          label="栏目名称"
          :rules="[{ required: true, message: '请输入栏目名称' }]"
        >
          <a-input v-model:value="columResForm.name" />
        </a-form-item>
        <!-- <a-form-item
          name="link"
          label="链接"
          :rules="[{ required: true, message: '请输入链接' }]"
        >
          <a-input v-model:value="formState.link" />
        </a-form-item> -->
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
/* 可以添加额外的自定义样式 */
</style>
