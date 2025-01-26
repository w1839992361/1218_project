package cn.backed.teriedu.controller

import cn.backed.teriedu.annotation.Anybody
import cn.backed.teriedu.annotation.AutoLog
import cn.backed.teriedu.entity.ApiResponse
import cn.backed.teriedu.entity.UpdateData
import cn.backed.teriedu.service.UpdateService
import cn.backed.teriedu.utils.DBUtils
import cn.backed.teriedu.utils.DatabaseImporter
import cn.backed.teriedu.utils.OutZipUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import javax.sql.DataSource

@RestController
@RequestMapping("/api/update")
class UpdateController(private  var dataSource: DataSource) {

    @Autowired lateinit var updateService: UpdateService
//    @Autowired lateinit var dataSource: DataSource

    private val importer = DatabaseImporter(dataSource)

    @Anybody
    @AutoLog
    @GetMapping("/getUpdateVersion")
    fun getUpdateVersion(): ApiResponse<String> {
        return  ApiResponse(200,"success",updateService.getUpdateVersion())
    }

    @Anybody
    @AutoLog
    @GetMapping("/getAllUpdateFiles/{id}")
    fun getAllUpdateFiles(@PathVariable id: Long):  ApiResponse<List<String>> {
        return  ApiResponse(200,"success",updateService.getAllUpdateFiles(tagId = id))
    }

    @Anybody
    @AutoLog
    @GetMapping("/getSqlFile")


    fun getUpdateFile(): ResponseEntity<out ByteArray> {
        DBUtils.exportDatabaseToSql("./data.sql")

        val file = File("./data.sql")
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_OCTET_STREAM
            contentDisposition = ContentDisposition
                .builder("attachment")
                .filename("files_.sql", StandardCharsets.UTF_8)
                .build()
        }

        FileInputStream(file).use { inputStream ->
            return ResponseEntity(inputStream.readBytes(), headers, HttpStatus.OK)
        }

    }
    @Anybody
    @AutoLog
    @GetMapping("/getUpdateZip/{id}")
    fun getUpdateZip(@PathVariable id: Long): ResponseEntity<out ByteArray> {
        val updateList =  updateService.getAllUpdateFiles(tagId = id)
        OutZipUtils.createZipFromPaths(updateList, "./data.zip")
        val file = File("./data.zip")
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_OCTET_STREAM
            contentDisposition = ContentDisposition
                .builder("attachment")
                .filename("files_.zip", StandardCharsets.UTF_8)
                .build()
        }

        FileInputStream(file).use { inputStream ->
            return ResponseEntity(inputStream.readBytes(), headers, HttpStatus.OK)
        }
    }

    @Anybody
    @AutoLog
    @PostMapping("/updateSqlFile")
    fun updateSqlFile(@RequestParam sqlFile: MultipartFile): ApiResponse<Any> {
        importer.importSqlFile(sqlFile)
        return ApiResponse(200,"更新成功")
    }
    @Anybody
    @AutoLog
    @PostMapping("/updateZip")
    fun updateZip(@RequestParam zipFile: MultipartFile): ApiResponse<Any> {
        importer.unzipFile(zipFile)
        return ApiResponse(200,"更新成功")
    }

    @Anybody
    @AutoLog
    @PostMapping("/createAUpdate")
    fun createAUpdate(@RequestBody  updateData: UpdateData): ApiResponse<Any> {
        val updateData1 = UpdateData(
            version = updateData.version,
            updateTime = updateData.updateTime
        )
        updateService.save(updateData1)
        return ApiResponse(200,"更新成功")
    }


}