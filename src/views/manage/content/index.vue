<script setup>
import { watch, ref, reactive, toRaw } from "vue";
import {
  getAllTree,
  addTree,
  delTree,
  uploadCover,
  updateTree,
  uploadVideo,
  getVideoUUID,
  getVideoInfo,
  uploadDocs,
} from "@/api/admin/content";
import { message } from "ant-design-vue";
import { UploadOutlined } from "@ant-design/icons-vue";
import { PlusOutlined, LoadingOutlined } from "@ant-design/icons-vue";
const baseUrl = import.meta.env.VITE_API_BASE_URL;

const treeData = ref([]);

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
  name: [{ required: true, message: "必须输入一个节点名字", trigger: "change" }],
  description: [{ required: true, message: "必须输入描述", trigger: "change" }],
  code: [{ required: false, message: "必须输入资源编码", trigger: "change" }],
};
const onSubmit = () => {
  addResFormRef.value.validate().then(async () => {
    const { code } = await addTree(toRaw(addResForm));
    if (code == 200) {
      message.success("添加成功!");
      resetForm();
      getList();
    }
  });
};
const addResetForm = () => {
  addResFormRef.value.resetFields();
  addDialog.value = false;
};

// del
async function handleDelete(id) {
  const { code } = await delTree(id);
  if (code == 200) {
    message.success("删除成功!");
    getList();
  }
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
  name: [{ required: true, message: "必须输入一个节点名字", trigger: "change" }],
};
const editResetForm = () => {
  editResFormRef.value.resetFields();
  editDialog.value = false;
};
function handleEdit(item) {
  Object.assign(editResForm, { ...item });
  editDialog.value = true;
}
function handleEditSubmit() {
  editResFormRef.value.validate().then(async () => {
    const { videoUuid, coverUuid, docsUuid, id } = toRaw(editResForm);
    const payload = {
      videoUuid: videoUuid ?? "",
      coverUuid: coverUuid ?? "",
      docsUuid: docsUuid ?? "",
    };
    const { code } = await updateTree(payload, id);
    if (code == 200) {
      message.success("编辑成功!");
      editResetForm();
      getList();
    }
  });
}

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
  title: [{ required: true, message: "必须输入标题", trigger: "change" }],
  description: [{ required: true, message: "必须输入描述", trigger: "change" }],
  coverFile: [{ required: true, message: "必须上传封面", trigger: ["blur"] }],
};
function handleCoverChange({ file }) {
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
    const { title, description } = toRaw(coverResForm);
    const formData = new FormData();
    console.log(coverResForm.coverFile);
    formData.append("coverFile", coverResForm.coverFile);
    const { code, data } = await uploadCover({ title, description }, formData);
    if (code == 200) {
      editResForm.coverUuid = data;
      if (!editResForm.resourceUuid) {
        const { videoUuid, coverUuid, docsUuid, id } = toRaw(editResForm);
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

// video
const videoDialog = ref(false);
const videoResFormRef = ref();
const previewVideo = ref("");
const videoLoading = ref(false);
const viewVideoDialog = ref(false);
const uploadedVideos = ref([]);
const columns = [
  {
    title: "标题",
    dataIndex: "title",
    key: "title",
  },
  {
    title: "上传时间",
    dataIndex: "uploadTime",
    key: "uploadTime",
  },
  // {
  //     title: '住址',
  //     dataIndex: 'address',
  //     key: 'address',
  // },
];
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
  tag: [{ required: true, message: "必须输入视频标签", trigger: "change" }],
  className: [{ required: true, message: "必须输入视频名称", trigger: "change" }],
  title: [{ required: true, message: "必须输入视频标题", trigger: "change" }],
  description: [{ required: true, message: "必须输入视频描述", trigger: "change" }],
  coverFile: [{ required: true, message: "必须上传视频封面", trigger: ["blur"] }],
  videoFile: [{ required: true, message: "必须上传视频", trigger: ["blur"] }],
};
function handleVideoCoverChange({ file }) {
  fileList.value = [];
  videoResForm.coverFile = file;
  const reader = new FileReader();
  reader.onload = (e) => {
    previewImg.value = e.target.result;
  };
  reader.readAsDataURL(file);
  return false;
}
function handleVideoChange({ file }) {
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
    const { tag, className, title, description, resourceUUID } = toRaw(videoResForm);
    const formData = new FormData();
    formData.append("coverFile", videoResForm.coverFile);
    formData.append("videoFile", videoResForm.videoFile);
    const { code, data } = await uploadVideo(
      { tag, className, title, description, resourceUUID },
      formData
    );
    if (code == 200) {
      videoDialog.value = false;
      message.success("添加成功!");
      videoResetForm();
      getList();
    }
  });
}
function handleUploadVideo(id) {
  videoResForm.resourceUUID = id;
  videoDialog.value = true;
}
async function handleViewVideos(id) {
  const { data, code } = await getVideoUUID(id);
  if (code === 200 && data.length) {
    viewVideoDialog.value = true;
    uploadedVideos.value = await Promise.all(
      data.map(async (item) => {
        return (await getVideoInfo(item)).data;
      })
    );
  } else {
    message.warning("还未上传视频！");
  }
}
const videoResetForm = () => {
  videoResFormRef.value.resetFields();
  videoDialog.value = false;
  previewVideo.value = false;
  previewImg.value = false;
};

