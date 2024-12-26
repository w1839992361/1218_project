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

// edit 
const editDialog = ref(false);
const editResFormRef = ref();
const editResForm = reactive({
    level: '',
    name: '',
    parentId: '',
});
const editResFormRules = {
    name: [
        { required: true, message: '必须输入一个节点名字', trigger: 'change' },
    ]
};

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

    <a-modal v-model:open="editDialog" title="编辑资源节点" ok-text="确定" @cancel="resetForm" cancel-text="取消" @ok="onSubmit">
        <a-form ref="editResFormRef" :model="editResForm" :rules="editResFormRules" :label-col="{ span: 5 }"
            :wrapper-col="{ span: 13 }">
            <a-form-item ref="name" label="节点名字" name="name">
                <a-input v-model:value="addResForm.name" />
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<style></style>