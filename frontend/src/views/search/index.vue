<script setup>
import {ref, onMounted} from 'vue'
import {SearchOutlined} from '@ant-design/icons-vue'
import {searchVideo} from '@/api/search'
import {useRouter, useRoute} from 'vue-router';
import {message} from 'ant-design-vue';
import Classify from "@/components/classify/index.vue";
import {getTagsById} from "@/api/other.js";
import TreeSelectComponent from "@/components/TreeSelectComponent.vue";

const searchQuery = ref('')
const currentTab = ref('1')
const loading = ref(false)

const classLoading = ref(true);

const handleChange = (v) => {
  if (v.length > 0) {
    handleSearch(1,v[v.length - 1]?.id);
  }
}


const baseUrl = import.meta.env.VITE_API_BASE_URL;
const imgUrl = baseUrl + '/api/videos/cover/';

const typeOptions = [
  {value: 'all', label: '全部'},
  {value: 'title', label: '标题'},
  {value: 'description', label: '描述'}
]

const tabItems = [
  {key: '1', label: '搜索'},
  // { key: '1', label: '课时(1)' },
  // { key: '2', label: '基础课程(0)' },
  // { key: '3', label: '复习课程(0)' },
  // { key: '4', label: '专题课程(0)' },
  // { key: '5', label: '考研(0)' }
]


const router = useRouter();
const route = useRoute();
searchQuery.value = route.query.keyword;
const searchResults = ref([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const handleSearch = async (page = 1,id) => {
  if (searchQuery.value === '') {
    router.push({name:'search',query:{keyword:'',courseClass:id?? route.query.courseClass}})
  }else{
    router.push({name:'search',query:{keyword:searchQuery.value,courseClass:id?? route.query.courseClass}})
  }
  loading.value = true
  try {
    const params = {
      page,
      size: pagination.value.pageSize,
      tagId:id ?? route.query.courseClass,
      //   searchType: searchType.value,
      titleOrDescription: searchQuery.value === '' ? -1: searchQuery.value
    }

    const res = await searchVideo(params)
    if (res.code === 200) {
      searchResults.value = res.data.records
      pagination.value = {
        current: res.data.current,
        pageSize: res.data.size,
        total: res.data.total
      }
    } else {
      message.error('搜索失败')
    }
  } catch (error) {
    message.error('搜索出错')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page,size) => {
  pagination.value.current = page;
  pagination.value.pageSize = size;
  handleSearch(page)
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handlePreview = (v)=>{
  router.push({name:'VideoPreview',query:{...v}})
}

</script>

<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-6xl mx-auto">
      <!-- Search Section -->
      <div class="bg-white rounded-lg shadow-sm p-6 mb-6">
        <div class="flex items-center justify-center gap-2 max-w-2xl mx-auto">
          <!--          <a-select-->
          <!--            v-model:value="searchType"-->
          <!--            class="w-24"-->
          <!--            :options="typeOptions"-->
          <!--          />-->
          <a-input-search
              v-model:value="searchQuery"
              placeholder="请输入搜索内容"
              @search="handleSearch(1)"
          >
            <template #enterButton>
              <SearchOutlined/>
            </template>
          </a-input-search>
        </div>
      </div>

      <TreeSelectComponent ref="treeSelectRef" :selected="[]"  :tag-id="3"
                           @change="handleChange" :isAll="true"/>

      <!-- Tabs Section -->
      <div class="bg-white rounded-lg shadow-sm p-6">
        <a-tabs v-model:activeKey="currentTab" class="mb-6">
          <a-tab-pane v-for="item in tabItems" :key="item.key" :tab="item.label"/>
        </a-tabs>

        <!-- Results Section -->
        <div class="space-y-4">
          <div @click="handlePreview(result)" v-for="result in searchResults" :key="result.id"
               class="p-4 border border-gray-100 rounded-lg hover:bg-gray-50 transition-colors">
            <div class="flex gap-4">
              <div class="w-40 h-24 bg-gray-100 rounded overflow-hidden">
                <img
                    :src="imgUrl+result.id"
                    :alt="result.title"
                    class="w-full h-full object-cover"
                />
              </div>
              <div class="flex-1">
                <h3 class="text-lg font-medium text-blue-600 mb-2">
                  {{ result.title }}
                </h3>
                <p class="text-gray-600 mb-2 line-clamp-2">{{ result.description }}</p>
                <div class="flex items-center text-sm text-gray-500 gap-4">
                  <span>上传时间: {{ formatDate(result.uploadTime) }}</span>
                  <!-- <span v-if="result.url">视频ID: {{ result.url }}</span> -->
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="!loading && searchResults.length === 0"
             class="text-center py-12 text-gray-500">
          暂无搜索结果
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="text-center py-12">
          <a-spin/>
        </div>

        <!-- Pagination -->
        <div class="flex justify-end items-center mt-6">
          <!-- <span class="text-sm text-gray-500">
            共 {{ pagination.total }} 条
          </span> -->
          <a-pagination
              v-model:current="pagination.current"
              :total="pagination.total"
              :pageSize="pagination.pageSize"
              @change="handlePageChange"
              show-quick-jumper
              show-size-changer
              :showTotal="total => `共 ${total} 条`"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

:deep(.ant-tabs-nav::before) {
  @apply !border-gray-100;
}

:deep(.ant-tabs-tab) {
  @apply !text-gray-500;
}

:deep(.ant-tabs-tab-active) {
  @apply !text-blue-600;
}

:deep(.ant-tabs-ink-bar) {
  @apply !bg-blue-600;
}
</style>

