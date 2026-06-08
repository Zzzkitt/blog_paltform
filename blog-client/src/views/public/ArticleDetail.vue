<template>
  <div class="container">
    <el-skeleton :loading="loading" animated :rows="10">
      <template #default>
        <article class="article-detail" v-if="article">
          <h1 class="article-title">{{ article.title }}</h1>

          <div class="article-meta">
            <span v-if="article.categoryName" class="meta-category">{{ article.categoryName }}</span>
            <span class="meta-date">{{ dayjs(article.publishedAt || article.createdAt).format('YYYY-MM-DD HH:mm') }}</span>
            <span class="meta-views">{{ article.viewCount }} 次阅读</span>
          </div>

          <div class="article-tags" v-if="article.tagNames?.length">
            <el-tag v-for="tag in article.tagNames" :key="tag" size="small">{{ tag }}</el-tag>
          </div>

          <div class="article-content" v-html="article.content"></div>

          <!-- Giscus 评论区 -->
          <GiscusComments v-if="article.id" :article-id="article.id" />
        </article>

        <el-empty v-else description="文章不存在" />
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import { getArticleById } from '@/api/public'
import GiscusComments from '@/components/GiscusComments.vue'

const route = useRoute()
const loading = ref(true)
const article = ref(null)

const md = new MarkdownIt({
  html: true,
  breaks: true,
  linkify: true,
  highlight: (str, lang) => {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch {}
    }
    return md.utils.escapeHtml(str)
  },
})

onMounted(async () => {
  try {
    const res = await getArticleById(route.params.id)
    const data = res.data
    // 如果有 rawContent（Markdown原文）则渲染，否则用后端的 HTML
    if (data.rawContent) {
      data.content = md.render(data.rawContent)
    }
    article.value = data
  } catch {
    ElMessage.error('文章不存在')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.article-detail {
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  padding: 40px;
  border-radius: 8px;
}

.article-title {
  font-size: 28px;
  margin-bottom: 16px;
  color: #303133;
}

.article-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #999;
  margin-bottom: 12px;
}

.meta-category {
  color: #409eff;
}

.article-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}

.article-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3) {
  margin-top: 32px;
  margin-bottom: 16px;
}

.article-content :deep(p) {
  margin-bottom: 16px;
}

.article-content :deep(code) {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 14px;
}

.article-content :deep(pre code) {
  background: none;
  padding: 0;
}

.article-content :deep(pre) {
  background: #f6f8fa;
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
  margin-bottom: 16px;
}

.article-content :deep(img) {
  max-width: 100%;
  border-radius: 4px;
}

.article-content :deep(blockquote) {
  border-left: 4px solid #409eff;
  padding-left: 16px;
  color: #666;
  margin: 16px 0;
}
</style>
