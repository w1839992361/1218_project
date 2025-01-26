package cn.backed.teriedu.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("video_history")
data class VideoHistory(
    @TableId
    var historyId:String?=null,
    var videoId:String?=null,
    var userId:String?=null,
    var watchTime: LocalDateTime?=null,
)
