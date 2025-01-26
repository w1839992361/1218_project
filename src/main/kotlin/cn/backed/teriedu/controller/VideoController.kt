package cn.backed.teriedu.controller

import cn.backed.teriedu.annotation.Anybody
import cn.backed.teriedu.annotation.AutoLog
import cn.backed.teriedu.entity.ApiResponse
import cn.backed.teriedu.entity.Video
import cn.backed.teriedu.entity.enum.ResourcesType
import cn.backed.teriedu.service.FileResourcesService
import cn.backed.teriedu.service.VideoService
import cn.backed.teriedu.utils.FileUploadUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.time.LocalDateTime
import java.util.*

/**
 * 视频控制器
 * @author Anaple@GPT4o
 */
@RestController
@RequestMapping("/api/videos")
class VideoController @Autowired constructor(
    private val videoService: VideoService,
    private val fileResourcesService: FileResourcesService
) {


    /**
     * 上传视频接口
     * @param file 上传的视频文件
     * @param tag 标签名称
     * @param className 年级
     * @param title 视频标题
     * @param description 视频描述
     * @return 上传结果
     */
    @PostMapping("/upload")
    @Anybody
    @AutoLog
    fun uploadVideo(
        @RequestParam videoFile: MultipartFile,
        @RequestParam tag: String,
        @RequestParam className: String,
        @RequestParam title: String,
         
        @RequestParam coverFile: MultipartFile,
        @RequestParam description: String,
        @RequestParam resourceUUID: String
    ): ApiResponse<Any> {
        val video = Video(
            title = title,
            description = description,
            uploadTime = LocalDateTime.now()
        )
        val videoId = videoService.saveVideo(
            video,
            tag,
            className,
            Date(),
            videoFile.bytes,
            videoFile.originalFilename ?: "unknown.mp4",
            coverFile.bytes,
            coverFile.originalFilename ?: "unknown.jpg",
            resourceUUID
        ) ?: return ApiResponse(500,"upload failed")



        return ApiResponse(200, "Video uploaded successfully", videoId)
    }

    /**
     * 上传外链接口
     * @param url 上传的视频外链
     * @param tag 标签名称
     * @param className 年级
     * @param title 视频标题
     * @param description 视频描述
     * @return 上传结果
     */
    @PostMapping("/uploadUrl")
    @Anybody
    @AutoLog
    fun uploadVideoUrl(
        @RequestParam url: String,
        @RequestParam tag: String,
        @RequestParam className: String,
        @RequestParam title: String,
        @RequestParam description: String,
        @RequestParam resourceUUID: String
    ): ApiResponse<Any> {
        val video = Video(
            title = title,
            description = description,
            uploadTime = LocalDateTime.now()
        )
        video.title?.let {
            video.url?.let { it1 ->
                fileResourcesService.saveFileResources(
                    it,
                    it1,
                    cn.backed.teriedu.entity.enum.MediaType.VIDEO,
                    resourceUUID,
                    ResourcesType.PremiumCourse
                )
            }
        }

        return ApiResponse(200, "Video url  uploaded successfully")
    }


    /**
     * 获取视频流接口
     * @param id 视频ID
     * @return 视频流
     */
    @Anybody
    @GetMapping("/stream/{id}")
    fun streamVideo(@PathVariable id: String): ResponseEntity<ByteArray> {

        val video = videoService.getVideoById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)

        if (video.url != null){
            val file = File("" + video.url)
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
            }

            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_OCTET_STREAM
                setContentDispositionFormData("inline", file.name)
            }

            FileInputStream(file).use { inputStream ->
                return ResponseEntity(inputStream.readBytes(), headers, HttpStatus.OK)
            }
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)

    }
    /**
     * 获取视频资源
     * @param id 通用视频ID
     */

    @Anybody
    @GetMapping("/getVideoUUID/{id}")
    fun getVideoUUID(@PathVariable id: String):ApiResponse<Any>{
       val videos = fileResourcesService.getResourcesByResourceUUIDAndMediaTypeAndResourceType(id, cn.backed.teriedu.entity.enum.MediaType.VIDEO , ResourcesType.PremiumCourse)
        var uuids = mutableListOf<String>()
        for (video in videos){
            video.filePath?.let { uuids.add(it) }
        }
        return ApiResponse(200,"success" ,uuids)
    }
    /**
     * 获取视频信息
     * @param id 视频ID
     * @return video对象
     */
    @Anybody
    @GetMapping("/info/{id}")
    fun videoInfo(
        @PathVariable id: String
    ):ApiResponse<Any>{
       var video = videoService.getVideoById(id);
        if(video !=null) {
            video.coverUrl = ""
            video.url = ""
            return ApiResponse(200,"success" ,video)
        }
        return ApiResponse(500,"fail")
    }

    /**
     * 获取视频封面
     * @param id
     * @return video
     */
    @Anybody
    @GetMapping("/cover/{id}")
    fun getCover(@PathVariable id: String): ResponseEntity<ByteArray> {
        val video = videoService.getVideoById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)

        if (video.coverUrl != null){
            val file = File("" + video.coverUrl)
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
            }

            val headers = HttpHeaders().apply {
                contentType = MediaType.IMAGE_JPEG
                setContentDispositionFormData("inline", file.name)
            }
            FileInputStream(file).use { inputStream ->
                return ResponseEntity(inputStream.readBytes(), headers, HttpStatus.OK)
            }
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
    }
}
