<template>
  <el-card>
    <template #header>
      <div class="card-header"><span>友链管理</span><el-button type="primary" @click="openDialog()">+ 新增友链</el-button></div>
    </template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="name" label="名称" width="150" />
      <el-table-column prop="url" label="URL" min-width="200" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="60" />
      <el-table-column label="可见" width="60"><template #default="{row}">{{ row.isVisible ? '是' : '否' }}</template></el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button size="small" type="danger">删除</el-button></template></el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
  <el-dialog v-model="visible" :title="isEdit?'编辑友链':'新增友链'" width="500px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="URL" prop="url"><el-input v-model="form.url" /></el-form-item>
      <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
      <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
      <el-form-item label="可见"><el-switch v-model="form.isVisible" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible=false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFriendLinks, createFriendLink, updateFriendLink, deleteFriendLink } from '@/api/admin'
const loading = ref(false), list = ref([]), visible = ref(false), isEdit = ref(false), submitting = ref(false), formRef = ref(null), editId = ref(null)
const form = reactive({ name: '', url: '', description: '', sortOrder: 0, isVisible: true })
const rules = { name: [{required:true,message:'请输入名称'}], url: [{required:true,message:'请输入URL'}] }
const fetchList = async () => { loading.value = true; try { const r = await getFriendLinks(); list.value = r.data } finally { loading.value = false } }
const openDialog = (row) => {
  if (row) { isEdit.value = true; editId.value = row.id; Object.assign(form, row) }
  else { isEdit.value = false; editId.value = null; form.name = ''; form.url = ''; form.description = ''; form.sortOrder = 0; form.isVisible = true }
  visible.value = true
}
const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return; submitting.value = true
  try {
    if (isEdit.value) { await updateFriendLink(editId.value, { ...form }); ElMessage.success('更新成功') }
    else { await createFriendLink({ ...form }); ElMessage.success('创建成功') }
    visible.value = false; fetchList()
  } finally { submitting.value = false }
}
const handleDelete = async (id) => { await deleteFriendLink(id); ElMessage.success('删除成功'); fetchList() }
onMounted(fetchList)
</script>
<style scoped>.card-header{display:flex;justify-content:space-between;align-items:center}</style>
