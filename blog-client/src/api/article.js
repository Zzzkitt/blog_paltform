import request from '@/utils/request'

export const getList = (params) => {
  return request.get('/admin/articles', { params })
}

export const getById = (id) => {
  return request.get(`/admin/articles/${id}`)
}

export const create = (data) => {
  return request.post('/admin/articles', data)
}

export const update = (id, data) => {
  return request.put(`/admin/articles/${id}`, data)
}

export const deleteById = (id) => {
  return request.delete(`/admin/articles/${id}`)
}

export const updateStatus = (id) => {
  return request.patch(`/admin/articles/${id}/status`)
}
