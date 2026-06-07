import request from '@/utils/request'

export const getList = () => {
  return request.get('/admin/tags')
}

export const getById = (id) => {
  return request.get(`/admin/tags/${id}`)
}

export const create = (data) => {
  return request.post('/admin/tags', data)
}

export const update = (id, data) => {
  return request.put(`/admin/tags/${id}`, data)
}

export const deleteById = (id) => {
  return request.delete(`/admin/tags/${id}`)
}
