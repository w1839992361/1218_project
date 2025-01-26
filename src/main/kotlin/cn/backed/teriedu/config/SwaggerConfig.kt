package cn.backed.teriedu.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    /**
     * 配置全局 API 信息
     * @return OpenAPI 配置信息
     */
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("视频服务后端 API 文档")
                    .description("此文档包含视频服务后端的所有接口")
                    .version("1.0.0")
            )
    }

    /**
     * 配置分组 API
     * @return GroupedOpenApi 配置信息
     */
    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("public-api")
            .pathsToMatch("/api/**")
            .build()
    }
}
