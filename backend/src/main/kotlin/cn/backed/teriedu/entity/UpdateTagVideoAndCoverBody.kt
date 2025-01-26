package cn.backed.teriedu.entity

data class UpdateTagVideoAndCoverBody(
    val videoUuid:String,
    val coverUuid:String,
    val docsUuid:String
)
