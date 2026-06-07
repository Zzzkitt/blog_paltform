<template>
  <div id ="app">
    <router-view />
  </div>
</template>

<script setup>
import {onMounted} from "vue";
import {ping} from '@/api/test'

onMounted(async () => {
  try {
    const result = await ping()
    console.log('后端响应:', result)
  } catch (error) {
    console.error('请求失败:', error)
  }
})
</script>
