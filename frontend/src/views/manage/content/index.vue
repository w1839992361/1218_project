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
import {getTagInfoById, getTagsByParentId} from "@/api/other";
import {message} from "ant-design-vue";
import {UploadOutlined, MoreOutlined} from "@ant-design/icons-vue";

const baseUrl = import.meta.env.VITE_API_BASE_URL;

const treeData = ref([]);
const leftLoading = ref(true);
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
  leftLoading.value = true;
  const {data} = await getAllTree(1);
  treeData.value = data.map((item) => transformNode(item));
  leftLoading.value = false;
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

    // const {data} = await getTagInfoById(addResForm.parentId);
    // const p_code = data?.code || '';
    // addResForm.code = p_code + addResForm.code;
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


// add
const addClassDialog = ref(false);
const addClassResFormRef = ref();
const addClassResForm = reactive({
  level: "",
  name: "",
  parentId: "",
  description: "",
  code: "",
  version: "",
  number: ""
});
const addClassResFormRules = {
  parentId: [
    {required: true, message: "", trigger: "change"},
    {
      validator: (rule, value) => {
        // 检查是否为数组且长度为 2
        if (!Array.isArray(value) || value.length !== 3) {
          // 如果为空，提示错误
          return Promise.reject("必须选择册次");
        }
        return Promise.resolve();
      },
      trigger: "change"
    }
  ],
  name: [{required: true, message: "必须输入课程名字", trigger: "change"}],
  description: [{required: true, message: "必须输入描述", trigger: "change"}],
  code: [{required: true, message: "必须输入资源编码", trigger: "change"}],
  version: [{required: true, message: "必须输入课程版本", trigger: "change"}],
  number: [{required: true, message: "必须输入课程册次", trigger: "change"}],
};

const addClassResetForm = () => {
  addClassResFormRef.value.resetFields();
  addClassDialog.value = false;
}

const handleAddClass = () => {
  addClassDialog.value = true;
}

const handleAddClassSubmit = () => {
  addClassResFormRef.value.validate().then(async () => {
    console.log(toRaw(addClassResForm))
  })
}


// del
async function handleDelete(id) {
  const {code} = await delTree(id);
  if (code === 200) {
    message.success("删除成功!");
    getList();
  }
}

