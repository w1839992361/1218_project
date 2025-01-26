<script setup>
import { ref, onMounted, watch ,} from 'vue'

const props = defineProps({
    data: Array,
    selectedId: Number
})

const emit = defineEmits(['change'])

const selections = ref({});

const current = ref();

// 根据ID在树中查找节点
const findNodeById = (nodes, id) => {
    for (const node of nodes) {
        if (node.id === id) return node
        if (node.children) {
            const found = findNodeById(node.children, id)
            if (found) return found
        }
    }
    return null
}

// 获取节点的父级链
const getParentChain = (nodes, id, chain = []) => {
    for (const node of nodes) {
        if (node.id === id) {
            return [...chain, node.id]
        }
        if (node.children) {
            const found = getParentChain(node.children, id, [...chain, node.id])
            if (found.length) return found
        }
    }
    return []
}

// 获取指定层级的节点
const getNodesAtLevel = (level) => {
    let currentNodes = props.data

    for (let i = 2; i < level; i++) {
        const selectedId = selections.value[i]
        const selectedNode = findNodeById(props.data, selectedId)
        if (selectedNode?.children) {
            currentNodes = selectedNode.children
        } else {
            return []
        }
    }

    return currentNodes
}

// 处理选择变更
const handleSelect = (level, id) => {
    const newSelections = {};

    // 保留当前层级之前的所有选择
    Object.entries(selections.value).forEach(([key, value]) => {
        if (Number(key) < level) {
            newSelections[Number(key)] = value;
        }
    });

    // 添加当前选择
    newSelections[level] = id;

    // 添加每个子级的第一个选项
    let currentNodes = findNodeById(props.data, id)?.children || [];
    let currentLevel = level + 1;

    while (currentNodes.length > 0 && currentLevel <= 6) {
        newSelections[currentLevel] = currentNodes[0].id;
        currentNodes = currentNodes[0].children || [];
        currentLevel++;
    }

    selections.value = newSelections;

    // 发送最下层子节点的第一个节点，考虑 level 限制
    let emitNode = findNodeById(props.data, id);
    while (emitNode && emitNode.children && emitNode.children.length > 0 && emitNode.level < 6) {
        emitNode = emitNode.children[0];
    }
    current.value = emitNode;
    emit('change', emitNode);
}



// 初始化选择
const initializeSelections = () => {
if (props.selectedId) {
    const chain = getParentChain(props.data, props.selectedId);
    const newSelections = {};

    chain.forEach((id) => {
        const node = findNodeById(props.data, id);
        if (node) {
            newSelections[node.level] = node.id;
        }
    });

    // 添加剩余层级的第一个选项，并更新 lastSelectedNode
    let currentNodes = findNodeById(props.data, chain[chain.length - 1])?.children || [];
    let currentLevel = (findNodeById(props.data, chain[chain.length - 1])?.level || 0) + 1;
    let lastSelectedNode = findNodeById(props.data, chain[chain.length - 1]); // 初始化为最后一个手动选中的节点

    while (currentNodes.length > 0 && currentLevel <= 6) {
        newSelections[currentLevel] = currentNodes[0].id;
        lastSelectedNode = findNodeById(props.data, currentNodes[0].id); // 更新最后选中的节点
        currentNodes = currentNodes[0].children || [];
        currentLevel++;
    }

    selections.value = newSelections;
    // Emit the updated last selected node
    current.value = lastSelectedNode;
    emit('change', lastSelectedNode);
}else {
        // 初始化时选择每个层级的第一个选项
        const newSelections = {}
        let currentNodes = props.data
        let currentLevel = 2
        let lastSelectedNode = null

        while (currentNodes.length > 0) {
            newSelections[currentLevel] = currentNodes[0].id
            lastSelectedNode = currentNodes[0]
            currentNodes = currentNodes[0].children || []
            if(currentLevel >=6){
              break;
            }
            currentLevel++
        }

        selections.value = newSelections
        current.value = lastSelectedNode;
        // Emit the last selected node
        emit('change', lastSelectedNode)
    }
}

// 监听 props 变化重新初始化
watch(() => props.selectedId, initializeSelections)
watch(() => props.data, initializeSelections)

// 组件挂载时初始化
onMounted(initializeSelections)

const levels = ref(['学段', '年级', '学科', '版本', '册次']);
</script>

<template>
    <div class="flex flex-col flex-wrap gap-4 p-4">
        <div v-for="(level, i) in [2, 3, 4, 5, 6]" :key="level" class="flex gap-2 flex-wrap">
            <span class="flex flex-wrap items-center">
                {{ levels[i] }}:
            </span>
            <a-button class="min-w-[90px]" @click="handleSelect(level, node.id)" shape="round" v-for="node in getNodesAtLevel(level)"
                :key="node.id" :type="selections[level] === node.id ? 'primary' : 'default'">{{ node.name }}</a-button>
        </div>
    </div>
</template>