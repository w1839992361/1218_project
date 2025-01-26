package cn.backed.teriedu.aop

import cn.backed.teriedu.annotation.AutoLog
import cn.backed.teriedu.entity.Log
import cn.backed.teriedu.service.LogService
import cn.backed.teriedu.utils.JwtUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.text.SimpleDateFormat
import java.time.LocalDateTime

@Aspect
@Component
@Order(1)   // 添加顺序注解，确保切面优先级
class AutoLogAspect(
// 假设这是你的JWT工具类
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    // 创建配置好的 ObjectMapper
    private val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    @Autowired
    lateinit var logService: LogService

    // 修改切点表达式，确保能正确匹配到注解
    @Pointcut("@annotation(cn.backed.teriedu.annotation.AutoLog)")
    fun autoLogPointcut() {
    }

    @Around("autoLogPointcut()")
    fun around(point: ProceedingJoinPoint): Any? {
        val startTime = System.currentTimeMillis()

        // 获取请求信息
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val method = request.method
        val url = request.requestURI

        // 获取请求参数
        val params = point.args
        val paramsJson = try {
            objectMapper.writeValueAsString(params)
        } catch (e: Exception) {
            log.error("序列化请求参数失败", e)
            "序列化失败: ${e.message}"
        }


        // 获取token中的用户信息
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
        val username = token?.let { JwtUtils.validateToken(it) }

        // 执行原方法
        val result = point.proceed()
        // 序列化响应结果
        val resultJson = try {
            objectMapper.writeValueAsString(result)
        } catch (e: Exception) {
            log.error("序列化响应结果失败", e)
            "序列化失败: ${e.message}"
        }

        // 创建日志对象
        val log = Log(
            actionUrl = url,
            actionMethod = method,
            actionParams = paramsJson,
            actionResult = resultJson,
            actionTime = LocalDateTime.now(),
            actionUser = username
        )

        // 保存日志(这里需要你实现具体的保存逻辑)
        saveLog(log)

        return result
    }

    private fun saveLog(log: Log) {
        // 实现日志保存逻辑
        // 可以注入LogRepository或LogService来保存
        // 这里仅作示例，直接打印到控制台
        println("保存日志：$log")
        try {
            logService.saveLog(log)
        }catch( e: Exception) {
            log.actionResult = "保存日志失败: ${e.message}"
            log.actionParams = "保存日志失败: ${e.message}"

            logService.saveLog(log)

        }

    }
}