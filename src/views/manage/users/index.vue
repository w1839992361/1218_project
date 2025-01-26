<script setup>
import { ref, reactive, toRaw } from "vue";
import { message } from "ant-design-vue";
import { reg } from "@/api/login";
import { getList, update } from "@/api/user";
import { LoadingOutlined, PlusOutlined } from "@ant-design/icons-vue";
import { useUserStore } from "@/stores/user";
import { getInfo } from '@/api/login';
const baseUrl = import.meta.env.VITE_API_BASE_URL;
console.log(baseUrl);
const columns = [
  {
    title: "用户名",
    dataIndex: "username",
    key: "username",
  },
  {
    title: "角色",
    dataIndex: "role",
    key: "role",
  },
  {
    title: "注册时间",
    dataIndex: "createTime",
    key: "createTime",
  },
  {
    title: "手机号",
    dataIndex: "phone",
    key: "phone",
  },
  {
    title: "操作",
    key: "action",
    width: 200,
  },
];

const dataSource = ref([]);

const queryParams = ref({
  keyword: "",
  page: 1,
  size: 10,
});
const fileList = ref([]);
const previewImg = ref("");

const _getUser = async () => {
  let ojb = Object.assign(queryParams.value);
  if (ojb.keyword === "") {
    ojb.keyword = " ";
  }
  const { data, code } = await getList(queryParams.value);
  if (code === 200) {
    dataSource.value = data.records;
    queryParams.value.total = data.total;
  }
};

_getUser();

const visible = ref(false);
const coverResFormRef = ref();

const showModal = () => {
  visible.value = true;
};

const handleCancel = () => {
  visible.value = false;
  coverResFormRef.value.resetFields();
};

const formState = reactive({
  nickname: "",
  organization: "",
  avatar: "",
  email: "",
  phone: "",
  password: "",
  isDelete: "",
  isDisable: "",
  username: "",
});

const handleOk = () => {
  coverResFormRef.value.validate().then(async () => {
    const { data, code } = await update(toRaw(formState));
    if (code === 200) {
      _getUser();
      handleCancel();
      message.success("编辑成功");
    }
  });
};

const formStateRules = {
  nickname: [{ required: true, message: "必须输入昵称", trigger: "change" }],
  organization: [{ required: true, message: "必须输入组织", trigger: "change" }],
  avatar: [{ required: true, message: "请上传头像", trigger: "change" }],
  email: [
    { required: true, message: "请输入邮箱", trigger: "change" },
    { type: "email", message: "请输入正确的邮箱地址", trigger: "change" },
  ],
  phone: [
    { required: true, message: "请输入电话号码", trigger: "change" },
    { pattern: /^[0-9]{11}$/, message: "电话号码格式不正确", trigger: "change" },
  ],
  password: [{ required: true, message: "请输入密码", trigger: "change" }],
};

const editItem = (record) => {
  formState.nickname = record.nickname;
  formState.organization = record.organization;
  formState.avatar = record.avatar;
  formState.email = record.email;
  formState.phone = record.phone;
  formState.password = record.password;
  formState.isDelete = record.delete;
  formState.isDisable = record.disable;
  formState.username = record.username;
  formState.id = record.id;
  showModal();
};

function handleChange(e) {
  if (e.file.status === "done") {
    let file = e.file.response.message;
    formState.avatar = file;
    previewImg.value = baseUrl + "/api/avatar/stream/" + file;
  }
}

const deleteItem = async (user) => {
  const data = {
    id: user.id,
    username: user.username,
    nickname: user.nickname,
    organization: user.organization,
    avatar: user.avatar,
    email: user.email,
    phone: user.phone,
    password: user.password,
    isDelete: true,
    isDisable: user.disable,
  };
  const { code } = await update(data);
  if (code === 200) {
    _getUser();
    message.success("删除成功");
  }
};

const pageChange = (page, size) => {
  queryParams.value.page = page;
  queryParams.value.size = size;
  _getList();
};
const getRole = (role) => {
  switch (role) {
    case "root":
      return "超级管理员";
    case "admin":
      return "管理员";
    case "user":
      return "用户";
    default:
      return "default";
  }
};
const getRoleColor = (role) => {
  switch (role) {
    case "root":
      return "green";
    case "admin":
      return "orange";
    case "user":
      return "red";
    default:
      return "default";
  }
};

const userStore = useUserStore();
const userInfo = ref(userStore.userInfo);

const regFormRef = ref(null);
const regVisible = ref(false);
const regForm = reactive({
  username: "",
  password: "",
  role: "user",
  avatar: "",
  nikename: "",
  email: "",
  phone: "",
  organization: "",
});
const regFormRules = {
  username: [{ required: true, message: "请输入账号", trigger: "change" }],
  password: [{ required: true, message: "请输入密码", trigger: "change" }],
  avatar: [{ required: true, message: "请上传头像", trigger: "change" }],
  role: [{ required: true, message: "请选择权限", trigger: "change" }],
  nikename: [{ required: true, message: "请输入名称", trigger: "change" }],
};
const loading = ref(false);
function resetForm() {
  regVisible.value = false;
  regFormRef.value.resetFields();
}

// 注册逻辑
const register = () => {
  regFormRef.value.validate().then(async () => {
    const { code } = await reg(toRaw(regForm));
    if (code === 200) {
      resetForm();
      _getUser();
      message.success("添加成功!");
    }
  });
};

