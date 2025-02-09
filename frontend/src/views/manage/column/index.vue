<script setup>
import {ref, reactive, toRaw} from "vue";
import {message} from "ant-design-vue";
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
  deleteTagResource
} from "@/api/admin/content";
import {getTagInfoById, getTagsByParentId, getTagsById, getTagsByLevel} from "@/api/other";
import {LoadingOutlined, PlusOutlined, UploadOutlined} from "@ant-design/icons-vue";


const subjectOptions = ref([]);
const columns = [
  {
    title: "栏目名称",
    dataIndex: "name",
    key: "name",
  },
  {
    title: "操作",
    key: "action",
    width: 200,
  },
];

const dataSource = ref([]);
const dataSource1 = ref([]);

async function fetchNavs() {
  const {data, code} = await getAllTree();
  if (code === 200) {
    dataSource1.value = data;
    dataSource.value = data.filter(v => v.id !== 1).map(v => {
      return {
        name: v.name,
        id: v.id
      }
    });
    // dataSource.value = data.map((item, index) => ({
    //   key: index + 1,
    //   id: item.id,
    //   name: item.name,
    //   children:
    //       item.name === "课程教学" && Array.isArray(item.children)
    //           ? item.children.map((child) => ({
    //             key: `${index + 1}-${child.name}`,
    //             name: child.name,
    //             id: child.id,
    //           }))
    //           : null,
    // }));
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
    const {code} = await addTree(toRaw(columResForm));
    if (code === 200) {
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
const editDialog = ref(false);
const formState = reactive({});
const editResForm = ref({});
const editResFormRef = ref();
const editResFormRules = {
  name: [{required: true, message: "必须输入册次", trigger: "change"}],
  description: [{required: true, message: "必须输入册次描述", trigger: "change"}],
  code: [{required: true, message: "必须输入资源编码", trigger: "change"}]
}
const handleEditSubmit = () => {
  editResFormRef.value.validate().then(async () => {
    editResetForm();
  })
}

const editResetForm = () => {
  editDialog.value = false;
}

const subjectPid = ref();
const editItem = async (record) => {
  subjectPid.value = record.id;
  unitForm.parentId = record.id;
  const {code, data} = await getTagsByParentId(record.id);
  if (code === 200) {
    subjectOptions.value = data;
  }
  editDialog.value = true;
};

const deleteItem = async (id) => {
  let target = dataSource1.value.find((item) => id === item.id)?.children || [];
  if (target.length === 0) {
    const {code} = await delTree(id);
    if (code === 200) {
      message.success("删除成功!");
      fetchNavs();
    }
  } else {
    message.error("栏目下有资源,无法删除!");
  }
};


const versionTable = ref([]);
const VersionColumns = ref([
  {
    title: '章节名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '章节描述',
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

const addResForm = reactive({});

const handleSelectChange = async (v) => {
  const {data, code} = await getTagsByParentId(v);
  if (code === 200) {
    console.log(data)
  }
}


// 添加章节
const chapterDialog = ref(false);
const editClassUnitResFormRef = ref();
const unitForm = reactive({
  name: "",
  parentId: '',
  description: "",
  code: "",
  level: 1
})
const unitFormResFormRules = {
  name: [{required: true, message: "必须输入单元标题", trigger: "change"}],
  description: [{required: true, message: "必须输入单元描述", trigger: "change"}],
  code: [{required: true, message: "必须输入资源编码", trigger: "change"}],
};
const handleAddChapterSubmit = () => {
  editClassUnitResFormRef.value.validate().then(async () => {
    const {code} = await addTree(toRaw(unitForm));
    if (code === 200) {
      const d = await getTagsByParentId(subjectPid.value);
      if (d.code === 200) {
        subjectOptions.value = d.data;
      }
      message.success('添加成功!');
      chapterResetForm();
    }
  })
}
const chapterResetForm = () => {
  let pid = unitForm.parentId;
  editClassUnitResFormRef.value.resetFields();
  unitForm.parentId = pid;
  chapterDialog.value = false;
}

// 删除章节
const handleVersionDelete = async (v) => {
  let {code} = await delTree(v);
  if (code === 200) {
    let {code, data} = await getTagsByParentId(subjectPid.value);
    if (code === 200) {
      subjectOptions.value = data;
      message.success("删除成功!");
    }
  }
}

const chapterEditDialog = ref(false);
const unitFormEdit = reactive({
  name: "",
  parentId: '',
  description: "",
  code: "",
  level: 1,
  videoUuid: "",
  coverUuid: "",
  docsUuid: "",
  resourceUuid: "",
})
const editEditUnitResFormRef = ref();
const unitEditFormResFormRules = {
  name: [{required: true, message: "必须输入单元标题", trigger: "change"}],
  description: [{required: true, message: "必须输入单元描述", trigger: "change"}],
  code: [{required: true, message: "必须输入资源编码", trigger: "change"}],
};
const handleEditChapterSubmit = () => {
  editEditUnitResFormRef.value.validate().then(async () => {
    const {videoUuid, coverUuid, docsUuid, id} = toRaw(unitFormEdit);
    const payload = {
      videoUuid: videoUuid ?? "",
      coverUuid: coverUuid ?? "",
      docsUuid: docsUuid ?? "",
    };
    const {code} = await updateTree(payload, id);
    if (code === 200) {
      chapterEditResetForm();
      chapterEditDialog.value = false;
    }
  })
}
const handleVersionEdit = async (v) => {
  chapterEditDialog.value = true;
  Object.assign(unitFormEdit, v);
}

const chapterEditResetForm = () => {
  editEditUnitResFormRef.value.resetFields();
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
    // console.log(coverResForm.coverFile);
    formData.append("coverFile", coverResForm.coverFile);
    const {code, data} = await uploadCover({title, description}, formData);
    if (code === 200) {
      unitFormEdit.coverUuid = data;
      if (!unitFormEdit.resourceUuid) {
        const {videoUuid, coverUuid, docsUuid, id} = toRaw(unitFormEdit);
        const payload = {
          videoUuid: videoUuid ?? "",
          coverUuid: coverUuid ?? "",
          docsUuid: docsUuid ?? "",
        };
        await updateTree(payload, id);
      }
      message.success("上传成功!");
      coverDialog.value = false;
      coverResetForm();
    }
  });
}

const coverResetForm = () => {
  coverResFormRef.value.resetFields();
  coverDialog.value = false;
  previewImg.value = false;
};

function handleUploadCover(id) {
  coverDialog.value = true;
}



const chapterId = ref(null);
const courseDialog = ref(false);
const courseData = ref([]);
const CourseColumns = ref([
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
])
const handleClick  = async (v)=>{
  let {code, data} = await getTagsByParentId(v.id);
  if (code === 200){
    chapterId.value = v.id;
    courseData.value = data;
    courseDialog.value = true;
  }
}

const handleEditChapterCourseSubmit = async (v)=>{
}

const editChapterCourseResetForm = async (v)=>{
  courseDialog.value = false;
}


const courseAddDialog = ref(false);
const courseResFromRef = ref();
const courseFormResFormRules = ref({
  name: [{required: true, message: "必须输入标题", trigger: "change"}],
  description: [{required: true, message: "必须输入描述", trigger: "change"}],
  code: [{required: true, message: "必须输入资源编码", trigger: "change"}],
});
const addCourseForm = reactive({
  name: "",
  parentId: '',
  description: "",
  code: "",
  level: 1
});
const handleAddCourse = (v)=>{
  courseResFromRef.value.validate().then(async () => {
    addCourseForm.parentId = chapterId.value;
    const {code} = await addTree(toRaw(addCourseForm));
    if (code === 200) {
      const d = await getTagsByParentId(chapterId.value);
      if (d.code === 200) {
        courseData.value = d.data;
        message.success('添加成功!');
        addCourseResetForm();
      }
    }
  })
}

const addCourseResetForm = ()=>{
  courseResFromRef.value.resetFields();
  courseAddDialog.value = false;
}

const handleCourseDelete = async (id)=>{
  let {code} = await delTree(id);
  if (code === 200) {
    let {code, data} = await getTagsByParentId(chapterId.value);
    if (code === 200) {
      courseData.value = data;
      message.success("删除成功!");
    }
  }
}



const courseEditDialog = ref(false);
const editCourseForm = reactive({
  name: "",
  parentId: '',
  description: "",
  code: "",
  level: 1,
  videoUuid: "",
  coverUuid: "",
  docsUuid: "",
  resourceUuid: "",
});
const courseEditResFromRef = ref();
const courseEditFormResFormRules = ref({
  name: [{required: true, message: "必须输入标题", trigger: "change"}],
  description: [{required: true, message: "必须输入描述", trigger: "change"}],
  code: [{required: true, message: "必须输入资源编码", trigger: "change"}],
});
const handleCourseEdit = async (v)=>{
  Object.assign(editCourseForm, {...v});
  courseEditDialog.value = true;
}
const handleEditCourseSubmit = async (v)=>{
  courseEditResFromRef.value.validate().then(async () => {
    const {videoUuid, coverUuid, docsUuid, id} = toRaw(editCourseForm);
    const payload = {
      videoUuid: videoUuid ?? "",
      coverUuid: coverUuid ?? "",
      docsUuid: docsUuid ?? "",
    };
    const {code} = await updateTree(payload, id);
    if (code === 200) {
      message.success("编辑成功!");
      editCourseResetForm();
    }
  });
}
const editCourseResetForm =  ()=>{
  courseEditResFromRef.value.resetFields();
  courseEditDialog.value = false;
}
const handleUploadCover2 = ()=>{
  coverDialog2.value = true;
}
// cover2
// common ↓
const fileList2 = ref([]);
// cover
const coverDialog2 = ref(false);
const coverResFormRef2 = ref();
const previewImg2 = ref("");
const coverLoading2 = ref(false);
const coverResForm2 = reactive({
  title: "",
  description: "",
  coverFile: "",
});
const coverResFormRules2 = {
  title: [{required: true, message: "必须输入标题", trigger: "change"}],
  description: [{required: true, message: "必须输入描述", trigger: "change"}],
  coverFile: [{required: true, message: "必须上传封面", trigger: ["blur"]}],
};

function handleCoverChange2({file}) {
  fileList2.value = [];
  coverResForm2.coverFile = file;
  const reader = new FileReader();
  reader.onload = (e) => {
    previewImg2.value = e.target.result;
  };
  reader.readAsDataURL(file);
  return false;
}

function handleCoverSubmit2() {
  coverResFormRef2.value.validate().then(async () => {
    const {title, description} = toRaw(coverResForm2);
    const formData = new FormData();
    // console.log(coverResForm.coverFile);
    formData.append("coverFile", coverResForm2.coverFile);
    const d = await uploadCover({title, description}, formData);
    if (d.code === 200) {
      editCourseForm.coverUuid = d.data;
      const {videoUuid, coverUuid, docsUuid, id} = toRaw(editCourseForm);
      const payload = {
        videoUuid: videoUuid ?? "",
        coverUuid: coverUuid ?? "",
        docsUuid: docsUuid ?? "",
      };
      await updateTree(payload, id);
      message.success("添加成功!");
      coverDialog2.value = false;
      coverResetForm2();
    }
  });
}

const coverResetForm2 = () => {
  coverResFormRef2.value.resetFields();
  coverDialog2.value = false;
  previewImg2.value = false;
};

function handleUploadVideo(id) {
  videoResForm.resourceUUID = id;
  videoDialog.value = true;
}
// video
const videoDialog = ref(false);
const videoResFormRef = ref();
const previewVideo = ref("");
const videoLoading = ref(false);
const viewVideoDialog = ref(false);
const uploadedVideos = ref([]);
// const columns = [
//   {
//     title: "标题",
//     dataIndex: "title",
//     key: "title",
//   },
//   {
//     title: "上传时间",
//     dataIndex: "uploadTime",
//     key: "uploadTime",
//   },
//   // {
//   //     title: '住址',
//   //     dataIndex: 'address',
//   //     key: 'address',
//   // },
// ];
const videoResForm = reactive({
  tag: "",
  className: "",
  title: "",
  description: "",
  resourceUUID: "",
  coverFile: "",
  videoFile: "",
});
const videoResFormRules = {
  tag: [{required: true, message: "必须输入视频标签", trigger: "change"}],
  className: [{required: true, message: "必须输入视频名称", trigger: "change"}],
  title: [{required: true, message: "必须输入视频标题", trigger: "change"}],
  description: [{required: true, message: "必须输入视频描述", trigger: "change"}],
  coverFile: [{required: true, message: "必须上传视频封面", trigger: ["blur"]}],
  videoFile: [{required: true, message: "必须上传视频", trigger: ["blur"]}],
};

function handleVideoCoverChange({file}) {
  fileList.value = [];
  videoResForm.coverFile = file;
  const reader = new FileReader();
  reader.onload = (e) => {
    previewImg.value = e.target.result;
  };
  reader.readAsDataURL(file);
  return false;
}

function handleVideoChange({file}) {
  fileList.value = [];
  videoResForm.videoFile = file;
  const reader = new FileReader();
  reader.onload = (e) => {
    previewVideo.value = e.target.result;
  };
  reader.readAsDataURL(file);
  return false;
}

function handleVideoSubmit() {
  videoResFormRef.value.validate().then(async () => {
    const {tag, className, title, description, resourceUUID} = toRaw(videoResForm);
    const formData = new FormData();
    formData.append("coverFile", videoResForm.coverFile);
    formData.append("videoFile", videoResForm.videoFile);
    const {code, data} = await uploadVideo(
        {tag, className, title, description, resourceUUID},
        formData
    );
    if (code === 200) {
      videoDialog.value = false;
      message.success("添加成功!");
      videoResetForm();
      // getList();
    }
  });
}

const videoResetForm = () => {
  videoResFormRef.value.resetFields();
  videoDialog.value = false;
  previewVideo.value = false;
  previewImg.value = false;
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
          <a-input v-model:value="columResForm.name"/>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>

  <a-modal
      v-model:open="editDialog"
      title="编辑"
      ok-text="确定"
      @cancel="editResetForm"
      cancel-text="取消"
      width="100%"
      wrap-class-name="full-modal"
      @ok="handleEditSubmit"
  >
    <a-row :gutter="16" class="m-2">
      <!--      <a-col :span="6">-->
      <!--        <a-form-item label="章节" name="name">-->
      <!--          <a-select @change="handleSelectChange" v-model:value="addResForm.parentId">-->
      <!--            <a-select-option v-for="item in subjectOptions" :value="item.id">{{ item.name }}</a-select-option>-->
      <!--          </a-select>-->
      <!--        </a-form-item>-->
      <!--      </a-col>-->
      <a-col :span="2">
        <a-button type="primary" @click="chapterDialog = true">添加章节</a-button>
      </a-col>
      <!--      <a-col :span="2">-->
      <!--        <a-button type="primary" @click="chapterDialog = true">添加课程</a-button>-->
      <!--      </a-col>-->
    </a-row>
    <h1 class="text-[red] font-bold mt-2 mb-2">点击章节名称进入课程编辑</h1>
    <a-table :dataSource="subjectOptions" :columns="VersionColumns">
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'name'">
          <span @click="handleClick(record)" class="text-[#4096ff] cursor-pointer">{{ text }}</span>
        </template>
        <template v-if="column.dataIndex === 'operation'">
          <a-button class="mr-2" size="small" @click="handleVersionEdit(record)">编辑</a-button>
          <a-button class="mr-2" size="small" danger @click="handleVersionDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </a-modal>

  <a-modal v-model:open="chapterDialog" title="添加章节" @ok="handleAddChapterSubmit" ok-text="添加"
           @cancel="chapterResetForm"
           cancel-text="取消">
    <a-form :model="unitForm" :rules="unitFormResFormRules" ref="editClassUnitResFormRef">
      <a-form-item label="章节名称" name="name">
        <a-input v-model:value="unitForm.name" placeholder="请输入章节名称"/>
      </a-form-item>
      <a-form-item label="章节描述" name="description">
        <a-input v-model:value="unitForm.description" placeholder="请输入章节描述"/>
      </a-form-item>
      <a-form-item label="资源编码" name="code">
        <a-input v-model:value="unitForm.code" placeholder="请输入资源编码"/>
      </a-form-item>
    </a-form>
  </a-modal>

  <a-modal v-model:open="chapterEditDialog" title="编辑章节" @ok="handleEditChapterSubmit" ok-text="确定"
           @cancel="chapterEditResetForm"
           cancel-text="取消">
    <a-form :model="unitFormEdit" :rules="unitEditFormResFormRules" ref="editEditUnitResFormRef">
      <a-form-item label="章节名称" name="name">

        <a-input v-model:value="unitFormEdit.name" disabled placeholder="请输入章节名称"/>
      </a-form-item>
      <a-form-item label="章节描述" name="description">
        <a-input v-model:value="unitFormEdit.description" disabled placeholder="请输入章节描述"/>
      </a-form-item>
      <a-form-item label="资源编码" name="code">
        <a-input v-model:value="unitFormEdit.code" disabled placeholder="请输入资源编码"/>
      </a-form-item>
      <a-form-item
          label="资源封面"
          name="code"
          @click="handleUploadCover(unitFormEdit.coverUuid)"
      >
        <a-button type="primary">
          <template #icon>
            <UploadOutlined/>
          </template>
          {{ Boolean(unitFormEdit.coverUuid) ? "重新" : "" }}上传
        </a-button>
      </a-form-item>
    </a-form>
  </a-modal>

  <!--  封面上传-->
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


  <a-modal
      v-model:open="courseDialog"
      title="编辑章节"
      ok-text="确定"
      @cancel="editChapterCourseResetForm"
      cancel-text="取消"
      width="100%"
      wrap-class-name="full-modal"
      @ok="handleEditChapterCourseSubmit"
  >
    <a-row :gutter="16" class="m-2">
      <a-col :span="2">
        <a-button type="primary" @click="courseAddDialog = true">添加课程</a-button>
      </a-col>
    </a-row>
    <a-table :dataSource="courseData" :columns="CourseColumns">
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'operation'">
          <a-button class="mr-2" size="small" @click="handleCourseEdit(record)">编辑</a-button>
          <a-button class="mr-2" size="small" danger @click="handleCourseDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </a-modal>

  <a-modal
      v-model:open="courseAddDialog"
      title="添加课程"
      @ok="handleAddCourse"
      ok-text="添加"
  >
    <a-form :model="addCourseForm" :rules="courseFormResFormRules" ref="courseResFromRef">
      <a-form-item label="课程名称" name="name">
        <a-input v-model:value="addCourseForm.name" placeholder="请输入课程名称"/>
      </a-form-item>
      <a-form-item label="课程描述" name="description">
        <a-input v-model:value="addCourseForm.description" placeholder="请输入课程描述"/>
      </a-form-item>
      <a-form-item label="资源编码" name="code">
        <a-input v-model:value="addCourseForm.code" placeholder="请输入资源编码"/>
      </a-form-item>
    </a-form>
  </a-modal>

  <a-modal
      v-model:open="courseEditDialog"
      title="编辑课程"
      @ok="handleEditCourseSubmit"
      ok-text="确定"
  >
    <a-form :model="editCourseForm" :rules="courseEditFormResFormRules" ref="courseEditResFromRef">
      <a-form-item label="课程名称" name="name">
        <a-input v-model:value="editCourseForm.name" disabled placeholder="请输入课程名称"/>
      </a-form-item>
      <a-form-item label="课程描述" name="description">
        <a-input v-model:value="editCourseForm.description" disabled placeholder="请输入课程描述"/>
      </a-form-item>
      <a-form-item label="资源编码" name="code">
        <a-input v-model:value="editCourseForm.code" disabled placeholder="请输入资源编码"/>
      </a-form-item>
      <a-form-item
          label="资源封面"
          name="res"
          @click="handleUploadCover2(editCourseForm.coverUuid)"
      >
        <a-button type="primary">
          <template #icon>
            <UploadOutlined/>
          </template>
          {{ Boolean(editCourseForm.coverUuid) ? "重新" : "" }}上传
        </a-button>
      </a-form-item>
      <template v-if="editCourseForm.coverUuid && editCourseForm.resourceUuid">
        <a-form-item label="视频资源" name="videoUuid">
          <a-button type="primary" @click="handleUploadVideo(editCourseForm.resourceUuid)">
            <template #icon>
              <UploadOutlined/>
            </template>
            上传
          </a-button>
        </a-form-item>
      </template>
    </a-form>
  </a-modal>

  <!--  cover2-->
  <a-modal
      v-model:open="coverDialog2"
      title="上传封面"
      ok-text="确定"
      @cancel="coverResetForm2"
      cancel-text="取消"
      @ok="handleCoverSubmit2"
  >
    <a-form
        ref="coverResFormRef2"
        :model="coverResForm2"
        :rules="coverResFormRules2"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 13 }"
    >
      <a-form-item label="封面标题" name="title">
        <a-input v-model:value="coverResForm2.title"/>
      </a-form-item>
      <a-form-item label="封面描述" name="description">
        <a-input v-model:value="coverResForm2.description"/>
      </a-form-item>
      <a-form-item label="封面" name="coverFile">
        <a-upload
            :customRequest="handleCoverChange2"
            accept=".png,.jpg"
            v-model:file-list="fileList2"
            name="coverFile"
            list-type="picture-card"
            class="avatar-uploader"
            :show-upload-list="false"
        >
          <img v-if="previewImg2" :src="previewImg2" alt="avatar"/>
          <div v-else>
            <loading-outlined v-if="coverLoading2"></loading-outlined>
            <plus-outlined v-else></plus-outlined>
            <div class="ant-upload-text">上传</div>
          </div>
        </a-upload>
      </a-form-item>
    </a-form>
  </a-modal>


  <!-- video -->
  <a-modal
      v-model:open="videoDialog"
      title="上传视频"
      ok-text="确定"
      @cancel="videoResetForm"
      cancel-text="取消"
      @ok="handleVideoSubmit"
  >
    <a-form
        ref="videoResFormRef"
        :model="videoResForm"
        :rules="videoResFormRules"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 13 }"
    >
      <a-form-item label="视频标签" name="tag">
        <a-input v-model:value="videoResForm.tag"/>
      </a-form-item>
      <a-form-item label="视频名称" name="className">
        <a-input v-model:value="videoResForm.className"/>
      </a-form-item>
      <a-form-item label="视频标题" name="title">
        <a-input v-model:value="videoResForm.title"/>
      </a-form-item>
      <a-form-item label="视频描述" name="description">
        <a-input v-model:value="videoResForm.description"/>
      </a-form-item>
      <a-form-item label="视频封面" name="coverFile">
        <a-upload
            :customRequest="handleVideoCoverChange"
            v-model:file-list="fileList"
            name="coverFile"
            list-type="picture-card"
            accept=".png,.jpg"
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
      <a-form-item label="视频" name="videoFile">
        <a-upload
            :customRequest="handleVideoChange"
            v-model:file-list="fileList"
            name="coverFile"
            list-type="picture-card"
            accept=".mp4"
            class="avatar-uploader"
            :show-upload-list="false"
        >
          <video v-if="previewVideo" autoplay muted :src="previewVideo" alt="avatar"/>
          <div v-else>
            <loading-outlined v-if="videoLoading"></loading-outlined>
            <plus-outlined v-else></plus-outlined>
            <div class="ant-upload-text">上传</div>
          </div>
        </a-upload>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<style scoped>
/* 可以添加额外的自定义样式 */
</style>
