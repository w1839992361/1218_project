import request from '@/utils/request'

export function getAllTree(level = 0) {
  return request({
    url: '/api/teriTag/tree/' + level,
    method: 'get'
  })
}


export function addTree(data) {
  return request({
    url: '/api/teriTag',
    method: 'post',
    data
  })
}

export function delTree(id) {
  return request({
    url: '/api/teriTag/delete/' + id,
    method: 'delete',
  })
}
