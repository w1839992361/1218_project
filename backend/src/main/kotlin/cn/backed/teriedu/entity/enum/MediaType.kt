package cn.backed.teriedu.entity.enum

enum class MediaType {
    Docs,
    IMAGE,
    VIDEO,
    AUDIO,
    OTHER;
    fun getMediaType(): String {
        return when (this) {
            Docs -> "docs"
            IMAGE -> "image"
            VIDEO -> "video"
            AUDIO -> "audio"
            OTHER -> "other"
        }
    }
    fun getMediaTypeByCode(code: String): MediaType {
        return when (code) {
            "docs" -> Docs
            "image" -> IMAGE
            "video" -> VIDEO
            "audio" -> AUDIO
            else -> OTHER
        }
    }
    fun getCode():Int{
        return ordinal + 1
    }
}