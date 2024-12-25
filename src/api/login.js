import request from '@/utils/request'

export function login(params) {
  return request({
    url: '/api/users/login',
    method: 'post',
    params
  })
}


export function reg(data) {
  return request({
    url: '/api/users/register',
    method: 'post',
    data
  })
}


export function getInfo(){
  return request({
    url: '/api/users/info',
    method: 'get'
  })
}