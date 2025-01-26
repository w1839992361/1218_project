package cn.backed.teriedu.controller

import cn.backed.teriedu.annotation.AutoLog
import cn.backed.teriedu.entity.ApiResponse
import cn.backed.teriedu.service.LogService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/logs")
class LogController {

    @Autowired
    lateinit var logService: LogService

    @GetMapping("/table")
   // @AutoLog
    fun getLogsTable(@RequestParam actionUser: String, @RequestParam actionUrl: String, @RequestParam page: Long, @RequestParam size: Long):ApiResponse<Any>{
        val tableList = logService.searchLogsByUserOrUrlWithPagination(actionUser, actionUrl, page, size)
        return ApiResponse(200,"success",tableList)
    }
}