const editClassDialog = ref(false);
const pid = ref(null);
const handleClick = (v) => {
  tableData.value = [];
  pid.value = null;
  if (v.level <= 1) return;

  if (v.level === 2) {

    let arr = v.children?.map(item => {
      return item.children && [...item.children];
    }).flat(Infinity).filter(v => v).map(v => {
      return {
        ...v,
        children: null
      }
    });
    tableData.value = arr ? arr : [];
    return;
  }


  pid.value = v.id;
  tableData.value = v.children?.map(v => {
    return {
      ...v,
      children: null
    }
  });
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
const columnDialog = ref(false);
const columResFormRef = ref();
const columResForm = reactive({
  level: 0,
  name: "",
  description: "栏目",
});
const columResFormRules = {
  name: [{required: true, message: "必须输入一个栏目标题", trigger: "change"}],
};

// add 学科
const subjectTable = ref([]);
const SubjectColumns = ref([
  {
    title: '学科名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '学科描述',
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
]);
const addSubjectDialog = ref(false);
const addSubjectResFormRef = ref();
const addSubjectResForm = reactive({
  name: "",
  parentId: "",
  description: "",
  code: "",
});
const addSubjectResFormRules = {
  name: [{required: true, message: "必须输入学科", trigger: "change"}],
  description: [{required: true, message: "必须输入学科描述", trigger: "change"}],
  code: [{required: true, message: "必须输入资源编码", trigger: "change"}],
};

const handleAddSubject = async () => {
  let p = pid.value;
  const {data} = await getTagsByParentId(p);
  console.log(data)
  subjectTable.value = data;
  addSubjectDialog.value = true;
}
const handleAddSubjectSubmit = (v) => {
  addSubjectResFormRef.value.validate().then(async () => {
    addSubjectResForm.parentId = pid.value;
    const {code} = await addTree(toRaw(addSubjectResForm));
    if (code === 200) {
      message.success("添加成功!");
      addSubjectResFormRef.value.resetFields();
      let p = pid.value;
      const {data} = await getTagsByParentId(p);
      subjectTable.value = data;
    }
  })
}
const addSubjectResetForm = (v) => {
  addSubjectResFormRef.value.resetFields();
  addSubjectDialog.value = false;
}
const handleSubjectDelete = async (id) => {
  const {code} = await delTree(id);
  if (code === 200) {
    message.success("删除成功!");
    let p = pid.value;
    const {data} = await getTagsByParentId(p);
    subjectTable.value = data;
  }
}


// add 版本
const versionTable = ref([]);
const VersionColumns = ref([
  {
    title: '版本名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '版本描述',
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
]);
const addVersionDialog = ref(false);
const addVersionResFormRef = ref();
const addVersionResForm = reactive({
  name: "",
  parentId: "",
  description: "",
  code: "",
});
const subjectOptions = ref([]);
const addVersionResFormRules = {
  name: [{required: true, message: "必须输入版本", trigger: "change"}],
  description: [{required: true, message: "必须输入版本描述", trigger: "change"}],
  code: [{required: true, message: "必须输入资源编码", trigger: "change"}],
  parentId: [{required: true, message: "必须选择学科", trigger: "change"}],
};
const handleAddVersion = async () => {
  let p = pid.value;
  const {data} = await getTagsByParentId(p);
  subjectOptions.value = data;
  addVersionDialog.value = true;
}
const handleVersionDelete = async (id) => {
  const {code} = await delTree(id);
  if (code === 200) {
    message.success("删除成功!");
    const {data} = await getTagsByParentId(addVersionResForm.parentId);
    versionTable.value = data;
  }
}
const handleVersionSelectChange = async (v) => {
  versionTable.value = [];
  if (!v) return;
  const {data} = await getTagsByParentId(v);
  versionTable.value = data;
}
const handleAddVersionSubmit = (v) => {
  addVersionResFormRef.value.validate().then(async () => {
    const {code} = await addTree(toRaw(addVersionResForm));
    if (code === 200) {
      message.success("添加成功!");
      let p = addVersionResForm.parentId;
      addVersionResFormRef.value.resetFields();
      versionTable.value = [];
      addVersionResForm.parentId = p;
      const {data} = await getTagsByParentId(addVersionResForm.parentId);
      versionTable.value = data;
    }
  })
}
const addVersionResetForm = (v) => {
  addVersionResFormRef.value.resetFields();
  addVersionDialog.value = false;
  versionTable.value = [];
}


// add 册次
const VolumeColumns = ref([
  {
    title: '册次名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '册次描述',
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
]);
const volumeTable = ref([]);
const addVolumeDialog = ref(false);
const addVolumeResFormRef = ref();
const addVolumeResForm = reactive({
  name: "",
  parentId: "",
  description: "",
  code: "",
});
const SubjectOptions = ref([]);
const handleVolumeSelectChange = async (v) => {
  const {data} = await getTagsByParentId(v[v.length - 1]);
  volumeTable.value = data;
}
const handleAddVolume = async () => {
  let p = pid.value;
  const {data} = await getTagsByParentId(p);
  SubjectOptions.value = await Promise.all(
      data.map(async (v) => {
        let c = await getTagsByParentId(v.id);
        return {
          ...v,
          children: c.data ?? []
        };
      })
  );
  console.log(SubjectOptions)
  addVolumeDialog.value = true;
}
const addVolumeResFormRules = {
  parentId: [
    {required: true, message: "", trigger: "change"},
    {
      validator: (rule, value) => {
        // 检查是否为数组且长度为 2
        if (!Array.isArray(value) || value.length !== 2) {
          // 如果为空，提示错误
          return Promise.reject("必须选择学科和版本");
        }
        return Promise.resolve();
      },
      trigger: "change"
    }
  ],
  name: [{required: true, message: "必须输入册次", trigger: "change"}],
  description: [{required: true, message: "必须输入册次描述", trigger: "change"}],
  code: [{required: true, message: "必须输入资源编码", trigger: "change"}],
};
const handleAddVolumeSubmit = (v) => {
  addVolumeResFormRef.value.validate().then(async () => {
    const lastParentId = addVolumeResForm.parentId[addVolumeResForm.parentId.length - 1];
    const payload = {
      ...toRaw(addVolumeResForm),
      parentId: lastParentId,
    };
    const {code} = await addTree(payload);
    if (code === 200) {
      message.success("添加成功!");
      let p = addVolumeResForm.parentId;
      addVolumeResFormRef.value.resetFields();
      addVolumeResForm.parentId = p;
      const {data} = await getTagsByParentId(addVolumeResForm.parentId[addVolumeResForm.parentId.length - 1]);
      volumeTable.value = data;
    }
  })
}
const handleVolumeDelete = async (id) => {
  const {code} = await delTree(id);
  if (code === 200) {
    message.success("删除成功!");
    const {data} = await getTagsByParentId(addVolumeResForm.parentId[addVolumeResForm.parentId.length - 1]);
    volumeTable.value = data;
  }
}
const addVolumeResetForm = (v) => {
  addVolumeResFormRef.value.resetFields();
  addVolumeDialog.value = false;
}
</script>
<template>
  <a-row class="h-[100%]" :gutter="16">
    <a-col :span="4" class="h-[100%] ">
      <a-card class="h-[100%] overflow-x-auto" :loading="leftLoading">
        <a-tree showLine blockNode :tree-data="treeData">
          <template #title="{ key: treeKey, title }">
            <a-dropdown :trigger="['contextmenu']" @click="handleClick(title)">
              <div class="w-[100%] flex justify-between">
                <span>
                  {{ title.name }}
                </span>
                <!-- {{ addResForm }} -->
                <div>
                  <a-dropdown v-if="title.level < 3">
                    <a class="ant-dropdown-link" @click.prevent>
                      <MoreOutlined/>
                    </a>
                    <template #overlay>
                      <a-menu>
                        <a-menu-item key="0">
                          <a-button size="small" type="link" @click.stop="
                addDialog = true;
                addResForm.level = title.level + 1;
                addResForm.parentId = treeKey;
              ">创建子级
                          </a-button>
                        </a-menu-item>
                        <a-menu-item key="1">
                          <a-button size="small" type="link">编辑</a-button>
                        </a-menu-item>
                        <a-menu-item key="2">
                          <a-button @click="handleDelete(treeKey)" size="small"
                                    :disabled="title.name ==='课程教学' || title.name === '学科课程'  || title.name === '拓展课程'"
                                    type="link">删除
                          </a-button>
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

    <a-col :span="20" class="h-[100%]">
      <a-card class="h-[100%]  overflow-x-auto">
        <a-form
            ref="columResFormRef"
            :model="columResForm"
            :rules="columResFormRules"
        >
          <a-row>
            <a-col :span="8">
              <a-form-item label="栏目标题" name="name">
                <a-input v-model:value="columResForm.name"/>
              </a-form-item>
            </a-col>
          </a-row>
          <template v-if="pid!==null">
            <a-row class="mt-2 mb-2">
              <a-col :span="3">
                <a-button type="primary" @click="handleAddSubject">
                  添加学科
                </a-button>
              </a-col>
              <a-col :span="3">
                <a-button type="primary" @click="handleAddVersion">
                  添加版本
                </a-button>
              </a-col>
              <a-col :span="3">
                <a-button type="primary" @click="handleAddVolume">
                  添加册次
                </a-button>
              </a-col>
              <a-col :span="3">
                <a-button type="primary" @click="handleAddClass()">
                  添加课程
                </a-button>
              </a-col>
            </a-row>
          </template>
        </a-form>

        <a-table :dataSource="tableData" :columns="columns">
          <template #bodyCell="{ column, text, record }">
            <template v-if="column.dataIndex === 'name'">
              <a-button class="mr-2" size="small" type="link">{{ text }}</a-button>
            </template>
            <template v-if="column.dataIndex === 'operation'">
              <a-button class="mr-2" size="small">编辑</a-button>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-col>
  </a-row>


  <!--  添加资源节点-->
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

  <!--  添加学科-->
  <a-modal
      v-model:open="addSubjectDialog"
      title="添加学科"
      ok-text="添加"
      width="100%"
      wrap-class-name="full-modal"
      @cancel="addSubjectResetForm"
      cancel-text="取消"
      @ok="handleAddSubjectSubmit"
  >
    <a-form
        ref="addSubjectResFormRef"
        :model="addSubjectResForm"
        :rules="addSubjectResFormRules"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 13 }"
    >
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item label="学科" name="name">
            <a-input v-model:value="addSubjectResForm.name"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="学科描述" name="description">
            <a-input v-model:value="addSubjectResForm.description"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="资源编码" name="code">
            <a-input v-model:value="addSubjectResForm.code"/>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>

    <a-table :dataSource="subjectTable" :columns="SubjectColumns">
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'operation'">
          <a-button class="mr-2" size="small" danger @click="handleSubjectDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </a-modal>

  <!--  添加版本-->
  <a-modal
      v-model:open="addVersionDialog"
      title="添加版本"
      ok-text="添加"
      @cancel="addVersionResetForm"
      cancel-text="取消"
      width="100%"
      wrap-class-name="full-modal"
      @ok="handleAddVersionSubmit"
  >
    <a-form
        ref="addVersionResFormRef"
        :model="addVersionResForm"
        :rules="addVersionResFormRules"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 13 }"
    >
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item label="学科" name="parentId">
            <a-select @change="handleVersionSelectChange" v-model:value="addVersionResForm.parentId">
              <a-select-option v-for="item in subjectOptions" :value="item.id">{{ item.name }}</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="版本" name="name">
            <a-input v-model:value="addVersionResForm.name"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="版本描述" name="description">
            <a-input v-model:value="addVersionResForm.description"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="资源编码" name="code">
            <a-input v-model:value="addVersionResForm.code"/>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <h6 class="text-[red] font-bold">表格数据需要先选择学科!</h6>
    <a-table :dataSource="versionTable" :columns="VersionColumns">
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'operation'">
          <a-button class="mr-2" size="small" danger @click="handleVersionDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </a-modal>

  <!--  添加册次-->
  <a-modal
      v-model:open="addVolumeDialog"
      title="添加册次"
      ok-text="添加"
      @cancel="addVolumeResetForm"
      cancel-text="取消"
      width="100%"
      wrap-class-name="full-modal"
      @ok="handleAddVolumeSubmit"
  >
    <a-form
        ref="addVolumeResFormRef"
        :model="addVolumeResForm"
        :rules="addVolumeResFormRules"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 13 }"
    >
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item label="学科和版本" name="parentId">
            <a-cascader
                v-model:value="addVolumeResForm.parentId"
                :options="SubjectOptions"
                @change="handleVolumeSelectChange"
                showSearch
                :change-on-select="false"
                :field-names="{ label: 'name', value: 'id'}"
            />
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="册次" name="name">
            <a-input v-model:value="addVolumeResForm.name"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="册次描述" name="description">
            <a-input v-model:value="addVolumeResForm.description"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="资源编码" name="code">
            <a-input v-model:value="addVolumeResForm.code"/>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <h6 class="text-[red] font-bold">表格数据需要先选择版本!</h6>
    <a-table :dataSource="volumeTable" :columns="VolumeColumns">
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'operation'">
          <a-button class="mr-2" size="small" danger @click="handleVolumeDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </a-modal>

  <!--  添加课程-->
  <a-modal
      v-model:open="addClassDialog"
      title="添加课程"
      ok-text="添加"
      @cancel="addClassResetForm"
      cancel-text="取消"
      @ok="handleAddClassSubmit"
  >
    <a-form
        ref="addClassResFormRef"
        :model="addClassResForm"
        :rules="addClassResFormRules"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 13 }"
    >
      <a-form-item label="课程名字" name="parentId">
        <a-input v-model:value="addClassResForm.parentId"/>
      </a-form-item>
      <a-form-item label="课程名字" name="name">
        <a-input v-model:value="addClassResForm.name"/>
      </a-form-item>
      <a-form-item label="课程描述" name="description">
        <a-input v-model:value="addClassResForm.description"/>
      </a-form-item>
      <a-form-item label="资源编码" name="code">
        <a-input v-model:value="addClassResForm.code"/>
      </a-form-item>
      <!--      <a-form-item label="学科" name="version">-->
      <!--        <a-input v-model:value="addClassResForm.version"/>-->
      <!--      </a-form-item>-->
      <!--      <a-form-item label="课程版本" name="version">-->
      <!--        <a-input v-model:value="addClassResForm.version"/>-->
      <!--      </a-form-item>-->
      <!--      <a-form-item label="课程册次" name="number">-->
      <!--        <a-input v-model:value="addClassResForm.number"/>-->
      <!--      </a-form-item>-->
    </a-form>

  </a-modal>

  <a-modal
      v-model:open="editClassDialog"
      title="添加资源节点"
      ok-text="添加"
      width="100%"
      wrap-class-name="full-modal"
      @cancel="addResetForm"
      cancel-text="取消"
      @ok="onSubmit"
  >
    <p>Some contents...</p>
    <p>Some contents...</p>
    <p>Some contents...</p>
  </a-modal>
</template>

<style scoped>

</style>
