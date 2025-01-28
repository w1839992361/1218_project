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
import {getTagInfoById, getTagsByParentId, getTagsById} from "@/api/other";
import {message} from "ant-design-vue";
import {UploadOutlined, MoreOutlined, LoadingOutlined, PlusOutlined} from "@ant-design/icons-vue";

const baseUrl = import.meta.env.VITE_API_BASE_URL;

const treeData = ref([]);
const expandedKeys = ref([]);
const selectedKeys = ref([]);
const leftLoading = ref(true);

function transformNode(node, level = 1, maxLevel = 3) {
  const {id, name, children, ...rest} = node;
  return {
    key: id,
    title: {name, ...rest, id, children},
    name: name,
    id: id,
    children: (children && level < maxLevel)
        ? children.map((child) => transformNode(child, level + 1, maxLevel))
        : null,
  };
}

async function getList() {
  leftLoading.value = true;
  const {data} = await getAllTree(1);
  treeData.value = data.map((item) => transformNode(item));
  if (current.value) {
    handleClick(current.value);
  }
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

    const {data} = await getTagInfoById(addResForm.parentId);
    const p_code = data?.code || '';
    // addResForm.code = p_code + addResForm.code;
    const {code} = await addTree(toRaw(addResForm));
    if (code === 200) {
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
const ClassOptions = ref([]);
const addClassResForm = reactive({
  name: "",
  parentId: "",
  description: "",
  code: "",
  level: 7,
});
const handleClassSelectChange = async (v) => {
  const {data} = await getTagsById(v[v.length - 1]);
  if (data[0]?.children?.length > 0) {
    addClassResForm.parentId = '';
    return message.warn('该册次已有课程!');
  }
};
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
const handleAddClass = async () => {
  const p = pid.value;
  const {data} = await getTagsById(p);
  if (!data[0].children) return message.warn('请先添加学科&版本&册次');
  ClassOptions.value = data[0].children.map(v => transformNode(v));
  addClassDialog.value = true;
}
const handleAddClassSubmit = () => {
  addClassResFormRef.value.validate().then(async () => {
    const lastParentId = addClassResForm.parentId[addClassResForm.parentId.length - 1];
    const payload = {
      ...toRaw(addClassResForm),
      parentId: lastParentId,
    };
    const {code} = await addTree(payload);
    if (code === 200) {
      addClassDialog.value = false;
      getList();
      addClassResetForm();
    }
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


// 通用递归方法，提取指定 level 的节点子项
const extractLevelData = (nodes, targetLevel = 6) => {
  if (!nodes || !Array.isArray(nodes)) return [];

  return nodes.reduce((acc, node) => {
    if (node.level === targetLevel && node.children) {
      // 当前节点符合目标 level，添加其子节点
      acc = acc.concat(node.children);
    }
    // 递归处理子节点
    if (node.children) {
      acc = acc.concat(extractLevelData(node.children, targetLevel));
    }
    return acc;
  }, []);
};

// 提取所有年级中的 level 6 节点子项
const extractAllLevel6Children = (nodes) => {
  if (!nodes || !Array.isArray(nodes)) return [];

  return nodes.reduce((acc, node) => {
    // 提取 level 6 的子节点
    acc.push(...extractLevelData(node.children, 6));
    return acc;
  }, []);
};

const editClassDialog = ref(false);
const pid = ref(null);
const current = ref(null);
const handleClick = async (v) => {
  tableData.value = [];
  current.value = null;
  pid.value = null;
  if (v.level <= 1) return;
  const {data} = await getTagsById(v.id);
  current.value = v;
  if (v.level === 2) {

    tableData.value = extractAllLevel6Children(data);
    return;
  }


  pid.value = v.id;
  tableData.value = extractLevelData(data, 6);
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
  level: 4
});
const addSubjectResFormRules = {
  name: [{required: true, message: "必须输入学科", trigger: "change"}],
  description: [{required: true, message: "必须输入学科描述", trigger: "change"}],
  code: [{required: true, message: "必须输入资源编码", trigger: "change"}],
};

const handleAddSubject = async () => {
  let p = pid.value;
  const {data} = await getTagsByParentId(p);
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
  level: 5
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
  if (data?.length === 0) {
    return message.warn('请先添加学科！');
  }
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
  level: 6
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
  volumeTable.value = [];
}


// edit
const editDialog = ref(false);
const editResFormRef = ref();
const editResForm = reactive({
  level: null,
  name: null,
  parentId: null,
  description: null,
  code: null,
  videoUuid: "",
  coverUuid: "",
  docsUuid: "",
  resourceUuid: "",
});
const editResFormRules = {
  name: [{required: true, message: "必须输入一个节点名字", trigger: "change"}],
};
const editResetForm = () => {
  editResFormRef.value.resetFields();
  editDialog.value = false;
};
const isXueke = ref(false);

function handleEdit(item) {
  // isXueke.value = findTopLevelNode(treeData.value, item.id,'学科课程')?.title?.name === '学科课程' || item.name ==='学科课程' ;
  Object.assign(editResForm, {...item});
  editDialog.value = true;
}

function handleEditSubmit() {
  editResFormRef.value.validate().then(async () => {
    const {videoUuid, coverUuid, docsUuid, id} = toRaw(editResForm);
    const payload = {
      videoUuid: videoUuid ?? "",
      coverUuid: coverUuid ?? "",
      docsUuid: docsUuid ?? "",
    };
    const {code} = await updateTree(payload, id);
    if (code == 200) {
      message.success("编辑成功!");
      editResetForm();
      getList();
    }
  });
}


// 封面上传
// common ↓
const fileList = ref([]);
// cover
const coverDialog = ref(false);
const coverResFormRef = ref();
const previewImg = ref("");
const coverLoading = ref(false);
const coverResForm = reactive({
  title: "",
  description: "",
  coverFile: "",
});
const coverResFormRules = {
  title: [{required: true, message: "必须输入标题", trigger: "change"}],
  description: [{required: true, message: "必须输入描述", trigger: "change"}],
  coverFile: [{required: true, message: "必须上传封面", trigger: ["blur"]}],
};

function handleCoverChange({file}) {
  fileList.value = [];
  console.log(file);
  coverResForm.coverFile = file;
  const reader = new FileReader();
  reader.onload = (e) => {
    previewImg.value = e.target.result;
  };
  reader.readAsDataURL(file);
  return false;
  // if (e.file.status === "done") {
  //     let file = e.file.response.data;
  //     coverResForm.coverFile = file;
  //     previewImg.value = baseUrl + '/api/covers/stream/' + file;
  // }
}

function handleCoverSubmit() {
  coverResFormRef.value.validate().then(async () => {
    const {title, description} = toRaw(coverResForm);
    const formData = new FormData();
    console.log(coverResForm.coverFile);
    formData.append("coverFile", coverResForm.coverFile);
    const {code, data} = await uploadCover({title, description}, formData);
    if (code == 200) {
      editResForm.coverUuid = data;
      if (!editResForm.resourceUuid) {
        const {videoUuid, coverUuid, docsUuid, id} = toRaw(editResForm);
        const payload = {
          videoUuid: videoUuid ?? "",
          coverUuid: coverUuid ?? "",
          docsUuid: docsUuid ?? "",
        };
        await updateTree(payload, id);
      }
      message.success("添加成功!");
      coverDialog.value = false;
      coverResetForm();
      getList();
    }
  });
}

function handleUploadCover(id) {
  coverDialog.value = true;
}

const coverResetForm = () => {
  coverResFormRef.value.resetFields();
  coverDialog.value = false;
  previewImg.value = false;
};


// 单元设置
const editClassUnitDialog = ref(false);
const editClassUnitResFormRef = ref();
const editClassUnitResForm = reactive({
  name: "",
  parentId: "",
  description: "",
  code: "",
  level: 8
});
const handleUtil = (v) => {
  editClassUnitResForm.parentId = v.id;
  editClassUnitDialog.value = true;
}
const handleClassUnitFormSubmit = async () => {

}
const editClassUnitForm = () => {
  // editClassUnitResFormRef.value.resetFields();
  editClassUnitDialog.value = false;
};
</script>
<template>
  <a-row class="h-[100%]" :gutter="16">
    <a-col :span="4" class="h-[100%] ">
      <a-card class="h-[100%] overflow-x-auto" :loading="leftLoading">
        <a-tree showLine blockNode :tree-data="treeData" v-model:expandedKeys="expandedKeys"
                v-model:selectedKeys="selectedKeys">
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
              <a-button class="mr-2" size="small" type="link" @click="handleEdit(record)">编辑</a-button>
              <a-button class="mr-2" size="small" type="link" @click="handleUtil(record)">单元设置</a-button>
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
      <a-form-item label="册次" name="parentId">
        <a-cascader
            v-model:value="addClassResForm.parentId"
            :options="ClassOptions"
            @change="handleClassSelectChange"
            showSearch
            :change-on-select="false"
            :field-names="{ label: 'name', value: 'id'}"
        />
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
    </a-form>

  </a-modal>

  <!--  编辑课程-->
  <a-modal
      v-model:open="editDialog"
      title="编辑资源节点"
      ok-text="确定"
      @cancel="editResetForm"
      cancel-text="取消"
      @ok="handleEditSubmit"
  >
    <a-form
        ref="editResFormRef"
        :model="editResForm"
        :rules="editResFormRules"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 13 }"
    >
      <a-form-item label="节点名字" name="name">
        <a-input disabled v-model:value="editResForm.name"/>
      </a-form-item>
      <a-form-item label="节点描述" name="description">
        <a-input disabled v-model:value="editResForm.description"/>
      </a-form-item>
      <a-form-item label="资源编码" name="code">
        <a-input disabled v-model:value="editResForm.code"/>
      </a-form-item>
      <a-form-item
          label="资源封面"
          name="code"
          @click="handleUploadCover(editResForm.coverUuid)"
      >
        <a-button type="primary">
          <template #icon>
            <UploadOutlined/>
          </template>
          {{ Boolean(editResForm.coverUuid) ? "重新" : "" }}上传
        </a-button>
      </a-form-item>
      <!--      <template v-if="editResForm.coverUuid && editResForm.resourceUuid">-->
      <!--        <a-form-item label="视频资源" name="videoUuid">-->
      <!--          <a-button type="primary" @click="handleUploadVideo(editResForm.resourceUuid)">-->
      <!--            <template #icon>-->
      <!--              <UploadOutlined />-->
      <!--            </template>-->
      <!--            上传-->
      <!--          </a-button>-->
      <!--          <a-button-->
      <!--              class="ml-2"-->
      <!--              type="primary"-->
      <!--              @click="handleViewVideos(editResForm.resourceUuid)"-->
      <!--          >-->
      <!--            <template #icon>-->
      <!--              <UploadOutlined />-->
      <!--            </template>-->
      <!--            查看已上传-->
      <!--          </a-button>-->
      <!--        </a-form-item>-->
      <!--        <a-form-item v-if="isXueke" label="学习清单" name="docsUuid">-->
      <!--          <a-button type="primary" @click="handleUploadDocs(editResForm.resourceUuid)">-->
      <!--            <template #icon>-->
      <!--              <UploadOutlined />-->
      <!--            </template>-->
      <!--            上传-->
      <!--          </a-button>-->
      <!--          <a-button-->
      <!--              class="ml-2"-->
      <!--              type="primary"-->
      <!--              @click="handleViewDocs(editResForm.resourceUuid)"-->
      <!--          >-->
      <!--            <template #icon>-->
      <!--              <UploadOutlined />-->
      <!--            </template>-->
      <!--            查看已上传-->
      <!--          </a-button>-->
      <!--        </a-form-item>-->
      <!--      </template>-->
    </a-form>
  </a-modal>


  <!--  封面上传-->
  <!-- cover -->
  <a-modal
      v-model:open="coverDialog"
      title="上传封面"
      ok-text="确定"
      @cancel="coverResetForm"
      cancel-text="取消"
      @ok="handleCoverSubmit"
  >
    <a-form
        ref="coverResFormRef"
        :model="coverResForm"
        :rules="coverResFormRules"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 13 }"
    >
      <a-form-item label="封面标题" name="title">
        <a-input v-model:value="coverResForm.title"/>
      </a-form-item>
      <a-form-item label="封面描述" name="description">
        <a-input v-model:value="coverResForm.description"/>
      </a-form-item>
      <a-form-item label="封面" name="coverFile">
        <a-upload
            :customRequest="handleCoverChange"
            accept=".png,.jpg"
            v-model:file-list="fileList"
            name="coverFile"
            list-type="picture-card"
            class="avatar-uploader"
            :show-upload-list="false"
        >
          <img v-if="previewImg" :src="previewImg" alt="avatar"/>
          <div v-else>
            <loading-outlined v-if="coverLoading"></loading-outlined>
            <plus-outlined v-else></plus-outlined>
            <div class="ant-upload-text">上传</div>
          </div>
        </a-upload>
      </a-form-item>
    </a-form>
  </a-modal>

  <!--  单元设置-->
  <a-modal
      v-model:open="editClassUnitDialog"
      title="编辑课程单元"
      ok-text="确定"
      width="100%"
      wrap-class-name="full-modal"
      @cancel="editClassUnitForm"
      cancel-text="取消"
      @ok="editClassUnitForm"
  >

  </a-modal>
</template>

<style scoped>

</style>
