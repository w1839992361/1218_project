package cn.backed.teriedu.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import org.apache.ibatis.type.StringTypeHandler
import java.time.LocalDateTime
@TableName("log")
data class Log (
    @TableId
    var id: String? = null,
    var actionUrl: String? = null,
    var actionMethod: String? = null,
    @TableField("action_params", typeHandler = StringTypeHandler::class)
    var actionParams: String? = null,
    @TableField("action_result", typeHandler = StringTypeHandler::class)
    var actionResult: String? = null,
    var actionTime: LocalDateTime? = null,
    var actionUser: String? = null,
)