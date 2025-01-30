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
export function getTagInfoById(id){
  return request({
    url: '/api/teriTag/info/'+id,
    method: 'get'
  })
}


export function updateStatistics(data){
  return request({
    url: '/api/teriTag/updateStatistics',
    method: 'post',
    data
  })
}


export function getUpdateZip(id) {
  return request({
    url: '/api/teriTag/getTagZip/'+id,
    method: 'get',
    responseType: 'blob'
  })
}