package cn.backed.teriedu.entity

import cn.backed.teriedu.entity.enum.MediaType
import cn.backed.teriedu.entity.enum.ResourcesType
import com.baomidou.mybatisplus.annotation.*
import java.io.Serializable
import java.time.LocalDateTime

@TableName("file_resources")
data class FileResources (
    @TableId
    //主键
    var id: String? = null,
    var fileName: String? = null,
    var filePath: String? = null,
    var fileCode: String? = null, // 资源序号
    var fullResourceCode: String? = null, //完整编号


    var resourceUuid: String? = null,

    var resourceType: ResourcesType? = null,
    var mediaType: MediaType? = null,

    var createTime: LocalDateTime? = null,

    var updateTime: LocalDateTime? = null,
    ): Serializable