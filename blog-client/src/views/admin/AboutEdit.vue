<template>
  <el-card>
    <template #header><span>关于页面编辑</span></template>
    <el-form>
      <el-form-item label="内容">
        <mavon-editor v-model="content" style="min-height:400px" :ishljs="true" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSave">保存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAbout, updateAbout } from '@/api/admin'
const content = ref(''), submitting = ref(false)
onMounted(async () => { try { const r = await getAbout(); content.value = r.data?.content || '' } catch {} })
const handleSave = async () => {
  submitting.value = true
  try { await updateAbout({ content: content.value }); ElMessage.success('保存成功') } finally { submitting.value = false }
}
</script>
