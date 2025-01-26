import request from '@/utils/request'

export function getList(params) {
    return request({
        url: '/api/users/table',
        method: 'get',
        params
    })
}

export function update(data) {
    return request({
        url: '/api/users/update',
        method: 'post',
        data
    })
}