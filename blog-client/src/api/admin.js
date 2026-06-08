import request from '@/utils/request'

// 仪表盘
export const getDashboard = () => request.get('/admin/dashboard')

// 友链管理
export const getFriendLinks = () => request.get('/admin/friend-links')
export const createFriendLink = (data) => request.post('/admin/friend-links', data)
export const updateFriendLink = (id, data) => request.put(`/admin/friend-links/${id}`, data)
export const deleteFriendLink = (id) => request.delete(`/admin/friend-links/${id}`)

// 关于页面
export const getAbout = () => request.get('/about')
export const updateAbout = (data) => request.put('/admin/about', data)

// 站点设置
export const getSiteSettings = () => request.get('/admin/site-settings')
export const saveSiteSettings = (data) => request.post('/admin/site-settings', data)
