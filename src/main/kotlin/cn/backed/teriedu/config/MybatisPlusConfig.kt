package cn.backed.teriedu.config

import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import org.apache.ibatis.type.LocalDateTimeTypeHandler
import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@MapperScan("cn.backed.teriedu.mapper")
class MybatisPlusConfig {
    @Bean
    fun mybatisPlusTypeHandler(): LocalDateTimeTypeHandler {
        return LocalDateTimeTypeHandler()
    }

    /**
     * 添加分页插件
     */
    @Bean
    fun mybatisPlusInterceptor(): MybatisPlusInterceptor {
        val interceptor = MybatisPlusInterceptor()
        interceptor.addInnerInterceptor(PaginationInnerInterceptor(DbType.MYSQL)) // 如果配置多个插件, 切记分页最后添加
        // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
        return interceptor
    }




}