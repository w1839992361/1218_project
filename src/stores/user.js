import { ref, computed } from 'vue';
import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', () => {
    const token = ref('');
    const userInfo = ref(null);

    function setToken(newToken) {
        token.value = newToken;
    }

    function setUserInfo(newUserInfo) {
        userInfo.value = newUserInfo;
    }

    function clearUser() {
        token.value = '';
        userInfo.value = null;
    }

    const isAuthenticated = computed(() => !!token.value);

    return {
        token,
        userInfo,
        setToken,
        setUserInfo,
        clearUser,
        isAuthenticated
    };
}, {
    persist: true  // 自动持久化到 localStorage
});
