<script setup>
import { ref } from "vue";
import { message } from "ant-design-vue";
import { getSqlFile, getUpdateZip, getUpdateVersion, createAUpdate } from "@/api/data";
import { getTagsByParentId, getTagsByLevel, getTagsById } from "@/api/other";
const exporting = ref(false);
const version = ref();
const selectedIds = ref([]);
const exportIds = ref([
  {
    id: 1,
    label: "课程教学",
  },
  {
    id: 2,
    label: "",
  },
  {
    id: 3,
    label: "",
  },
  {
    id: 4,
    label: "",
  },
  {
    id: 5,
    label: "",
  },
]); // 示例导出ID

const createVersion = async () => {
  try {
    const { data, code } = await createAUpdate();
    if (code === 200) {
      message.success("版本创建成功");
      await getVersion();
    } else {
      message.error("版本创建失败");
    }
  } catch (error) {
    message.error("版本创建失败");
  }
};

const handleExport = async () => {
  if (selectedIds.value.length === 0) {
    message.error("请至少选择一个导出ID");
    return;
  }

  exporting.value = true;

  try {
    // 仅导出一次SQL文件
    let response = await getSqlFile();
    let blob = new Blob([response], { type: "application/sql" });
    let url = URL.createObjectURL(blob);
    let a = document.createElement("a");
    a.href = url;
    a.download = "export.sql";
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);

    message.success("SQL文件导出成功");

    // 导出ZIP文件（根据选择的ID）
    for (const id of selectedIds.value) {
      response = await getUpdateZip(id);
      blob = new Blob([response], { type: "application/zip" });
      url = URL.createObjectURL(blob);
      a = document.createElement("a");
      a.href = url;
      a.download = `export_${id}.zip`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);

      message.success(`ZIP文件导出成功 (ID: ${id})`);
    }
  } catch (error) {
    console.error("Error exporting files:", error);
    message.error("文件导出失败！");
  } finally {
    exporting.value = false;
  }
};

async function getVersion() {
  const { data, code } = await getUpdateVersion();
  if (code == 200) {
    version.value = data;
  }
}
async function getExportId() {
  let arr = []; // 用于存储所有的id和name
  const { data } = await getTagsByParentId(1);
  const data2 = await getTagsByParentId(3);

  // 收集data中的id和name
  if (Array.isArray(data)) {
    arr.push(...data.map((item) => ({ id: item.id, name: item.name })));
  }

  // 收集data2中的id和name
  if (Array.isArray(data2.data)) {
    arr.push(...data2.data.map((item) => ({ id: item.id, name: item.name })));
  }
  exportIds.value = arr;
}
getExportId();
getVersion();
</script>

<template>
  <div class="container">
    <h1 class="title">数据导出</h1>
    <div class="flex justify-center items-center">
      <a-card
        :loading="!version"
        title="文件导出"
        bordered
        class="text-center min-w-[800px]"
      >
        <p>当前版本: {{ version }}</p>
        <span class="text-[red]">导出过程中,请不要关闭页面！</span>
        <div>
          <a-button class="mt-5" @click="createVersion">
            创建新版本(先创建新版本,在选择导出)
          </a-button>
        </div>

        <!-- 多选框列表 -->
        <div v-for="item in exportIds" :key="item.id" class="m-2">
          <label>
            <input type="checkbox" :value="item.id" v-model="selectedIds" /> 导出:
            {{ item.name }}
          </label>
        </div>

        <a-button class="mt-5" :loading="exporting" @click="handleExport">
          {{ exporting ? "导出中..." : "导出所选版本（zip和sql）" }}
        </a-button>
      </a-card>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 16px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
  text-align: center;
}

.grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

@media (min-width: 768px) {
  .grid {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
