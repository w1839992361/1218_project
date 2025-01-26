package cn.backed.teriedu.interceptor


import org.slf4j.LoggerFactory
import cn.backed.teriedu.annotation.Anybody
import cn.backed.teriedu.utils.JwtUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor


@Component
class AuthInterceptor : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(AuthInterceptor::class.java)
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        logger.info("AuthInterceptor invoked for path: {}", request.requestURI)
        if (handler is HandlerMethod) {
            // 检查方法上是否存在 @Anybody 注解
            val hasAnybodyAnnotation = handler.hasMethodAnnotation(Anybody::class.java)
            if (hasAnybodyAnnotation) return true

            // 获取 Token
            val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            if (token.isNullOrBlank()) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Missing or invalid token")
                return false
            }

            // 验证 Token
            val username = JwtUtils.validateToken(token)
            if (username.isNullOrBlank()) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Invalid token")
                return false
            }

            // 将解析的用户名设置到请求属性中
            request.setAttribute("username", username)
        }
        return true
    }

}
