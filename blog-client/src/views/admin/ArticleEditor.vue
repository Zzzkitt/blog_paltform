<template>
  <div class="article-editor">
    <el-card>
      <el-form :model="form" label-width="80px">
        <!-- 标题 -->
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入文章标题" size="large" />
        </el-form-item>

        <!-- 分类 + 标签 + 置顶 行 -->
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" clearable placeholder="选择分类" style="width:100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="标签">
              <el-select v-model="form.tagIds" multiple collapse-tags placeholder="选择标签" style="width:100%">
                <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="置顶">
              <el-switch v-model="form.isTop" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 封面图 -->
        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="封面图片 URL（可选）" />
        </el-form-item>

        <!-- 摘要 -->
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="文章摘要（可选，不填自动截取前150字）" />
        </el-form-item>

        <!-- Markdown 编辑器 -->
        <el-form-item label="内容" required>
          <mavon-editor
            v-model="form.content"
            style="min-height:400px"
            :ishljs="true"
          />
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit('PUBLISHED')">
            发布
          </el-button>
          <el-button size="large" :loading="submitting" @click="handleSubmit('DRAFT')">
            存为草稿
          </el-button>
          <el-button size="large" @click="router.push('/admin/articles')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getById, create, update } from '@/api/article'
import { getList as getCategoryList } from '@/api/category'
import { getList as getTagList } from '@/api/tag'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

const router = useRouter()
const route = useRoute()
const isEdit = !!route.params.id
document.title = isEdit ? '编辑文章' : '新建文章'

const submitting = ref(false)
const categories = ref([])
const tags = ref([])

const form = reactive({
  title: '',
  content: '',
  summary: '',
  coverImage: '',
  categoryId: null,
  tagIds: [],
  isTop: false,
})

// 加载分类和标签
onMounted(async () => {
  try {
    const [catRes, tagRes] = await Promise.all([getCategoryList(), getTagList()])
    categories.value = catRes.data
    tags.value = tagRes.data
  } catch {
    // 分类/标签接口可失败（尚无数据）
  }

  // 编辑模式加载文章
  if (isEdit) {
    try {
      const res = await getById(route.params.id)
      const data = res.data
      form.title = data.title
      form.content = data.rawContent || data.content
      form.summary = data.summary
      form.coverImage = data.coverImage
      form.categoryId = data.categoryId
      form.tagIds = data.tagIds || []
      form.isTop = data.isTop
    } catch {
      ElMessage.error('文章不存在')
      router.push('/admin/articles')
    }
  }
})

const handleSubmit = async (status) => {
  if (!form.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  if (!form.content.trim()) {
    ElMessage.warning('请输入内容')
    return
  }

  submitting.value = true
  try {
    const payload = { ...form, status }
    if (isEdit) {
      await update(route.params.id, payload)
      ElMessage.success('文章更新成功')
    } else {
      await create(payload)
      ElMessage.success('文章创建成功')
    }
    router.push('/admin/articles')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.article-editor {
  max-width: 1000px;
}
</style>
