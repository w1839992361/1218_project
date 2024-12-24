import request from '@/utils/request'

export function getFile(name) {
    return request({
        url: name,
        method: 'get',
        responseType: 'blob'
    })
}