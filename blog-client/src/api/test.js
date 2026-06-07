import request from '@/utils/request.js'

export const ping = () => {
    return request.get('/ping')
}