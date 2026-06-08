<template>
  <div class="container">
    <h1 class="page-title">友情链接</h1>
    <el-row :gutter="16">
      <el-col :span="8" v-for="link in links" :key="link.id" style="margin-bottom:16px">
        <el-card shadow="hover" @click="openUrl(link.url)" class="link-card">
          <div class="link-name">{{ link.name }}</div>
          <div class="link-desc" v-if="link.description">{{ link.description }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!links.length" description="暂无友链" />
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getPublicFriendLinks } from '@/api/public'

const links = ref([])
onMounted(async () => {
  try { const r = await getPublicFriendLinks(); links.value = r.data } catch {}
})
const openUrl = (url) => { window.open(url, '_blank') }
</script>
<style scoped>
.page-title { font-size: 24px; margin-bottom: 20px; }
.link-card { cursor: pointer; }
.link-card:hover { transform: translateY(-2px); }
.link-name { font-size: 16px; font-weight: bold; color: #409eff; }
.link-desc { font-size: 13px; color: #999; margin-top: 8px; }
</style>
