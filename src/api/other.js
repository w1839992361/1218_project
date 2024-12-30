import request from '@/utils/request'

export function getTagsByLevel(level) {
  return request({
    url: '/api/teriTag/level/'+level,
    method: 'get'
  })
}

export function getTagsByParentId(id){
  return request({
    url: '/api/teriTag/parent/'+id,
    method: 'get'
  })
}

export function getTagsById(id){
  return request({
    url: '/api/teriTag/tree/id/'+id,
    method: 'get'
  })
}
