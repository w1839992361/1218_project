package cn.backed.teriedu.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime
import java.util.*

/**
 * 视频实体类
 *
 * @author Anaple@GPT4o
 */
@TableName("videos")
data class Video(
    var id: String? = null, // 使用UUID作为ID
    var title: String? = null, // 视频标题
    var description: String? = null, // 视频描述
    var url: String? = null, // 视频URL
    var coverUrl: String? = null, // 视频封面URL
    var uploadTime: LocalDateTime? = null // 上传时间

)