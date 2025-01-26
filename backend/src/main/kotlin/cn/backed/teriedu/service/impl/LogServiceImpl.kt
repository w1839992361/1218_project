package cn.backed.teriedu.service.impl

import cn.backed.teriedu.entity.Log
import cn.backed.teriedu.mapper.LogMapper
import cn.backed.teriedu.service.LogService
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class LogServiceImpl: ServiceImpl<LogMapper, Log>(), LogService{


    @Autowired
    lateinit var logMapper: LogMapper

    override fun saveLog(log: Log): Boolean {
        if (log.id.isNullOrEmpty()) {
            log.id = UUID.randomUUID().toString() // 生成UUID
        }

        return save(log)
    }

    override fun searchLogsByUserOrUrlWithPagination(
        actionUser: String,
        actionUrl: String,
        page: Long,
        size: Long
    ): Page<Log> {
        val queryWrapper = QueryWrapper<Log>()

        // 检查是否都为空
        if (actionUser.isBlank() && actionUrl.isBlank()) {
            // 如果都为空，返回所有记录
            return logMapper.selectPage(Page(page, size), queryWrapper)
        }

        // 使用动态条件构建
        queryWrapper.apply {
            if (actionUrl.isNotBlank()) {
                like("action_url", actionUrl)
            }
            if (actionUser.isNotBlank()) {
                if (actionUrl.isNotBlank()) {
                    or()
                }
                like("action_user", actionUser)
            }
        }

        return logMapper.selectPage(Page(page, size), queryWrapper)
    }
}