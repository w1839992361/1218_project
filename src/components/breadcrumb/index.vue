<template>
    <a-breadcrumb separator=">">
        <a-breadcrumb-item style="cursor:pointer;" v-for="(route, index) in breadcrumbList" :key="index">
            <template v-if="route.path">
                <span @click="$router.push(route.path)">{{ route.title }}</span>
            </template>
            <template v-else>
                {{ route.title }}
            </template>
        </a-breadcrumb-item>
    </a-breadcrumb>
</template>

<script>
export default {
    computed: {
        breadcrumbList() {
            let matchedRoutes = this.$route.matched;
            return matchedRoutes.map(route => ({
                title: route.meta.title,
                path: route.path,
            }));
        }
    }
}
</script>