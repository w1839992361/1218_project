<script setup>
import {ref, onMounted, watch} from "vue";
import {getTagsById} from "@/api/other.js";
import {useRouter, useRoute} from "vue-router";

const classifyData = ref([]); // 存储各级数据
const selected = ref([]); // 记录每级默认选中的选项
const isLoading = ref(true);
const levels = ref(['', '', '学段', '年级', '学科', '版本', '册次']);

const route = useRoute();

const props = defineProps({
  tagId: {
    default: null,
    type: Number
  },
  selected: {
    default: [],
    type: Array
  },
  isAll: {
    default: false,
    type: Boolean
  }
});

const emits = defineEmits(['change']);

watch(props, (v) => {
  isLoading.value = true;
  classifyData.value = [];
  selected.value = [];
  initializeSelections();
  console.log('reset')
});

watch(selected, (v) => {
  emits('change', v.filter(cv => cv));
});

function generateId() {
  return 'id-' + Math.random().toString(36).substr(2, 9);
}

const handleAddAll = (data) => {
  console.log(data)
  let arr = data.map(v => {
    let da = {
      2: {
        id: 100000,
        parentId: v.id,
        name: '全部',
        level: 3,
        children: [
          {
            id: 100001,
            parentId: 100000,
            name: '全部',
            level: 4,
            children: [
              {
                id: 100002,
                parentId: 100001,
                name: '全部',
                level: 5,
                children: [
                  {
                    id: v.id,
                    parentId: 100002,
                    name: '全部',
                    level: 6,
                  }
                ]
              }
            ]
          }
        ]
      },
      3: {
        id: 200000,
        parentId: v.id,
        name: '全部',
        level: 4,
        children: [
          {
            id: 200001,
            parentId: 200000,
            name: '全部',
            level: 5,
            children: [
              {
                id: v.id,
                parentId: 200001,
                name: '全部',
                level: 6,
              }
            ]
          }
        ]
      },
      4: {
        id: 300000,
        parentId: v.id,
        name: '全部',
        level: 5,
        children: [
          {
            id: v.id,
            parentId: 300000,
            name: '全部',
            level: 6,
          }
        ]
      },
      5: {
        id:  v.id,
        parentId: v.id,
        name: '全部',
        level: 6,
      }
    }

    let cd = v.children ?? [];

    cd.forEach(i => {
      let newDa3 = JSON.parse(JSON.stringify(da[3])); // 深拷贝，确保 newDa3 独立
      newDa3.parentId = i.id;

      // 递归修改 `newDa3` 的最后一个 `id`
      let lastChild = newDa3.children[0].children[0];
      if (lastChild) {
        lastChild.id = i.id ; // 这里每次赋值给 `i.id`，不会被覆盖
      }

      i.children = [newDa3, ...(i.children ?? [])];

      i.children.forEach(ii => {
          if (ii.name !== '全部') {
            let newDa4 = JSON.parse(JSON.stringify(da[4])); // 深拷贝 `da[4]`
            newDa4.parentId = ii.id;
            let lastChild2 = newDa4.children[0];
            console.log(lastChild2,'2222')
            if (lastChild2) {
              console.log(ii.id,'idididid')
              lastChild2.id = ii.id ; // 这里每次赋值给 `i.id`，不会被覆盖
            }
            ii.children = [newDa4, ...(ii.children ?? [])];

            ii.children.forEach(iii=>{
              console.log(iii,'iiiiiiiii')
              if (iii.name !== '全部') {
                  let newDa5 = JSON.parse(JSON.stringify(da[5])); // 深拷贝 `da[4]`
                  newDa5.parentId = iii.id;
                  iii.children = [newDa5, ...(iii.children ?? [])];
                  newDa5.id = iii.id ; // 这里每次赋值给 `i.id`，不会被覆盖
              }
            })
          }
      });
    });




    if (da[v.level]) {
      cd.unshift(da[v.level]); // 仅在存在时 unshift
    }

    return{
      ...v,
      children:cd
    }
  })
  return arr;
};

// 生成对应层级的“全部”节点结构

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
  let id = props.tagId ?? (route.query.courseName === '拓展课程' ? 4 : 3);
  const {data, code} = await getTagsById(id);
  if (code === 200 && data.length) {
    let arr = [];
    let expand =
        {
          id: '00001',
          name: '全部',
          level: 2,
          children: [
            {
              id: '00002',
              name: '全部',
              level: 3,
              children: [
                {
                  id: '00003',
                  name: '全部',
                  level: 4,
                  children: [
                    {
                      id: '00004',
                      name: '全部',
                      level: 5,
                      children: [
                        {
                          id: -1,
                          name: '全部',
                          level: 6,
                        },
                      ]
                    },
                  ]
                },
              ]
            },
          ]
        }
    if (props.isAll) {
      data[0].children = handleAddAll(data[0]?.children)
      data[0]?.children.unshift(expand);
    }
    arr = transformNode(data[0]);

    classifyData.value = [[...arr.children]]; // 初始化第一层
    // 否则使用默认逻辑
    selected.value = [data[0].children[0]]; // 默认选中第一层的第一个
    updateChildren(0); // 继续展开子级
    isLoading.value = false;

    let arr2 = props.selected;
    if (arr2.length > 0) {
      for (let i = 0; i < arr2.length; i++) {
        if (i === 0) {
          setTimeout(() => {
            handleClick(arr2[i], i);
          }, 1)
        }
        if (i === 1) {
          setTimeout(() => {
            handleClick(arr2[i], i);
          }, 2)
        }
      }
    }
  }
}

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
  <!--      <span class="text-[red]">-->
  <!--        {{ selected }}-->
  <!--      </span>-->
  <a-card :loading="isLoading" class="min-h-[300px]">
    <template v-for="(level, index) in classifyData" :key="index">
      <a-row class="items-center">
        <template v-for="(item, item_i) in level" :key="item.id">
          <template v-if="item_i === 0">
            <a-col :span="2">
              {{ levels[item.level] }}
            </a-col>
          </template>
          <!--          <a-col :span="3" class="mt-2">-->
          <a-button
              class="m-2"
              shape="round"
              @click="handleClick(item, index)"
              :type="selected[index]?.id === item.id ? 'primary' : 'default'"
          >
            {{ item.name }}
          </a-button>
          <!--          </a-col>-->
        </template>
      </a-row>
    </template>
  </a-card>
</template>


<style scoped>
</style>
