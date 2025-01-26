import request from '@/utils/request'

export function searchVideo(params) {
  return request({
    url: '/api/globalSearch/searchVideo',
    method: 'get',
    params
  })
}