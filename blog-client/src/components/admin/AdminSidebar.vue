<template>
  <el-menu
    :default-active="route.path"
    router
    :collapse="isCollapse"
    class="admin-sidebar"
  >
    <el-menu-item index="/admin">
      <el-icon><Odometer /></el-icon>
      <span>仪表盘</span>
    </el-menu-item>

    <el-sub-menu index="content">
      <template #title>
        <el-icon><Document /></el-icon>
        <span>内容管理</span>
      </template>
      <el-menu-item index="/admin/articles">文章管理</el-menu-item>
      <el-menu-item index="/admin/categories">分类管理</el-menu-item>
      <el-menu-item index="/admin/tags">标签管理</el-menu-item>
    </el-sub-menu>

    <el-sub-menu index="system">
      <template #title>
        <el-icon><Setting /></el-icon>
        <span>系统</span>
      </template>
      <el-menu-item index="/admin/links">友链管理</el-menu-item>
      <el-menu-item index="/admin/about">关于页面</el-menu-item>
      <el-menu-item index="/admin/settings">站点设置</el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { Odometer, Document, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const isCollapse = ref(false)
</script>

<style scoped>
.admin-sidebar {
  height: 100%;
  border-right: none;
}
</style>
