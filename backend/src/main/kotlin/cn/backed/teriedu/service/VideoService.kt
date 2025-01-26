package cn.backed.teriedu.service

import cn.backed.teriedu.entity.Video
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import java.util.*

interface VideoService {
    fun saveVideo(video: Video, tag: String, className: String, date: Date, videoBytes: ByteArray, originalFilename: String,coverBytes: ByteArray,coverOriginalFilename: String
                  ,resourceUUID: String): String?
    fun getVideoById(id: String): Video?
    fun searchVideosByTitleOrDescriptionWithPagination(
        titleOrDescription: String,
        page: Long,
        size: Long
    ): Page<Video>
}