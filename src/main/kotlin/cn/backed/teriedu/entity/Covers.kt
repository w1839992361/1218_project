package cn.backed.teriedu.entity

import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("covers")
data class Covers(
    var id: String? = null,
    var coverUrl: String? = null,
    var title: String? = null,
    var decription: String? = null,
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null

)