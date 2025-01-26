package cn.backed.teriedu.entity

import com.baomidou.mybatisplus.annotation.*
import java.io.Serializable
import java.time.LocalDateTime

/**
 * @Author: Bamboo
 * @Description: Entity class for TeriTag
 */
@TableName("teri_tag")
data class TeriTag(
    @TableId (type = IdType.AUTO)
    val id: Long? = null,
    var name: String? = null,
    var description: String? = null,
    var code:String? = null,
    var sequence: Int? = null,
    var parentId: Long? = null,
    var level: Int? = null,
    var resourceUuid: String? = null,
    var coverUuid:String? = null,

    var subNum:Long? = null,
    var favoriteNum:Long? = null,
    var viewNum:Long? = null,


    @TableField(exist = false) // 标记为非数据库字段
    var children: MutableList<TeriTag>? = null // 添加 children 属性
) : Serializable
