import request from '@/utils/request'

export const getList = () => {
  return request.get('/admin/categories')
}

export const getById = (id) => {
  return request.get(`/admin/categories/${id}`)
}

export const create = (data) => {
  return request.post('/admin/categories', data)
}

export const update = (id, data) => {
  return request.put(`/admin/categories/${id}`, data)
}

export const deleteById = (id) => {
  return request.delete(`/admin/categories/${id}`)
}
