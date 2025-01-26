package cn.backed.teriedu.entity

import com.baomidou.mybatisplus.annotation.TableName
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizedUrl
import java.time.LocalDateTime

@TableName("docs")
data class Docs(
    var id: String? = null,
    var title: String? = null,
    var pptUrl: String? = null,
    var leanTaskUrl: String? = null,
    var homeworkUrl: String? = null,
    var designClassUrl: String? = null,
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null
)