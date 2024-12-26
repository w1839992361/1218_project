<template>
    <div class="tag-container">
        <div v-for="(category, categoryIndex) in categories" :key="categoryIndex" class="tag-item">
            <span type="text" shape="round">{{ category.label }}:</span>
            <a-button v-for="(item, itemIndex) in category.items" :key="itemIndex"
                :type="item.selected ? 'primary' : 'default'" shape="round"
                @click="handleSelect(categoryIndex, itemIndex)">
                {{ item.name }}
            </a-button>
        </div>
    </div>
</template>

<script>
export default {
    name: "NestedDataRender",
    props: {
        data: {
            type: Array,
            required: true,
        },
    },
    emits: ["change"],
    data() {
        return {
            categories: [],
        };
    },
    created() {
        this.initializeCategories();
    },
    methods: {
        initializeCategories() {
            let currentLevel = this.data;
            let levelIndex = 0;
            while (currentLevel && levelIndex < 5) {
                const label = this.getLabelByLevel(levelIndex);
                const items = currentLevel.map((node, index) => ({
                    id: node.id,
                    name: node.name,
                    selected: index === 0, // 默认选中第一项
                    children: node.children || [],
                }));

                this.categories.push({ label, items });

                // 继续处理下一层
                const selectedItem = items.find((item) => item.selected);
                currentLevel = selectedItem ? selectedItem.children : null;
                levelIndex++;
            }

            this.$emit("change", this.getSelectedItems());
        },
        getLabelByLevel(level) {
            const labels = ["学段", "年级", "学科", "版本", "册次"];
            return labels[level] || `层级 ${level + 1}`;
        },
        handleSelect(categoryIndex, itemIndex) {
            const selectedItem = this.categories[categoryIndex].items[itemIndex];

            // 取消当前层所有选择
            this.categories[categoryIndex].items.forEach((item) => {
                item.selected = false;
            });

            // 设置选中
            selectedItem.selected = true;

            // 移除后续层级数据
            this.categories = this.categories.slice(0, categoryIndex + 1);

            // 递归展开所有子层级
            let currentLevel = selectedItem.children;
            let currentIndex = categoryIndex + 1;

            while (currentLevel && currentIndex < 5) {
                const label = this.getLabelByLevel(currentIndex);
                const items = currentLevel.map((node, index) => ({
                    id: node.id,
                    name: node.name,
                    selected: index === 0, // 默认选中第一项
                    children: node.children || [],
                }));

                this.categories.push({ label, items });

                const nextSelectedItem = items.find((item) => item.selected);
                currentLevel = nextSelectedItem ? nextSelectedItem.children : null;
                currentIndex++;
            }

            this.$emit("change", this.getSelectedItems());
        },
        getSelectedItems() {
            return this.categories
                .map((category) => category.items.find((item) => item.selected))
                .filter(Boolean);
        },
    },
};
</script>

<style scoped>
.tag-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.tag-item {
    display: flex;
    align-items: center;
    gap: 0px;
}
</style>