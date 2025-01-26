package cn.backed.teriedu.controller

import cn.backed.teriedu.annotation.Anybody
import cn.backed.teriedu.annotation.AutoLog
import cn.backed.teriedu.entity.ApiResponse
import cn.backed.teriedu.entity.Docs
import cn.backed.teriedu.entity.FileResources
import cn.backed.teriedu.entity.enum.ResourcesType
import cn.backed.teriedu.service.DocsService
import cn.backed.teriedu.service.FileResourcesService
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
 * 文档管理控制器
 */
@RestController
@RequestMapping("/api/docs")
class DocsController @Autowired constructor(private val docsService: DocsService) {
    @Autowired
    private lateinit var fileResourcesService: FileResourcesService

    /**
     * 上传文档
     */
    @Anybody
    @AutoLog
    @PostMapping("/upload")
    fun uploadDocs(
        @RequestParam pptFile: MultipartFile,
        @RequestParam leanTaskFile: MultipartFile,
        @RequestParam homeworkFile: MultipartFile,
        @RequestParam designClassFile: MultipartFile,
        @RequestParam title: String,
        @RequestParam resourceUUID:String
    ): ApiResponse<String> {
        val docs = Docs(title = title)
        docsService.saveDocs(
            docs,
            pptFile.bytes, pptFile.originalFilename ?: "unknown.ppt",
            leanTaskFile.bytes, leanTaskFile.originalFilename ?: "unknown.task",
            homeworkFile.bytes, homeworkFile.originalFilename ?: "unknown.homework",
            designClassFile.bytes, designClassFile.originalFilename ?: "unknown.design",
            resourceUUID
        )
        return ApiResponse(200, "Docs uploaded successfully", resourceUUID)
    }
    /**
     * 获取文档信息ResourceId
     */
    @Anybody
    @GetMapping("/info/{id}")
    fun getDocsById(@PathVariable id: String): ApiResponse<Any> {
        val docs = fileResourcesService.getByResourceUUID(id, cn.backed.teriedu.entity.enum.MediaType.Docs)
        return ApiResponse(200, "Docs retrieved successfully", docs)
    }
    /**
     * 获取文档流 fileId
     */
    @Anybody
    @GetMapping("/stream/{id}")
    fun getDocsStream(@PathVariable id: String): ResponseEntity<ByteArray> {
        return streamDocument(id) {
            it.filePath
        }
    }
    @Anybody
    @AutoLog
    @PostMapping("/uploadUrl")
    fun uploadDocsUrl(
        @RequestParam url: String,
        @RequestParam fileType:cn.backed.teriedu.entity.enum.MediaType,
        @RequestParam docType : ResourcesType,
        @RequestParam resourceUUID:String,
        @RequestParam fileName: String,
    ): ApiResponse<Any> {
       val isSave =  fileResourcesService.saveFileResources(fileName, url, fileType, resourceUUID, docType);
        return if (isSave) {
            ApiResponse(200, "Docs uploaded successfully", resourceUUID)
        } else {
            ApiResponse(400, "Docs uploaded failed", resourceUUID)
        }
    }


//    /**
//     * 获取PPT文档流
//     */
//    @Anybody
//    @GetMapping("/stream/ppt/{id}")
//    fun streamPPT(@PathVariable id: String): ResponseEntity<ByteArray> {
//        return streamDocument(id) { it.pptUrl }
//    }
//
//    /**
//     * 获取学习任务文档流
//     */
//    @Anybody
//    @GetMapping("/stream/leanTask/{id}")
//    fun streamLeanTask(@PathVariable id: String): ResponseEntity<ByteArray> {
//        return streamDocument(id) { it.leanTaskUrl }
//    }
//
//    /**
//     * 获取作业文档流
//     */
//    @Anybody
//    @GetMapping("/stream/homework/{id}")
//    fun streamHomework(@PathVariable id: String): ResponseEntity<ByteArray> {
//        return streamDocument(id) { it.homeworkUrl }
//    }
//
//    /**
//     * 获取设计课程文档流
//     */
//    @Anybody
//    @GetMapping("/stream/designClass/{id}")
//    fun streamDesignClass(@PathVariable id: String): ResponseEntity<ByteArray> {
//        return streamDocument(id) { it.designClassUrl }
//    }
//
    /**
     * 通用文档流接口
     */
    private fun streamDocument(
        id: String,
        filePathExtractor: (FileResources) -> String?
    ): ResponseEntity<ByteArray> {

        val docs = fileResourcesService.getByID(id)
        val filePath = filePathExtractor(docs) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        val file = File(filePath)
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_OCTET_STREAM
        }

        FileInputStream(file).use { inputStream ->
            return ResponseEntity(inputStream.readBytes(), headers, HttpStatus.OK)
        }
    }
}