function handleChange2(e) {
  if (e.file.status === "done") {
    let file = e.file.response.message;
    regForm.avatar = file;
    previewImg.value = baseUrl + "/api/avatar/stream/" + file;
  }
}
</script>

<template>
  <div class="p-6">
    <a-form layout="inline" class="mb-2" :model="queryParams">
      <a-form-item label="用户名">
        <a-input v-model:value="queryParams.keyword" placeholder="请输入用户名" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="_getUser">查询</a-button>
      </a-form-item>
      <a-form-item>
        <a-button
          type="primary"
          @click="regVisible = true"
          v-if="userInfo.role === 'root'"
          >添加用户</a-button
        >
      </a-form-item>
    </a-form>
    <!--    <a-button type="primary" class="mb-4" @click="showModal">添加用户</a-button>-->
    <a-table
      class="min-h-[812px]"
      :pagination="false"
      :columns="columns"
      :dataSource="dataSource"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'role'">
          <a-tag :color="getRoleColor(record.role)">{{ getRole(record.role) }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-button type="link" @click="editItem(record)">编辑</a-button>
          <a-button type="link" v-if="userInfo.role === 'root'" danger @click="deleteItem(record)">删除</a-button>
        </template>
      </template>
    </a-table>
    <a-pagination
      v-model:current="queryParams.page"
      size="small"
      :total="queryParams.total"
      @change="pageChange"
    />

    <template>
      <a-modal v-model:open="regVisible" title="添加用户" @ok="register">
        <a-form ref="regFormRef" :model="regForm" :rules="regFormRules">
          <a-form-item label="账号" name="username">
            <a-input v-model:value="regForm.username" />
          </a-form-item>
          <a-form-item label="密码" name="password">
            <a-input-password v-model:value="regForm.password" />
          </a-form-item>
          <a-form-item label="权限" name="role">
            <a-select v-model:value="regForm.role" placeholder="请选择角色">
              <a-select-option value="user">普通用户</a-select-option>
              <a-select-option value="admin">普通管理员</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item label="头像" name="avatar">
            <a-upload
              v-model:file-list="fileList"
              name="avatarFile"
              list-type="picture-card"
              class="avatar-uploader"
              :show-upload-list="false"
              :action="`${baseUrl}/api/avatar/upload`"
              @change="handleChange2"
            >
              <img v-if="regForm.avatar" :src="previewImg" alt="avatar" />
              <div v-else>
                <loading-outlined v-if="loading"></loading-outlined>
                <plus-outlined v-else></plus-outlined>
                <div class="ant-upload-text">Upload</div>
              </div>
            </a-upload>
          </a-form-item>
          <a-form-item label="昵称" name="nikename">
            <a-input v-model:value="regForm.nikename" />
          </a-form-item>
          <a-form-item label="邮箱" name="email">
            <a-input v-model:value="regForm.email" />
          </a-form-item>
          <a-form-item label="手机" name="phone">
            <a-input v-model:value="regForm.phone" />
          </a-form-item>
          <a-form-item label="组织" name="organization">
            <a-input v-model:value="regForm.organization" />
          </a-form-item>
        </a-form>
      </a-modal>

      <a-modal v-model:open="visible" title="编辑用户" @ok="handleOk">
        <a-form
          ref="coverResFormRef"
          :model="formState"
          :rules="formStateRules"
          :label-col="{ span: 5 }"
          :wrapper-col="{ span: 13 }"
        >
          <!-- 昵称 -->
          <a-form-item name="nickname" label="昵称">
            <a-input v-model:value="formState.nickname" />
          </a-form-item>
          <!-- 组织 -->
          <a-form-item name="organization" label="组织">
            <a-input v-model:value="formState.organization" />
          </a-form-item>
          <!-- 邮箱 -->
          <a-form-item name="email" label="邮箱">
            <a-input v-model:value="formState.email" />
          </a-form-item>

          <!-- 电话 -->
          <a-form-item name="phone" label="电话">
            <a-input v-model:value="formState.phone" />
          </a-form-item>

          <!-- 密码 -->
          <a-form-item name="password" label="密码">
            <a-input-password v-model:value="formState.password" />
          </a-form-item>

          <!-- 是否删除 -->
          <a-form-item name="isDelete" label="是否删除">
            <a-switch v-model:checked="formState.isDelete" />
          </a-form-item>

          <!-- 是否禁用 -->
          <a-form-item name="isDisable" label="是否禁用">
            <a-switch v-model:checked="formState.isDisable" />
          </a-form-item>
          <a-form-item label="头像" name="coverFile">
            <a-upload
              v-model:file-list="fileList"
              name="avatarFile"
              list-type="picture-card"
              class="avatar-uploader"
              :show-upload-list="false"
              :action="`${baseUrl}/api/avatar/upload`"
              @change="handleChange"
            >
              <img v-if="formState.avatar" :src="previewImg" alt="avatar" />
              <div v-else>
                <plus-outlined></plus-outlined>
                <div class="ant-upload-text">Upload</div>
              </div>
            </a-upload>
          </a-form-item>
        </a-form>
      </a-modal>
    </template>
  </div>
</template>

<style scoped>
/* 可以添加额外的自定义样式 */
</style>
