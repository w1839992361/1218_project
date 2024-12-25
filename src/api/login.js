import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/api/users/login',
    method: 'post',
    data
  })
}


export function reg(data) {
  return request({
    url: '/api/users/register',
    method: 'post',
    data
  })
}