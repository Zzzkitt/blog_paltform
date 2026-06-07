import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  // 前台展示
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/public/HomePage.vue'),
  },
  // 登录
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/admin/LoginPage.vue'),
  },
  // 后台管理
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        meta: { title: '仪表盘' },
        component: () => import('@/views/admin/DashboardPage.vue'),
      },
      // 文章
      {
        path: 'articles',
        name: 'ArticleList',
        meta: { title: '文章管理' },
        component: () => import('@/views/admin/ArticleList.vue'),
      },
      {
        path: 'articles/edit',
        name: 'ArticleEditor',
        meta: { title: '新建文章' },
        component: () => import('@/views/admin/ArticleEditor.vue'),
      },
      {
        path: 'articles/edit/:id',
        name: 'ArticleEdit',
        meta: { title: '编辑文章' },
        component: () => import('@/views/admin/ArticleEditor.vue'),
      },
      // 分类
      {
        path: 'categories',
        name: 'CategoryList',
        meta: { title: '分类管理' },
        component: () => import('@/views/admin/CategoryList.vue'),
      },
      // 标签
      {
        path: 'tags',
        name: 'TagList',
        meta: { title: '标签管理' },
        component: () => import('@/views/admin/TagList.vue'),
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
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
