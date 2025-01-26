package cn.backed.teriedu.authenticationfilter

import cn.backed.teriedu.annotation.Anybody
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@Component
class AnybodyAnnotationRequestMatcher : RequestMatcher {
    @Autowired
    private lateinit var handlerMappingHandlerMapping: RequestMappingHandlerMapping

    override fun matches(request: HttpServletRequest): Boolean {
        try {

            val handler = handlerMappingHandlerMapping.getHandler(request)?.handler
            if (handler is HandlerMethod) {
                // 检查方法上是否有@Anybody注解
                if (handler.hasMethodAnnotation(Anybody::class.java)) {
                    return true
                }
                // 检查类上是否有@Anybody注解
                if (handler.beanType.isAnnotationPresent(Anybody::class.java)) {
                    return true
                }
            }
        } catch (e: Exception) {
            // 如果出现异常，默认不放行
            return false
        }
        return false
    }
}