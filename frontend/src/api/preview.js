import request from '@/utils/request'

export function getFile(name) {
    return request({
        url: name,
        method: 'get',
        responseType: 'blob'
    })
}

export function getPreview(url){
    return request({
        url: url,
        method: 'get',
    })
}