<template>
  <el-breadcrumb class="app-breadcrumb">
    <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item v-for="item in items" :key="item.path">
      <router-link v-if="item.path" :to="item.path">{{ item.name }}</router-link>
      <span v-else>{{ item.name }}</span>
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
defineProps({
  items: { type: Array, default: () => [] },
})
</script>

<style scoped>
.app-breadcrumb {
  margin-bottom: 16px;
}
</style>
