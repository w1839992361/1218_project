import request from '@/utils/request'

export function updateZip(data) {
    return request({
        url: '/api/update/updateZip',
        method: 'post',
        data,
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    })
}


export function updateSqlFile(data) {
    return request({
        url: '/api/update/updateSqlFile',
        method: 'post',
        data,
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    })
}



export function getSqlFile() {
    return request({
        url: '/api/update/getSqlFile',
        method: 'get'
    })
}



export function getUpdateZip() {
    return request({
        url: '/api/update/getUpdateZip',
        method: 'get'
    })
}


export function getUpdateVersion() {
    return request({
        url: '/api/update/getUpdateVersion',
        method: 'get'
    })
}

