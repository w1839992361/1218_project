
package cn.backed.teriedu.config

import cn.backed.teriedu.interceptor.AuthInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

//@Configuration
//class WebConfig(private val authInterceptor: AuthInterceptor) : WebMvcConfigurer {
//
//    override fun addInterceptors(registry: InterceptorRegistry) {
//        registry.addInterceptor(authInterceptor)
//            .addPathPatterns("/**") // 拦截所有请求
//            .excludePathPatterns("/api/users/login", "/api/users/register",) // 放行登录和注册接口
//    }
//}
