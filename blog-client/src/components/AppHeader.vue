<template>
  <el-header class="app-header">
    <div class="header-inner">
      <router-link to="/" class="logo">My Blog</router-link>
      <nav class="nav">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/archives" class="nav-link">归档</router-link>
        <router-link to="/about" class="nav-link">关于</router-link>
        <router-link to="/links" class="nav-link">友链</router-link>
      </nav>
      <div class="search-box">
        <el-input
          v-model="keyword"
          placeholder="搜索文章..."
          :prefix-icon="Search"
          size="default"
          clearable
          @keyup.enter="handleSearch"
        />
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const keyword = ref('')

const handleSearch = () => {
  if (keyword.value.trim()) {
    router.push({ path: '/search', query: { q: keyword.value.trim() } })
  }
}
</script>

<style scoped>
.app-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 20px;
}

.logo {
  font-size: 22px;
  font-weight: bold;
  color: #409eff;
  text-decoration: none;
  margin-right: 40px;
}

.nav {
  display: flex;
  gap: 24px;
  flex: 1;
}

.nav-link {
  color: #333;
  text-decoration: none;
  font-size: 15px;
}

.nav-link:hover {
  color: #409eff;
}

.search-box {
  width: 200px;
}
</style>
