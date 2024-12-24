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
    width: 1368px;
    min-height: calc(100% - 399px);
    padding: 20px 50px;
    margin: 0 auto;
}
</style>