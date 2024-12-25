<script setup>
import { watch, ref, reactive, toRaw } from 'vue';
import { getAllTree, addTree, delTree } from '@/api/admin/content';
import { message } from 'ant-design-vue';

const treeData = ref([

]);

// add
const addDialog = ref(false);
const addResFormRef = ref();
const addResForm = reactive({
    level: '',
    name: '',
    parentId: '',
});
const addResFormRules = {
    name: [
        { required: true, message: '必须输入一个节点名字', trigger: 'change' },
    ]
};
const onSubmit = () => {
    addResFormRef.value
        .validate()
        .then(async () => {
            const { code } = await addTree(toRaw(addResForm));
            if (code == 200) {
                message.success('添加成功!');
                resetForm();
                getList();
            }
        })
};
const resetForm = () => {
    addResFormRef.value.resetFields();
    addDialog.value = false;
};

// del

async function handleDelete(id) {
    const { code } = await delTree(id);
    if (code == 200) {
        message.success('删除成功!');
        getList();
    }
}

async function getList() {
    const { data } = await getAllTree();
    treeData.value = data.map(item => transformNode(item));
    console.log(treeData.value)
}

getList();

function transformNode(node) {
    const { id, name, children, ...rest } = node;
    return {
        key: id,
        title: { name, ...rest },
        children: children ? children.map(child => transformNode(child)) : null,
    };
}

// const onContextMenuClick = (treeKey, menuKey) => {
//     console.log(`treeKey: ${treeKey}, menuKey: ${menuKey}`);
// };
const expandedKeys = ref([1]);


</script>
<template>
    <a-tree showLine blockNode v-model:expandedKeys="expandedKeys" :tree-data="treeData">
        <template #title="{ key: treeKey, title }">
            <a-dropdown :trigger="['contextmenu']">
                <div class="w-[100%] flex justify-between">
                    <span>
                        {{ title.name }}
                    </span>
                    <!-- {{ addResForm }} -->
                    <div>
                        <a-button class="ml-2" size="small"
                            @click="addDialog = true; addResForm.level = title.level + 1; addResForm.parentId = treeKey;">添加</a-button>
                        <!-- <a-button class="ml-2" size="small">添加下级</a-button> -->
                        <a-button class="ml-2" size="small">编辑</a-button>
                        <a-popconfirm title="确定是否删除该节点" ok-text="删除" cancel-text="取消" @confirm="handleDelete(treeKey)">
                            <a-button class="ml-2" size="small">删除</a-button>
                        </a-popconfirm>
                    </div>

                </div>
                <!-- <a-button-group style="float:right">
                        <a-button size="small" @click="slotAddSame(nodeData)" icon="plus-circle"
                            title="添加同级"></a-button>
                        <a-button size="small" @click="slotAddSub(nodeData)" icon="share-alt" title="添加下级"></a-button>
                        <a-button size="small" @click="slotModify(nodeData)" icon="form" title="修改"></a-button>
                        <a-button size="small" @click="slotDelete(nodeData)" icon="close-circle" title="删除"></a-button>
                    </a-button-group> -->
                <!-- <template #overlay>
                    <a-menu @click="({ key: menuKey }) => onContextMenuClick(treeKey, menuKey)">
                        <a-menu-item key="1">1st menu item</a-menu-item>
                        <a-menu-item key="2">2nd menu item</a-menu-item>
                        <a-menu-item key="3">3rd menu item</a-menu-item>
                    </a-menu>
                </template> -->
            </a-dropdown>
        </template>
    </a-tree>

    <a-modal v-model:open="addDialog" title="添加资源节点" ok-text="添加" @cancel="resetForm" cancel-text="取消" @ok="onSubmit">
        <a-form ref="addResFormRef" :model="addResForm" :rules="addResFormRules" :label-col="{ span: 5 }"
            :wrapper-col="{ span: 13 }">
            <a-form-item ref="name" label="节点名字" name="name">
                <a-input v-model:value="addResForm.name" />
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<style></style>