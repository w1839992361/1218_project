package cn.backed.teriedu.config


import cn.backed.teriedu.authenticationfilter.AnybodyAnnotationRequestMatcher
import cn.backed.teriedu.authenticationfilter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,  // 启用 @Secured
    jsr250Enabled = true,   // 启用 @RolesAllowed
    prePostEnabled = true   // 启用 @PreAuthorize 和 @PostAuthorize
)
class SecurityConfig(private val anybodyAnnotationRequestMatcher: AnybodyAnnotationRequestMatcher){
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {
                it.requestMatchers(anybodyAnnotationRequestMatcher)
                    .permitAll()  // 放行带@Anybody注解的方法
                it.requestMatchers("/api/users/login/**").permitAll()  // 登录注册接口放行
                    .requestMatchers("/api/users/register/**").permitAll() // 公开接口放行
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/**").permitAll()

                    .anyRequest().permitAll()  // 其他接口需要认证
            }
            .addFilterBefore(
                JwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

//    class AnybodyAnnotationRequestMatcher : RequestMatcher {
//        override fun matches(request: HttpServletRequest): Boolean {
//            // 获取处理当前请求的处理器
//            val handler = getHandler(request)
//            if (handler is HandlerMethod) {
//                // 检查方法上是否存在 @Anybody 注解
//                val hasAnybodyAnnotation = handler.hasMethodAnnotation(Anybody::class.java)
//                if (hasAnybodyAnnotation) return true
//            }
//
//            // 检查处理器方法是否带有@Anybody注解
//            return false
//        }
//
//        private fun getHandler(request: HttpServletRequest): HandlerMethod? {
//            val context = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
//            val mapping = context?.request?.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)
//            return mapping as? HandlerMethod
//        }
//    }
}