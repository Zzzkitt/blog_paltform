<template>
  <div class="container">
    <div class="main-layout">
      <div class="content-area">
        <h1 class="page-title">文章归档</h1>

        <el-skeleton :loading="loading" animated :rows="6">
          <template #default>
            <div v-if="archive.length">
              <div v-for="group in archive" :key="group.date" class="archive-group">
                <h2 class="archive-month">{{ group.date }}</h2>
                <ul class="archive-list">
                  <li v-for="a in group.articles" :key="a.id" class="archive-item">
                    <span class="archive-date">{{ dayjs(a.publishedAt).format('MM-dd') }}</span>
                    <router-link :to="`/articles/${a.id}`" class="archive-title">
                      {{ a.title }}
                    </router-link>
                  </li>
                </ul>
              </div>
            </div>
            <el-empty v-else description="暂无文章" />
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
import dayjs from 'dayjs'
import Sidebar from '@/components/Sidebar.vue'
import { getArchive, getCategories, getTags, getTopArticles } from '@/api/public'

const loading = ref(true)
const archive = ref([])
const categories = ref([])
const tags = ref([])
const recentArticles = ref([])

onMounted(async () => {
  try {
    const res = await getArchive()
    archive.value = res.data
  } catch {}
  try { getCategories().then(r => categories.value = r.data) } catch {}
  try { getTags().then(r => tags.value = r.data) } catch {}
  try { getTopArticles().then(r => recentArticles.value = r.data) } catch {}
  loading.value = false
})
</script>

<style scoped>
.page-title {
  font-size: 24px;
  margin-bottom: 24px;
}

.archive-group {
  margin-bottom: 32px;
}

.archive-month {
  font-size: 20px;
  color: #409eff;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
}

.archive-list {
  list-style: none;
  padding: 0;
}

.archive-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.archive-date {
  color: #999;
  font-size: 14px;
  min-width: 48px;
}

.archive-title {
  color: #333;
  text-decoration: none;
  font-size: 15px;
}

.archive-title:hover {
  color: #409eff;
}
</style>
