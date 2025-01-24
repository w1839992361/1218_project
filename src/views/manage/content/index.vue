<script setup>
import {watch, ref, reactive, toRaw} from "vue";
import {
  getAllTree,
  addTree,
  delTree,
  uploadCover,
  updateTree,
  uploadVideo,
  getVideoUUID,
  getDocsInfo,
  getVideoInfo,
  uploadDocs,
} from "@/api/admin/content";
import {getTagInfoById} from "@/api/other";
import {message} from "ant-design-vue";
import {UploadOutlined, MoreOutlined} from "@ant-design/icons-vue";

const baseUrl = import.meta.env.VITE_API_BASE_URL;

const treeData = ref([]);

const isLoading = ref(true);

function transformNode(node, level = 1, maxLevel = 3) {
  const {id, name, children, ...rest} = node;
  return {
    key: id,
    title: {name, ...rest, id, children},
    children: (children && level < maxLevel)
        ? children.map((child) => transformNode(child, level + 1, maxLevel))
        : null,
  };
}

async function getList() {
  isLoading.value = true;
  const {data} = await getAllTree(1);
  treeData.value = data.map((item) => transformNode(item));
  isLoading.value = false;
}

getList();

// add
const addDialog = ref(false);
const addResFormRef = ref();
const addResForm = reactive({
  level: "",
  name: "",
  parentId: "",
  description: "",
  code: "",
});
const addResFormRules = {
  name: [{required: true, message: "必须输入一个节点名字", trigger: "change"}],
  description: [{required: true, message: "必须输入描述", trigger: "change"}],
  code: [{required: true, message: "必须输入资源编码", trigger: "change"}],
};
const onSubmit = () => {
  addResFormRef.value.validate().then(async () => {

    const {data} = await getTagInfoById(addResForm.parentId);
    const p_code = data?.code || '';
    addResForm.code = p_code + addResForm.code;
    const {code} = await addTree(toRaw(addResForm));
    if (code == 200) {
      message.success("添加成功!");
      addResetForm();
      getList();
    }
  });
};
const addResetForm = () => {
  addResFormRef.value.resetFields();
  addDialog.value = false;
};


const handleClick = (v) => {
  tableData.value = [];
  tableData.value = v.children;
}

const tableData = ref([]);

const columns = [
  {
    title: '课程名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '课程描述',
    dataIndex: 'description',
    key: 'description',
  },
  {
    title: '资源编码',
    dataIndex: 'code',
    key: 'code',
  },
  {
    title: '操作',
    dataIndex: 'operation',
    key: 'operation',
  },

]

// const columnDialog = ref(false);
// const columResFormRef = ref();
// const columResForm = reactive({
//   level: 0,
//   name: "",
//   description: "栏目",
// });
// const columResFormRules = {
//   name: [{required: true, message: "必须输入一个栏目标题", trigger: "change"}],
// };
</script>
<template>
  <a-row class="h-[100%]" :gutter="16">
    <a-col :span="6" class="h-[100%] ">
      <a-card class="h-[100%] overflow-x-auto">
        <a-tree showLine blockNode :tree-data="treeData">
          <template #title="{ key: treeKey, title }">
            <a-dropdown :trigger="['contextmenu']">
              <div class="w-[100%] flex justify-between">
                <span @click="handleClick(title)">
                  {{ title.name }}
                </span>
                <!-- {{ addResForm }} -->
                <div>
                  <a-dropdown trigger="click">
                    <a class="ant-dropdown-link" @click.prevent>
                      <MoreOutlined/>
                    </a>
                    <template #overlay>
                      <a-menu>
                        <a-menu-item key="0">
                          <a-button size="small" type="link" @click="
                addDialog = true;
                addResForm.level = title.level + 1;
                addResForm.parentId = treeKey;
              ">创建
                          </a-button>
                        </a-menu-item>
                        <a-menu-item key="1">
                          <a-button size="small" type="link">编辑</a-button>
                        </a-menu-item>
                        <a-menu-item key="2">
                          <a-button size="small" type="link">删除</a-button>
                        </a-menu-item>
                        <a-menu-divider/>
                      </a-menu>
                    </template>
                  </a-dropdown>
                </div>
              </div>
            </a-dropdown>
          </template>
        </a-tree>
      </a-card>

    </a-col>

    <a-col :span="18" class="h-[100%]">
      <a-card class="h-[100%]  overflow-x-auto">
        <!--        <a-form-->
        <!--            ref="columResFormRef"-->
        <!--            :model="columResForm"-->
        <!--            :rules="columResFormRules"-->
        <!--        >-->
        <!--          <a-row>-->
        <!--            <a-col :span="8">-->
        <!--              <a-form-item label="栏目标题" name="name">-->
        <!--                <a-input v-model:value="columResForm.name"/>-->
        <!--              </a-form-item>-->
        <!--            </a-col>-->
        <!--            <a-col :span="8">-->
        <!--              <a-form-item label="栏目标题" name="name">-->
        <!--                <a-input v-model:value="columResForm.name"/>-->
        <!--              </a-form-item>-->
        <!--            </a-col>-->
        <!--            <a-col :span="8">-->
        <!--              <a-form-item label="栏目标题" name="name">-->
        <!--                <a-input v-model:value="columResForm.name"/>-->
        <!--              </a-form-item>-->
        <!--            </a-col>-->
        <!--          </a-row>-->
        <!--        </a-form>-->

        <a-table :dataSource="tableData" :columns="columns">
          <template #bodyCell="{ column, text, record }">
            <template v-if="column.dataIndex === 'operation'">
                <a-button size="small">编辑</a-button>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-col>
  </a-row>


  <a-modal
      v-model:open="addDialog"
      title="添加资源节点"
      ok-text="添加"
      @cancel="addResetForm"
      cancel-text="取消"
      @ok="onSubmit"
  >
    <a-form
        ref="addResFormRef"
        :model="addResForm"
        :rules="addResFormRules"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 13 }"
    >
      <a-form-item label="节点名字" name="name">
        <a-input v-model:value="addResForm.name"/>
      </a-form-item>
      <a-form-item label="节点描述" name="description">
        <a-input v-model:value="addResForm.description"/>
      </a-form-item>
      <a-form-item label="资源编码" name="code">
        <a-input v-model:value="addResForm.code"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<style></style>
