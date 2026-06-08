<template>
  <el-footer class="app-footer">
    <p>
      &copy; {{ year }} My Blog
      <a :href="rssUrl" class="rss-link" target="_blank" title="RSS 订阅">
        RSS
      </a>
    </p>
  </el-footer>
</template>

<script setup>
const year = new Date().getFullYear()
const rssUrl = `${import.meta.env.VITE_API_BASE_URL || '/api'}/rss`
</script>

<style scoped>
.app-footer {
  text-align: center;
  padding: 24px;
  color: #999;
  font-size: 14px;
  border-top: 1px solid #e6e6e6;
}

.rss-link {
  margin-left: 8px;
  color: #f60;
  text-decoration: none;
  font-weight: bold;
}

.rss-link:hover {
  text-decoration: underline;
}
</style>
