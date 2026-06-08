<template>
  <div class="giscus-wrapper" ref="wrapper"></div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'

const props = defineProps({
  articleId: { type: Number, required: true },
})

const wrapper = ref(null)

const loadGiscus = () => {
  if (!wrapper.value) return

  wrapper.value.innerHTML = ''

  const repo = import.meta.env.VITE_GISCUS_REPO
  if (!repo) {
    wrapper.value.innerHTML = '<p style="color:#999;text-align:center;padding:40px 0">请配置 Giscus 环境变量</p>'
    return
  }

  const script = document.createElement('script')
  script.src = 'https://giscus.app/client.js'
  script.setAttribute('data-repo', repo)
  script.setAttribute('data-repo-id', import.meta.env.VITE_GISCUS_REPO_ID)
  script.setAttribute('data-category', import.meta.env.VITE_GISCUS_CATEGORY)
  script.setAttribute('data-category-id', import.meta.env.VITE_GISCUS_CATEGORY_ID)
  script.setAttribute('data-mapping', 'specific')
  script.setAttribute('data-term', `article-${props.articleId}`)
  script.setAttribute('data-strict', '0')
  script.setAttribute('data-reactions-enabled', '1')
  script.setAttribute('data-emit-metadata', '0')
  script.setAttribute('data-input-position', 'bottom')
  script.setAttribute('data-theme', 'preferred_color_scheme')
  script.setAttribute('data-lang', 'en')
  script.setAttribute('crossorigin', 'anonymous')
  script.async = true
  wrapper.value.appendChild(script)
}

watch(() => props.articleId, () => {
  nextTick(loadGiscus)
}, { immediate: true })
</script>

<style scoped>
.giscus-wrapper {
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #e6e6e6;
}
</style>
