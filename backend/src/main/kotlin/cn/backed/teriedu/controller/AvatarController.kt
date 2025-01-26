package cn.backed.teriedu.controller

import cn.backed.teriedu.annotation.Anybody
import cn.backed.teriedu.annotation.AutoLog
import cn.backed.teriedu.entity.ApiResponse
import cn.backed.teriedu.utils.FileUploadUtils
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.IOException


@RequestMapping("/api/avatar")
@RestController
class AvatarController {
    @Autowired
    lateinit var fileUploadUtils: FileUploadUtils

    private val logger = LoggerFactory.getLogger(javaClass)

    private val allowedExtensions = setOf("jpg", "jpeg", "png", "gif", "webp")

    @Anybody
    @AutoLog
    @PostMapping("/upload")
    fun uploadAvatar(@RequestParam avatarFile: MultipartFile): ApiResponse<String> {
        // 处理上传头像的逻辑

        var filePath = fileUploadUtils.saveAvatar(avatarFile.bytes, avatarFile.originalFilename!!)
        filePath =  filePath.replace("\\", "/")
        filePath = filePath.removePrefix("./")
        return ApiResponse(200, filePath)
    }

    @Anybody
    @GetMapping("/stream/**")
    fun getAvatar(request: HttpServletRequest): ResponseEntity<ByteArray> {

        var path = request.requestURI.replace("/api/avatar/stream/", "").replace("\\", "/")
        path = "./$path"

        // 定义允许的基础路径
        val baseDir = File(fileUploadUtils.userAvatarPath)  // 替换为你的实际路径


        // 清理路径中的潜在危险字符
//        val sanitizedPath = path.replace(Regex("[^a-zA-Z0-9.-/]"), "")
//            .replace("..", "")

        // 构建完整文件路径

        val file = File(path)
        // 检查文件是否在允许的目录内
        if (!file.canonicalPath.startsWith(baseDir.canonicalPath)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access to this path is forbidden".toByteArray())
        }

        // 检查文件是否存在
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("File not found".toByteArray())
        }

        // 在代码中验证
        if (!allowedExtensions.contains(file.extension.lowercase())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("File type not allowed".toByteArray())
        }

        val contentType = when {
            path.lowercase().endsWith(".jpg") || path.lowercase().endsWith(".jpeg") ->
                MediaType.IMAGE_JPEG
            path.lowercase().endsWith(".png") ->
                MediaType.IMAGE_PNG
            path.lowercase().endsWith(".gif") ->
                MediaType.IMAGE_GIF
            path.lowercase().endsWith(".webp") ->
                MediaType.parseMediaType("image/webp")
            else -> MediaType.APPLICATION_OCTET_STREAM
        }

        val headers = HttpHeaders().apply {
            this.contentType = contentType
            // 修正缓存控制的设置
//            this.setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic().getHeaderValue())
            // 或者直接设置缓存控制头
             this.set(HttpHeaders.CACHE_CONTROL, "public, max-age=${30 * 24 * 60 * 60}")

            contentDisposition = ContentDisposition.builder("inline")
                .filename(file.name)
                .build()
        }
        return try {
            FileInputStream(file).use { inputStream ->
                ResponseEntity(inputStream.readBytes(), headers, HttpStatus.OK)
            }
        } catch (e: IOException) {
            logger.error("Error processing file: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }


}