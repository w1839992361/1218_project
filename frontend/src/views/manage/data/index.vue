<script setup>
import { ref } from "vue";
import { message } from "ant-design-vue";
import { getSqlFile, getUpdateZip, getUpdateVersion, createAUpdate } from "@/api/data";
import { getTagsByParentId, getTagsByLevel, getTagsById } from "@/api/other";
const exporting = ref(false);
const version = ref();
const selectedIds = ref([]);
const exportIds = ref([]); // 示例导出ID

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
  // 将版本转为数字，自增1，然后转换为字符串
  let newVersion = (parseFloat(version.value) + 1).toFixed(1);

// 调用函数并传递新的版本号
  await createAUpdate({ version: newVersion });
  exporting.value = true;
  getVersion();

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
  const data3 = await getTagsByLevel(0);

  // 收集data中的id和name
  if (Array.isArray(data)) {
    arr.push(...data.map((item) => ({ id: item.id, name: item.name })));
  }

  // 收集data2中的id和name
  if (Array.isArray(data2.data)) {
    arr.push(...data2.data.map((item) => ({ id: item.id, name: item.name })));
  }

  // 收集data3中的id和name
  if (Array.isArray(data3.data)) {
    arr.push(...data3.data.map((item) => ({ id: item.id, name: item.name })));
  }

  // 使用 Set 去重（基于 id 和 name 的字符串化比较）
  const uniqueArr = Array.from(
      new Set(arr.map((item) => JSON.stringify(item)))
  ).map((item) => JSON.parse(item));
  exportIds.value = uniqueArr;
}
getExportId();
getVersion();
</script>

<template>
  <div class="container">
    <div class="flex justify-center items-center">
      <a-card
          :loading="!version"
          title="数据上传"
          bordered
          class="text-center min-w-[800px]"
      >
        <p>当前版本: {{ version }}</p>
        <span class="text-[red]">(上传过程中,请不要关闭页面！)</span>
        <!-- <div>
          <a-button class="mt-5" @click="createVersion">
            创建新版本(先创建新版本,在选择导出)
          </a-button>
        </div> -->

        <!-- 多选框列表 -->
        <a-row :gutter="16">
          <a-col :span="6" v-for="item in exportIds" :key="item.id">
            <label>
              <input type="checkbox" :value="item.id" v-model="selectedIds" />
              {{ item.name }}
            </label>
          </a-col>
        </a-row>

        <a-button class="mt-5" :loading="exporting" @click="handleExport">
          {{ exporting ? "上传中..." : "上传所选（zip和sql）" }}
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
