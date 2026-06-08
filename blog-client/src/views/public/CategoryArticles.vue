<template>
  <div class="container">
    <div class="main-layout">
      <div class="content-area">
        <h1 class="page-title">分类：{{ route.params.slug }}</h1>

        <el-skeleton :loading="loading" animated :rows="5">
          <template #default>
            <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
            <el-empty v-if="!articles.length && !loading" description="该分类暂无文章" />
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
import { getCategoryArticles, getCategories, getTags, getTopArticles } from '@/api/public'

const route = useRoute()
const loading = ref(true)
const articles = ref([])
const categories = ref([])
const tags = ref([])
const recentArticles = ref([])
const page = ref(1)
const total = ref(0)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCategoryArticles(route.params.slug, { page: page.value, pageSize: 10 })
    articles.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    fetchData(),
    getCategories().then(r => categories.value = r.data).catch(() => {}),
    getTags().then(r => tags.value = r.data).catch(() => {}),
    getTopArticles().then(r => recentArticles.value = r.data).catch(() => {}),
  ])
})
</script>

<style scoped>
.page-title {
  font-size: 24px;
  margin-bottom: 20px;
}
</style>
