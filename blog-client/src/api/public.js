import request from '@/utils/request'

export const getArticles = (params) => {
  return request.get('/articles', { params })
}

export const getArticleById = (id) => {
  return request.get(`/articles/${id}`)
}

export const getTopArticles = () => {
  return request.get('/articles/top')
}

export const getArchive = () => {
  return request.get('/articles/archive')
}

export const searchArticles = (params) => {
  return request.get('/articles/search', { params })
}

export const getCategories = () => {
  return request.get('/categories')
}

export const getCategoryArticles = (slug, params) => {
  return request.get(`/categories/${slug}/articles`, { params })
}

export const getTags = () => {
  return request.get('/tags')
}

export const getTagArticles = (slug, params) => {
  return request.get(`/tags/${slug}/articles`, { params })
}

export const getSiteInfo = () => {
  return request.get('/site-info')
}

export const getPublicFriendLinks = () => {
  return request.get('/friend-links')
}

export const getPublicAbout = () => {
  return request.get('/about')
}
