<template>
  <div class="container">
    <el-card class="about-card">
      <h1>关于</h1>
      <div class="about-content" v-if="content" v-html="content"></div>
      <el-empty v-else description="暂无内容" />
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import MarkdownIt from 'markdown-it'
import { getPublicAbout } from '@/api/public'

const md = new MarkdownIt({ html: true, breaks: true })
const content = ref('')

onMounted(async () => {
  try {
    const r = await getPublicAbout()
    if (r.data?.content) content.value = md.render(r.data.content)
  } catch {}
})
</script>
<style scoped>
.about-card { max-width: 800px; margin: 0 auto; padding: 24px; }
.about-card h1 { margin-bottom: 24px; }
.about-content { font-size: 16px; line-height: 1.8; }
.about-content :deep(h2) { margin-top: 24px; }
.about-content :deep(p) { margin-bottom: 16px; }
</style>
