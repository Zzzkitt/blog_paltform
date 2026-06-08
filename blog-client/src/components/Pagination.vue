<template>
  <div class="pagination-wrap" v-if="total > 0">
    <el-pagination
      v-model:current-page="current"
      v-model:page-size="size"
      :total="total"
      :page-sizes="pageSizes"
      layout="total, sizes, prev, pager, next, jumper"
      background
      @change="handleChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 },
  total: { type: Number, default: 0 },
  pageSizes: { type: Array, default: () => [10, 20, 50] },
})

const emit = defineEmits(['update:modelValue', 'update:pageSize', 'change'])

const current = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

const size = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val),
})

const handleChange = (page, pageSize) => {
  emit('change', { page, pageSize })
}
</script>

<style scoped>
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
