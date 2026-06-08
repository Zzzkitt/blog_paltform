<template>
  <div class="dashboard">
    <h2 class="page-title">仪表盘</h2>
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in cards" :key="card.label">
        <el-card class="stat-card">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/admin'
const cards = ref([
  { label: '文章总数', value: 0 },
  { label: '已发布', value: 0 },
  { label: '草稿', value: 0 },
  { label: '分类', value: 0 },
  { label: '标签', value: 0 },
])
onMounted(async () => {
  try {
    const r = await getDashboard()
    const d = r.data
    cards.value = [
      { label: '文章总数', value: d.totalArticles },
      { label: '已发布', value: d.publishedArticles },
      { label: '草稿', value: d.draftArticles },
      { label: '分类', value: d.totalCategories },
      { label: '标签', value: d.totalTags },
    ]
  } catch {}
})
</script>
<style scoped>
.page-title { font-size: 24px; margin-bottom: 20px; }
.stat-card { text-align: center; margin-bottom: 16px; }
.stat-value { font-size: 36px; font-weight: bold; color: #409eff; }
.stat-label { font-size: 14px; color: #999; margin-top: 8px; }
</style>