const docVideoDialog = ref(false);
const docResFormRef = ref();
const uploadedDocs = ref([]);
const docResForm = reactive({
  title: "",
  resourceUUID: "",
  pptFile: "",
  designClassFile: "",
  homeworkFile: "",
  leanTaskFile: "",
});
const docResFormRules = {
  title: [{ required: true, message: "必须输入清单标题", trigger: "change" }],
  pptFile: [{ required: true, message: "必须上传 PPT 文件", trigger: "change" }],
  designClassFile: [{ required: true, message: "必须上传设计课文件", trigger: "change" }],
  homeworkFile: [{ required: true, message: "必须上传作业文件", trigger: "change" }],
  leanTaskFile: [{ required: true, message: "必须上传学习任务文件", trigger: "change" }],
};
function handleDocSubmit() {
  docResFormRef.value.validate().then(async () => {
    const { title, resourceUUID } = toRaw(docResForm);
    const formData = new FormData();
    formData.append("designClassFile", docResForm.designClassFile);
    formData.append("homeworkFile", docResForm.homeworkFile);
    formData.append("leanTaskFile", docResForm.leanTaskFile);
    formData.append("pptFile", docResForm.pptFile);
    const { code, data } = await uploadDocs({ title, resourceUUID }, formData);
    if (code == 200) {
      docVideoDialog.value = false;
    //   editResForm.docsUuid = data;
      message.success("添加成功!");
      docResetForm();
      getList();
    }
  });
}
function handleUploadDocs(id) {
  docResForm.resourceUUID = id;
  docVideoDialog.value = true;
}
const docResetForm = () => {
  docResFormRef.value.resetFields();
  docVideoDialog.value = false;
};
const pptList = ref([]);
const designClassList = ref([]);
const homeworkList = ref([]);
const leanTaskList = ref([]);

function handlePPTChange({ file }) {
  pptList.value = [file];
  docResForm.pptFile = file;
  return false;
}

function handleDesignClassChange({ file }) {
  designClassList.value = [file];
  docResForm.designClassFile = file;
  return false;
}

function handleHomeworkChange({ file }) {
  homeworkList.value = [file];
  docResForm.homeworkFile = file;
  return false;
}

function handleLeanTaskChange({ file }) {
  leanTaskList.value = [file];
  docResForm.leanTaskFile = file;
  return false;
}

async function getList() {
  const { data } = await getAllTree();
  treeData.value = data.map((item) => transformNode(item));
  console.log(treeData.value);
}

getList();

function transformNode(node) {
  const { id, name, children, ...rest } = node;
  return {
    key: id,
    title: { name, ...rest, id },
    children: children ? children.map((child) => transformNode(child)) : null,
  };
}

