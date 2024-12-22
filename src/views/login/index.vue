<script setup>
import { ref } from 'vue';
import { Form, Input, Button, message } from 'ant-design-vue';
import { login } from '@/api/login'
// 管理表单输入数据
const email = ref('a@qq.com');
const password = ref('123123');
const confirmPassword = ref('');

// 控制当前显示的表单类型
const isLogin = ref(true);

// 切换表单
const toggleForm = () => {
    email.value = '';
    password.value = '';
    isLogin.value = !isLogin.value;
};

// 登录逻辑
const submit = () => {
    form.value
        .validate()
        .then(() => {
            login({ email: email.value, password: password.value })
        })
};

// 注册逻辑
const register = () => {
    form.value
        .validate()
        .then(() => {
            // 这里可以实现注册请求，比如调用 API。
            console.log('注册成功', { email: email.value, password: password.value });
        })
        .catch((errorInfo) => {
            console.log('注册失败', errorInfo);
        });
};

// 创建一个 Form 实例
const form = ref(null);
</script>

<template>
    <div class="container">
        <img src="../../assets/images/login/bg2.png" class="bg2" alt="">
        <div class="auth-container">
            <div v-if="isLogin" class="auth-form">
                <h2>登录</h2>
                <Form ref="form" :model="{ email, password }" @submit.prevent="submit">
                    <a-form-item label="邮箱" name="email" :rules="[
                { required: true, message: '邮箱不能为空' },
                { type: 'email', message: '请输入有效的邮箱地址' }
            ]">
                        <a-input v-model:value="email" type="email" placeholder="请输入邮箱" />
                    </a-form-item>
                    <a-form-item label="密码" name="password" :rules="[
                { required: true, message: '密码不能为空' }
            ]">
                        <a-input-password v-model:value="password" placeholder="请输入密码" />
                    </a-form-item>
                    <a-form-item>
                        <Button type="primary" block @click="submit">登录</Button>
                    </a-form-item>
                </Form>
                <p class="switch-auth">
                    还没有账号？<span @click="toggleForm">注册</span>
                </p>
            </div>

            <div v-if="!isLogin" class="auth-form">
                <h2>注册</h2>
                <Form ref="form" :model="{ email, password, confirmPassword }" @submit.prevent="register">
                    <a-form-item label="邮箱" name="email" :rules="[
                { required: true, message: '邮箱不能为空' },
                { type: 'email', message: '请输入有效的邮箱地址' }
            ]">
                        <a-input v-model:value="email" type="email" placeholder="请输入邮箱" />
                    </a-form-item>
                    <a-form-item label="密码" name="password" :rules="[
                { required: true, message: '密码不能为空' }
            ]">
                        <a-input-password v-model:value="password" placeholder="请输入密码" />
                    </a-form-item>
                    <a-form-item>
                        <Button type="primary" block @click="register">注册</Button>
                    </a-form-item>
                </Form>
                <p class="switch-auth">
                    已有账号？<span @click="toggleForm">登录</span>
                </p>
            </div>
        </div>
    </div>
</template>

<style scoped>
.container {
    background: url('@/assets/images/login/bg.png');
    width: 100%;
    height: 100%;
    position: relative;
}

.bg2{
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
