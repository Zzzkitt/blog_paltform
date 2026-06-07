<template>
  <el-container class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="admin-aside">
      <div class="logo">博客管理</div>
      <AdminSidebar />
    </el-aside>

    <!-- 主区域 -->
    <el-container>
      <!-- 顶部 -->
      <el-header class="admin-header">
        <el-breadcrumb>
          <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="header-right">
          <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>

      <!-- 内容 -->
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import AdminSidebar from '@/components/admin/AdminSidebar.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const handleLogout = async () => {
  await ElMessageBox.confirm('确定要退出登录吗？', '提示')
  authStore.logout()
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.admin-aside {
  background: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.admin-main {
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
