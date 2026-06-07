import { defineStore } from 'pinia'
import { login as loginApi } from '@/api/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        // 初始化时从 localStorage 读取 token
        token: localStorage.getItem('token') || '',
        // 可选：存储用户信息（用户名、角色等）
        userInfo: null
    }),
    getters: {
        // 是否已登录（根据 token 是否存在判断）
        isLoggedIn: (state) => !!state.token
    },
    actions: {
        // 登录：调用 API，保存 token 和用户信息
        async login(username, password) {
            try {
                const res = await loginApi({ username, password })
                // 假设后端返回数据格式：{ code: 200, data: { token, username, role } }
                const { token, username: userName, role } = res.data
                this.token = token
                localStorage.setItem('token', token)
                this.userInfo = { username: userName, role }
                return Promise.resolve()
            } catch (error) {
                // 错误已由 axios 拦截器处理，此处只需向上传递
                return Promise.reject(error)
            }
        },
        // 退出登录：清除 token 和用户信息，跳转到登录页
        logout() {
            this.token = ''
            localStorage.removeItem('token')
            this.userInfo = null
            router.push('/login')
        }
    }
})