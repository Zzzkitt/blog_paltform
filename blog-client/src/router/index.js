import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  { path: '/', name: 'Home', component: () => import('@/views/public/HomePage.vue') },
  { path: '/articles/:id', name: 'ArticleDetail', component: () => import('@/views/public/ArticleDetail.vue') },
  { path: '/category/:slug', name: 'CategoryArticles', component: () => import('@/views/public/CategoryArticles.vue') },
  { path: '/tag/:slug', name: 'TagArticles', component: () => import('@/views/public/TagArticles.vue') },
  { path: '/archives', name: 'Archives', component: () => import('@/views/public/Archives.vue') },
  { path: '/about', name: 'About', component: () => import('@/views/public/AboutPage.vue') },
  { path: '/links', name: 'FriendLinks', component: () => import('@/views/public/FriendLinks.vue') },
  { path: '/search', name: 'Search', component: () => import('@/views/public/SearchPage.vue') },
  { path: '/login', name: 'Login', component: () => import('@/views/admin/LoginPage.vue') },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'Dashboard', meta: { title: '仪表盘' }, component: () => import('@/views/admin/DashboardPage.vue') },
      { path: 'articles', name: 'ArticleList', meta: { title: '文章管理' }, component: () => import('@/views/admin/ArticleList.vue') },
      { path: 'articles/edit', name: 'ArticleEditor', meta: { title: '新建文章' }, component: () => import('@/views/admin/ArticleEditor.vue') },
      { path: 'articles/edit/:id', name: 'ArticleEdit', meta: { title: '编辑文章' }, component: () => import('@/views/admin/ArticleEditor.vue') },
      { path: 'categories', name: 'CategoryList', meta: { title: '分类管理' }, component: () => import('@/views/admin/CategoryList.vue') },
      { path: 'tags', name: 'TagList', meta: { title: '标签管理' }, component: () => import('@/views/admin/TagList.vue') },
      { path: 'links', name: 'LinkList', meta: { title: '友链管理' }, component: () => import('@/views/admin/LinkList.vue') },
      { path: 'about', name: 'AboutEdit', meta: { title: '关于页面' }, component: () => import('@/views/admin/AboutEdit.vue') },
      { path: 'settings', name: 'SiteSettings', meta: { title: '站点设置' }, component: () => import('@/views/admin/SiteSettings.vue') },
    ],
  },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const authStore = useAuthStore()
    if (!authStore.isLoggedIn) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
