package cn.backed.teriedu.entity

/**
 * 通用返回实例类
 * @author Anaple@GPT4o
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
)