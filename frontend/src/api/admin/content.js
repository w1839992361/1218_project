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

export function updateTree(data, id) {
  return request({
    url: '/api/teriTag/update/' + id,
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

export function uploadCover(params, data) {
  const queryParams = new URLSearchParams(params).toString();
  return request({
    url: `/api/covers/upload?${queryParams}`,
    method: 'POST',
    data,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export function uploadVideo(params, data) {
  const queryParams = new URLSearchParams(params).toString();
  return request({
    url: `/api/videos/upload?${queryParams}`,
    method: 'POST',
    data,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
export function getVideoUUID(id) {
  return request({
    url: `/api/videos/getVideoUUID/${id}`,
    method: 'get',
  })
}

export function getVideoInfo(id) {
  return request({
    url: `/api/videos/info/${id}`,
    method: 'get',
  })
}


export function uploadDocs(params, data) {
  const queryParams = new URLSearchParams(params).toString();
  return request({
    url: `/api/docs/upload?${queryParams}`,
    method: 'POST',
    data,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export function getDocsInfo(id) {
  return request({
    url: `/api/docs/info/${id}`,
    method: 'get',
  })
}
