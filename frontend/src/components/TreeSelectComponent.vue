<script setup>
import {ref, onMounted, watch} from "vue";
import {getTagsById} from "@/api/other.js";

const classifyData = ref([]); // 存储各级数据
const selected = ref([]); // 记录每级默认选中的选项
const isLoading = ref(true);
const levels = ref(['', '', '学段', '年级', '学科', '版本', '册次']);

const props = defineProps({
  tagId: {
    default: 3,
    type: Number
  }
});

const emits = defineEmits(['change']);

watch(props, (v) => {
  isLoading.value = true;
  classifyData.value = [];
  selected.value = [];
  initializeSelections();
});

watch(selected, (v) => {
  emits('change', v[v.length - 1]);
});

function transformNode(node, level = 1, maxLevel = 6) {
  const {children, ...rest} = node;
  return {
    ...rest,
    children: (children && level < maxLevel)
        ? children.map((child) => transformNode(child, level + 1, maxLevel))
        : null,
  };
}

// 初始化数据
const initializeSelections = async () => {
  isLoading.value = true;
  const {data, code} = await getTagsById(props.tagId);
  if (code === 200 && data.length) {
    let arr = [];
    arr = transformNode(data[0]);
    classifyData.value = [[...arr.children]]; // 初始化第一层
    selected.value = [data[0].children[0]]; // 默认选中第一层的第一个
    updateChildren(0); // 继续展开子级
    isLoading.value = false;
  }
};

// 处理点击，更新下一级的 children
const handleClick = (item, level) => {
  selected.value[level] = item; // 选中当前级
  selected.value = selected.value.slice(0, level + 1); // 清除后续选项
  classifyData.value = classifyData.value.slice(0, level + 1); // 清除后续子级数据
  updateChildren(level);
};

// clear
const handleClear = () => {
  selected.value = [[]];
}


defineExpose({
  handleClick,
  handleClear,
  selected
});
// 更新下一级
const updateChildren = (level) => {
  const currentItem = selected.value[level];
  if (currentItem?.children?.length) {
    classifyData.value[level + 1] = [...currentItem.children]; // 加载子级
    selected.value[level + 1] = currentItem.children[0]; // 默认选中第一个
    updateChildren(level + 1); // 递归继续展开
  }
};

onMounted(initializeSelections);
</script>
<template>
  <a-card :loading="isLoading" class="min-h-[300px]">
    <template v-for="(level, index) in classifyData" :key="index">
      <a-row class="items-center">
        <template v-for="(item, item_i) in level" :key="item.id">
          <template v-if="item_i === 0">
            <a-col :span="2">
              {{ levels[item.level] }}
            </a-col>
          </template>
          <a-col :span="3" class="mt-2">
            <a-button
                shape="round"
                @click="handleClick(item, index)"
                :type="selected[index]?.id === item.id ? 'primary' : 'default'"
            >
              {{ item.name }}
            </a-button>
          </a-col>
        </template>
      </a-row>
    </template>
  </a-card>
</template>


<style scoped>
</style>
