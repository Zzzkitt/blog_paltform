<template>
  <div class="article-list">
    <!-- 顶部操作栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" clearable placeholder="全部" style="width:120px">
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="草稿" value="DRAFT" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="queryParams.keyword" placeholder="搜索标题" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="router.push('/admin/articles/edit')">
            + 新建文章
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table :data="list" v-loading="loading" stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'warning'">
              {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读" width="80" align="center" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ dayjs(row.createdAt).format('YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="router.push(`/admin/articles/edit/${row.id}`)">编辑</el-button>
            <el-button
              size="small"
              :type="row.status === 'PUBLISHED' ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'PUBLISHED' ? '下架' : '发布' }}
            </el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="fetchList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { getList, deleteById, updateStatus } from '@/api/article'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  status: '',
  keyword: '',
})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getList(queryParams)
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  fetchList()
}

const handleReset = () => {
  queryParams.page = 1
  queryParams.status = ''
  queryParams.keyword = ''
  fetchList()
}

const handleToggleStatus = async (row) => {
  await updateStatus(row.id)
  ElMessage.success(row.status === 'PUBLISHED' ? '已下架' : '已发布')
  fetchList()
}

const handleDelete = async (id) => {
  await deleteById(id)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}
.table-card {
  min-height: 400px;
}
.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
