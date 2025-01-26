package cn.backed.teriedu.controller

import cn.backed.teriedu.annotation.Anybody
import cn.backed.teriedu.annotation.AutoLog
import cn.backed.teriedu.entity.ApiResponse
import cn.backed.teriedu.entity.Covers
import cn.backed.teriedu.service.CoversService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream

/**
 * 封面管理控制器
 * @author Anaple
 */
@RestController
@RequestMapping("/api/covers")
class CoversController @Autowired constructor(private val coversService: CoversService) {


    /**
     * 上传封面
     */
    @Anybody
    @AutoLog
    @PostMapping("/upload")
    fun uploadCover(
        @RequestParam coverFile: MultipartFile,
        @RequestParam title: String,
        @RequestParam description: String
    ): ApiResponse<String> {
        val cover = Covers(
            title = title,
            decription = description
        )
        val coverId = coversService.saveCover(
            cover,
            coverFile.bytes,
            coverFile.originalFilename ?: "unknown.jpg"
        )
        return ApiResponse(200, "Cover uploaded successfully", coverId)
    }

    /**
     * 根据ID获取封面信息
     */
    @Anybody
    @AutoLog
    @GetMapping("/{id}")
    fun getCoverById(@PathVariable id: String): ApiResponse<Covers> {
        val cover = coversService.getCoverById(id)
        return ApiResponse(200, "Success", cover)
    }

    /**
     * 根据URL获取图片流
     */
    @Anybody
    @GetMapping("/stream/{id}")
    fun streamCover(@PathVariable id: String): ResponseEntity<ByteArray> {
        val cover = coversService.getCoverById(id)
        val coverUrl = cover.coverUrl ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        val file = File(coverUrl)
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }

        val headers = HttpHeaders().apply {
            if(file.path.endsWith(".jpg") || file.path.endsWith(".jpeg")) {
                contentType = MediaType.IMAGE_JPEG
            }

            else if (file.path.endsWith(".png")) {
                contentType = MediaType.IMAGE_PNG
            }
        }

        FileInputStream(file).use { inputStream ->
            return ResponseEntity(inputStream.readBytes(), headers, HttpStatus.OK)
        }
    }
}
