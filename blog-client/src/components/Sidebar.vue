<template>
  <aside class="sidebar">
    <!-- 分类列表 -->
    <el-card class="sidebar-card">
      <h3>分类</h3>
      <ul class="sidebar-list">
        <li v-for="c in categories" :key="c.id">
          <router-link :to="`/category/${c.slug}`">
            {{ c.name }}
          </router-link>
        </li>
        <li v-if="!categories.length" class="empty">暂无分类</li>
      </ul>
    </el-card>

    <!-- 标签云 -->
    <el-card class="sidebar-card">
      <h3>标签</h3>
      <div class="tag-cloud">
        <router-link
          v-for="t in tags"
          :key="t.id"
          :to="`/tag/${t.slug}`"
          class="tag-item"
        >
          {{ t.name }}
        </router-link>
        <p v-if="!tags.length" class="empty">暂无标签</p>
      </div>
    </el-card>

    <!-- 最新文章 -->
    <el-card class="sidebar-card">
      <h3>最新文章</h3>
      <ul class="sidebar-list">
        <li v-for="a in recentArticles" :key="a.id">
          <router-link :to="`/articles/${a.id}`" class="recent-link">
            {{ a.title }}
          </router-link>
        </li>
        <li v-if="!recentArticles.length" class="empty">暂无文章</li>
      </ul>
    </el-card>
  </aside>
</template>

<script setup>
defineProps({
  categories: { type: Array, default: () => [] },
  tags: { type: Array, default: () => [] },
  recentArticles: { type: Array, default: () => [] },
})
</script>

<style scoped>
.sidebar {
  width: 100%;
}

.sidebar-card {
  margin-bottom: 16px;
}

.sidebar-card h3 {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.sidebar-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-list li {
  padding: 6px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.sidebar-list a {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;
}

.sidebar-list a:hover {
  text-decoration: underline;
}

.recent-link {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  display: inline-block;
  padding: 2px 10px;
  background: #ecf5ff;
  color: #409eff;
  border-radius: 4px;
  font-size: 13px;
  text-decoration: none;
}

.tag-item:hover {
  background: #409eff;
  color: #fff;
}

.empty {
  color: #999;
  font-size: 13px;
}
</style>