// const onContextMenuClick = (treeKey, menuKey) => {
//     console.log(`treeKey: ${treeKey}, menuKey: ${menuKey}`);
// };
const expandedKeys = ref([1]);
</script>
<template>
  <a-tree showLine blockNode v-model:expandedKeys="expandedKeys" :tree-data="treeData">
    <template #title="{ key: treeKey, title }">
      <a-dropdown :trigger="['contextmenu']">
        <div class="w-[100%] flex justify-between">
          <span>
            {{ title.name }}
          </span>
          <!-- {{ addResForm }} -->
          <div>
            <a-button
              class="ml-2"
              size="small"
              @click="
                addDialog = true;
                addResForm.level = title.level + 1;
                addResForm.parentId = treeKey;
              "
              >添加</a-button
            >
            <!-- <a-button class="ml-2" size="small">添加下级</a-button> -->
            <a-button class="ml-2" size="small" @click="handleEdit(title)">编辑</a-button>
            <a-popconfirm
              title="确定是否删除该节点"
              ok-text="删除"
              cancel-text="取消"
              @confirm="handleDelete(treeKey)"
            >
              <a-button class="ml-2" size="small">删除</a-button>
            </a-popconfirm>
          </div>
        </div>
      </a-dropdown>
    </template>
  </a-tree>

  <!-- add -->
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
        <a-input v-model:value="addResForm.name" />
      </a-form-item>
      <a-form-item label="节点描述" name="description">
        <a-input v-model:value="addResForm.description" />
      </a-form-item>
      <a-form-item label="资源编码" name="code">
        <a-input v-model:value="addResForm.code" />
      </a-form-item>
    </a-form>
  </a-modal>

  <!-- edit -->
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
        <a-input disabled v-model:value="editResForm.name" />
      </a-form-item>
      <a-form-item label="节点描述" name="description">
        <a-input disabled v-model:value="editResForm.description" />
      </a-form-item>
      <a-form-item label="资源编码" name="code">
        <a-input disabled v-model:value="editResForm.code" />
      </a-form-item>
      <a-form-item
        label="资源封面"
        name="code"
        @click="handleUploadCover(editResForm.coverUuid)"
      >
        <a-button type="primary">
          <template #icon>
            <UploadOutlined />
          </template>
          {{ Boolean(editResForm.coverUuid) ? "重新" : "" }}上传
        </a-button>
      </a-form-item>
      <template v-if="editResForm.coverUuid && editResForm.resourceUuid">
        <a-form-item label="视频资源" name="videoUuid">
          <a-button type="primary" @click="handleUploadVideo(editResForm.resourceUuid)">
            <template #icon>
              <UploadOutlined />
            </template>
            上传
          </a-button>
          <a-button
            class="ml-2"
            type="primary"
            @click="handleViewVideos(editResForm.resourceUuid)"
          >
            <template #icon>
              <UploadOutlined />
            </template>
            查看已上传
          </a-button>
        </a-form-item>
        <a-form-item
          label="学习清单"
          name="docsUuid"
          
        >
          <a-button type="primary" @click="handleUploadDocs(editResForm.resourceUuid)">
            <template #icon>
              <UploadOutlined />
            </template>
            上传
          </a-button>
          <a-button
            class="ml-2"
            type="primary"
            @click="handleViewVideos(editResForm.resourceUuid)"
          >
            <template #icon>
              <UploadOutlined />
            </template>
            查看已上传
          </a-button>
        </a-form-item>
      </template>
    </a-form>
  </a-modal>

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
        <a-input v-model:value="coverResForm.title" />
      </a-form-item>
      <a-form-item label="封面描述" name="description">
        <a-input v-model:value="coverResForm.description" />
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
          <img v-if="previewImg" :src="previewImg" alt="avatar" />
          <div v-else>
            <loading-outlined v-if="coverLoading"></loading-outlined>
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
        <a-input v-model:value="videoResForm.tag" />
      </a-form-item>
      <a-form-item label="视频名称" name="className">
        <a-input v-model:value="videoResForm.className" />
      </a-form-item>
      <a-form-item label="视频标题" name="title">
        <a-input v-model:value="videoResForm.title" />
      </a-form-item>
      <a-form-item label="视频描述" name="description">
        <a-input v-model:value="videoResForm.description" />
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
          <img v-if="previewImg" :src="previewImg" alt="avatar" />
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
          <video v-if="previewVideo" autoplay muted :src="previewVideo" alt="avatar" />
          <div v-else>
            <loading-outlined v-if="videoLoading"></loading-outlined>
            <plus-outlined v-else></plus-outlined>
            <div class="ant-upload-text">上传</div>
          </div>
        </a-upload>
      </a-form-item>
    </a-form>
  </a-modal>
  <!-- view video -->
  <a-modal
    v-model:open="viewVideoDialog"
    title="已上传视频"
    ok-text="确定"
    @cancel="viewVideoDialog = false"
    cancel-text="取消"
    @ok="viewVideoDialog = false"
  >
    <a-table
      :dataSource="uploadedVideos"
      :columns="columns"
      :pagination="{ pageSize: 5 }"
    />
  </a-modal>

  <!-- 教学资源 -->
  <a-modal
    v-model:open="docVideoDialog"
    title="上传学习清单"
    ok-text="确定"
    @cancel="docResetForm"
    cancel-text="取消"
    @ok="handleDocSubmit"
  >
    <a-form
      ref="docResFormRef"
      :model="docResForm"
      :rules="docResFormRules"
      :label-col="{ span: 5 }"
      :wrapper-col="{ span: 13 }"
    >
      <a-form-item label="清单标题" name="title">
        <a-input v-model:value="docResForm.title" />
      </a-form-item>
      <!-- 上传 PPT -->
      <a-form-item label="PPT" name="pptFile">
        <a-upload
          :customRequest="handlePPTChange"
          accept=".pptx,.docx,.xlsx,.xls,.mp4,.html,txt,.pdf"
          v-model:file-list="pptList"
          name="coverFile"
          list-type="picture-card"
          class="avatar-uploader"
        >
          <div>
            <plus-outlined></plus-outlined>
            <div class="ant-upload-text">上传</div>
          </div>
        </a-upload>
      </a-form-item>

      <!-- 上传设计课文件 -->
      <a-form-item label="设计课文件" name="designClassFile">
        <a-upload
          :customRequest="handleDesignClassChange"
          accept=".pptx,.docx,.xlsx,.xls,.mp4,.html,txt,.pdf"
          v-model:file-list="designClassList"
          name="designClassFile"
          list-type="picture-card"
          class="avatar-uploader"
        >
          <div>
            <plus-outlined></plus-outlined>
            <div class="ant-upload-text">上传</div>
          </div>
        </a-upload>
      </a-form-item>

      <!-- 上传作业文件 -->
      <a-form-item label="作业文件" name="homeworkFile">
        <a-upload
          :customRequest="handleHomeworkChange"
          accept=".pptx,.docx,.xlsx,.xls,.mp4,.html,txt,.pdf"
          v-model:file-list="homeworkList"
          name="homeworkFile"
          list-type="picture-card"
          class="avatar-uploader"
        >
          <div>
            <plus-outlined></plus-outlined>
            <div class="ant-upload-text">上传</div>
          </div>
        </a-upload>
      </a-form-item>

      <!-- 上传学习任务文件 -->
      <a-form-item label="学习任务文件" name="leanTaskFile">
        <a-upload
          :customRequest="handleLeanTaskChange"
          accept=".pptx,.docx,.xlsx,.xls,.mp4,.html,txt,.pdf"
          v-model:file-list="leanTaskList"
          name="leanTaskFile"
          list-type="picture-card"
          class="avatar-uploader"
        >
          <div>
            <plus-outlined></plus-outlined>
            <div class="ant-upload-text">上传</div>
          </div>
        </a-upload>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<style></style>
