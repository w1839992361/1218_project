package cn.backed.teriedu.service

import cn.backed.teriedu.entity.Log
import cn.backed.teriedu.entity.Video
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService


interface LogService:IService<Log> {
    fun saveLog(log: Log): Boolean
    fun searchLogsByUserOrUrlWithPagination(
        actionUser: String,
        actionUrl: String,
        page: Long,
        size: Long
    ): Page<Log>
}