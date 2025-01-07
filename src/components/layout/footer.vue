<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();
function handleGoByNmae(name) {
    router.push({name})
}
</script>


<template>

    <div class="h-[100px] bg-[#e0eeff] flex items-center justify-center">
        <!-- <h1 class="text-[#393939]">
            &copy;2025
        </h1> -->
        <!-- <a-button type="primary" @click="handleGoByNmae('DataUpdate')">
            同步数据(学校使用)
        </a-button>
        <a-button type="primary" class="ml-2" @click="handleGoByNmae('Contents')">
            管理资源(教育局使用)
        </a-button> -->
    </div>

</template>



<style scoped>
div {
    width: 100%;
    text-align: center;
}
</style>