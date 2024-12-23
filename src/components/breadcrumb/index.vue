<template>
    <a-breadcrumb :separator="'>'">
        <a-breadcrumb-item style="cursor:pointer;" v-for="(route, index) in breadcrumbList" :key="index">
            <!-- {{route.path}} -->
            <template v-if="route.path">
                <span @click="$router.push({ path: route.path })">{{ route.title }}</span>
            </template>
            <template v-else>
                {{ route.title }}
            </template>
        </a-breadcrumb-item>
    </a-breadcrumb>
</template>

<script>
export default {
    props: {
        nav: {
            type: Array,
            required: false,
            default: []
        }
    },
    data() {
        return {
            breadcrumbList: []
        }
    },
    created() {
        let matchedRoutes = this.$route.matched;
        let allRoutes = this.$router.getRoutes();

        // Create a map for quick route lookup by name
        let routeMap = new Map(allRoutes.map(route => [route.name, route]));

        this.breadcrumbList = matchedRoutes.map(route => {
            let pid = route.meta.pid;
            const breadcrumb = [];
            // If the route has a parent (pid), add the parent first
            if (pid) {
                let parentRoute = routeMap.get(pid);
                if (parentRoute) {
                    breadcrumb.push({
                        title: parentRoute.meta.title,
                        path: parentRoute.path,
                    });
                }
            }

            // Add the current route itself
            breadcrumb.push({
                title: route.meta.title,
                path: route.path,
            });

            return breadcrumb;
        }).flat(Infinity); // Flatten only once

        this.breadcrumbList = this.breadcrumbList.concat(this.nav);

    }
}
</script>