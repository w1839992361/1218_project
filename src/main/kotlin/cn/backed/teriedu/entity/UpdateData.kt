package cn.backed.teriedu.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("data_version")
data class UpdateData (
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var version: String? = null,
    var updateTime: LocalDateTime? = null,
    )