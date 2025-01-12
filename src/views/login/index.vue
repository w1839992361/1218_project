<script setup>
import { ref, reactive, toRaw } from 'vue';
import { login, reg,getInfo } from '@/api/login';
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { setToken } from '@/utils/index';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { getTagsByParentId, getTagsByLevel, getTagsById } from "@/api/other";
const userStore = useUserStore();

const router = useRouter();
const baseUrl = import.meta.env.VITE_API_BASE_URL;



const loginFormRef = ref(null);
const loginForm = reactive({
    username: '',
    password: '',
});
const loginFormRules = {
    username: [
        { required: true, message: '请输入账号', trigger: 'change' },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'change' },
    ],
};

const regFormRef = ref(null);
const regForm = reactive({
    username: '',
    password: '',
    role: '',
    avatar: '',
    nikename: '',
    email: '',
    phone: '',
    organization: ''
});
const regFormRules = {
    username: [
        { required: true, message: '请输入账号', trigger: 'change' },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'change' },
    ],
    avatar: [
        { required: true, message: '请上传头像', trigger: 'change' },
    ],
    nikename: [
        { required: true, message: '请输入名称', trigger: 'change' },
    ],
};
const fileList = ref([]);
const loading = ref(false);
const previewImg = ref('');

// 控制当前显示的表单类型
const isLogin = ref(true);

// 切换表单
const toggleForm = () => {
    isLogin.value = !isLogin.value;
    resetForm()
};

// 登录逻辑
const submit = () => {
    loginFormRef.value
        .validate()
        .then(async () => {
            const { code, data } = await login(toRaw(loginForm));
            if (code === 200) {
                setToken(data);
                const user = await getInfo()
                // 示例用法
                userStore.setUserInfo(user.data);
                userStore.setToken(data);
                message.success('登录成功!');
                resetForm();
                router.push('/');
            }
        })
};

function resetForm() {
    loginFormRef.value.resetFields();
    regFormRef.value.resetFields();
}

// 注册逻辑
const register = () => {
    regFormRef.value
        .validate()
        .then(async () => {
            const { code } = await reg(toRaw(regForm));
            if (code === 200) {
                message.success('注册成功!');
                loginForm.username = regForm.username;
                loginForm.password = regForm.password;
                submit();
            }
        })

};

function handleChange(e) {
    if (e.file.status === "done") {
        let file = e.file.response.message;
        regForm.avatar = file;
        previewImg.value = baseUrl + '/api/avatar/stream/' + file;
    }
}

</script>

<template>
    <div class="warp">
        <img src="../../assets/images/login/bg2.png" class="bg2" alt="">
        <div class="auth-container">
            <div v-show="isLogin" class="auth-form">
                <h2>登录</h2>
                <a-form ref="loginFormRef" :model="loginForm" :rules="loginFormRules">
                    <a-form-item label="账号" name="username">
                        <a-input v-model:value="loginForm.username" />
                    </a-form-item>
                    <a-form-item label="密码" name="password">
                        <a-input-password v-model:value="loginForm.password" />
                    </a-form-item>
                </a-form>
                <div class="w-[100%] flex justify-end items-center">
                    <a-button type="primary" @click="submit">登录</a-button>
                    <!-- <a-button type="text" @click="toggleForm"> 去注册</a-button> -->
                </div>
            </div>

            <div v-show="!isLogin" class="auth-form">
                <h2>注册</h2>
                <a-form ref="regFormRef" :model="regForm" :rules="regFormRules">
                    <a-form-item label="账号" name="username">
                        <a-input v-model:value="regForm.username" />
                    </a-form-item>
                    <a-form-item label="密码" name="password">
                        <a-input-password v-model:value="regForm.password" />
                    </a-form-item>
                             <a-form-item label="role" name="role">
                        <a-input-password v-model:value="regForm.role" />
                    </a-form-item>
                    <a-form-item label="头像" name="avatar">
                        <a-upload v-model:file-list="fileList" name="avatarFile" list-type="picture-card"
                            class="avatar-uploader" :show-upload-list="false" :action="`${baseUrl}/api/avatar/upload`"
                            @change="handleChange">
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
                <div class="w-[100%] flex justify-end items-center">
                    <a-button type="primary" @click="register">注册</a-button>
                    <a-button type="text" @click="toggleForm">去登录</a-button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.warp {
    background: url('@/assets/images/login/bg.png');
    width: 100%;
    height: 100%;
    position: relative;
}

.bg2 {
    width: 612px;
    height: 376px;
    position: absolute;
    top: 50%;
    left: 20%;
    transform: translateY(-50%)
}

.auth-container {
    min-width: 400px;
    min-height: 400px;
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    right: 10%;
    padding: 20px;
    border-radius: 8px;
    background: #FFF;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.auth-form h2 {
    text-align: center;
    margin-bottom: 20px;
}

.switch-auth {
    text-align: center;
    margin-top: 15px;
}

.switch-auth span {
    color: #007bff;
    cursor: pointer;
}
</style>
