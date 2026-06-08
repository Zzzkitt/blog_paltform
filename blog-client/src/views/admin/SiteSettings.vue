<template>
  <el-card>
    <template #header><span>站点设置</span></template>
    <el-form v-if="Object.keys(settings).length" label-width="120px">
      <el-form-item v-for="(value, key) in settings" :key="key" :label="key">
        <el-input v-model="settings[key]" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSave">保存</el-button>
      </el-form-item>
    </el-form>
    <el-empty v-else description="暂无设置" />
  </el-card>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSiteSettings, saveSiteSettings } from '@/api/admin'
const settings = reactive({}), submitting = ref(false)
onMounted(async () => {
  try { const r = await getSiteSettings(); Object.assign(settings, r.data) } catch {}
})
const handleSave = async () => {
  submitting.value = true
  try { await saveSiteSettings({ ...settings }); ElMessage.success('保存成功') } finally { submitting.value = false }
}
</script>
