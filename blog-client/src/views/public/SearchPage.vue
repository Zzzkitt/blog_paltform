<template>
  <div class="container">
    <div class="main-layout">
      <div class="content-area">
        <h1 class="page-title">搜索</h1>
        <el-input
          v-model="keyword"
          placeholder="输入关键词搜索文章..."
          size="large"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>

        <el-skeleton :loading="loading" animated :rows="5" style="margin-top:24px">
          <template #default>
            <p v-if="keyword && !loading" class="search-info">
              共找到 <strong>{{ total }}</strong> 篇关于"{{ keyword }}"的文章
            </p>
            <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
            <el-empty v-if="!articles.length && searched" description="未找到相关文章" />
            <Pagination
              v-if="total > 0"
              v-model="page"
              :total="total"
              @change="fetchData"
            />
          </template>
        </el-skeleton>
      </div>

      <Sidebar
        :categories="categories"
        :tags="tags"
        :recent-articles="recentArticles"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ArticleCard from '@/components/ArticleCard.vue'
import Pagination from '@/components/Pagination.vue'
import Sidebar from '@/components/Sidebar.vue'
import { searchArticles, getCategories, getTags, getTopArticles } from '@/api/public'

const route = useRoute()
const keyword = ref(route.query.q || '')
const articles = ref([])
const categories = ref([])
const tags = ref([])
const recentArticles = ref([])
const loading = ref(false)
const searched = ref(false)
const page = ref(1)
const total = ref(0)

const fetchData = async () => {
  if (!keyword.value.trim()) return
  loading.value = true
  searched.value = true
  try {
    const res = await searchArticles({ q: keyword.value.trim(), page: page.value, pageSize: 10 })
    articles.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchData()
}

onMounted(async () => {
  Promise.all([
    getCategories().then(r => categories.value = r.data).catch(() => {}),
    getTags().then(r => tags.value = r.data).catch(() => {}),
    getTopArticles().then(r => recentArticles.value = r.data).catch(() => {}),
  ])
  if (route.query.q) fetchData()
})
</script>

<style scoped>
.page-title {
  font-size: 24px;
  margin-bottom: 20px;
}

.search-info {
  margin: 16px 0;
  color: #666;
  font-size: 14px;
}
</style>
