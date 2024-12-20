<script setup>
import { RouterView } from 'vue-router';
import Header from './header.vue';
import Footer from './footer.vue';


</script>


<template>
    <Header />

    <div class="main">
        <RouterView />
    </div>

    <Footer />
</template>



<style scoped>
.main {
    width: 100%;
    min-height: calc(100% - 394px);
    padding: 20px 50px;
}
</style>