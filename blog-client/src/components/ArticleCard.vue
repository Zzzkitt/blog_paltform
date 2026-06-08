<template>
  <el-card class="article-card" shadow="hover" @click="goDetail">
    <div class="card-body">
      <div class="card-content">
        <h2 class="card-title">{{ article.title }}</h2>
        <p class="card-summary">{{ article.summary || stripHtml(article.content).slice(0, 150) }}</p>
        <div class="card-meta">
          <span class="meta-date">{{ dayjs(article.publishedAt || article.createdAt).format('YYYY-MM-DD') }}</span>
          <span class="meta-category" v-if="article.categoryName">{{ article.categoryName }}</span>
          <span class="meta-views">{{ article.viewCount }} 阅读</span>
        </div>
        <div class="card-tags" v-if="article.tagNames?.length">
          <el-tag v-for="tag in article.tagNames" :key="tag" size="small">{{ tag }}</el-tag>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'

const props = defineProps({
  article: { type: Object, required: true },
})

const router = useRouter()

const goDetail = () => {
  router.push(`/articles/${props.article.id}`)
}

const stripHtml = (html) => {
  if (!html) return ''
  return html.replace(/<[^>]*>/g, '').replace(/\s+/g, ' ').trim()
}
</script>

<style scoped>
.article-card {
  margin-bottom: 16px;
  cursor: pointer;
}

.card-body {
  display: flex;
  gap: 16px;
}

.card-content {
  flex: 1;
}

.card-title {
  margin: 0 0 8px;
  font-size: 20px;
  color: #303133;
}

.card-summary {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 12px;
}

.card-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.card-meta span {
  display: flex;
  align-items: center;
}

.meta-category {
  color: #409eff;
}

.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
</style>
