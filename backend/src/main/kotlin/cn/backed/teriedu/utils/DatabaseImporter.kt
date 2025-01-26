package cn.backed.teriedu.utils

import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.sql.Connection
import java.util.zip.ZipInputStream
import org.springframework.jdbc.datasource.DataSourceUtils
import javax.sql.DataSource
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class DatabaseImporter(private val dataSource: DataSource) {

    /**
     * 导入SQL文件
     * @param sqlFile SQL文件（MultipartFile）
     */
    fun importSqlFile(sqlFile: MultipartFile) {
        var connection: Connection? = null
        try {
            connection = DataSourceUtils.getConnection(dataSource)
            connection.autoCommit = false

            println("Starting SQL import from: ${sqlFile.originalFilename}")

            val statements = parseSqlFile(sqlFile)
            val statement = connection.createStatement()

            var count = 0
            val total = statements.size

            statements.forEach { sql ->
                try {
                    if (sql.isNotBlank()) {
                        statement.execute(sql)
                        count++
                        if (count % 100 == 0) {
                            println("Processed $count of $total statements (${(count.toDouble() / total * 100).toInt()}%)")
                            connection.commit() // 定期提交以避免事务过大
                        }
                    }
                } catch (e: Exception) {
                    println("Error executing SQL statement: ${sql.take(100)}...")
                    println("Error message: ${e.message}")
                }
            }

            connection.commit()
            println("SQL import completed. Total statements executed: $count")

        } catch (e: Exception) {
            connection?.rollback()
            println("Error during SQL import: ${e.message}")
            throw e
        } finally {
            connection?.autoCommit = true
            if (connection != null) {
                DataSourceUtils.releaseConnection(connection, dataSource)
            }
        }
    }

    /**
     * 解析SQL文件，将其分割成单独的SQL语句
     */
    private fun parseSqlFile(sqlFile: MultipartFile): List<String> {
        val statements = mutableListOf<String>()
        val sb = StringBuilder()
        var delimiter = ";"

        BufferedReader(InputStreamReader(sqlFile.inputStream, "UTF-8")).use { reader ->
            reader.forEachLine { line ->
                val trimmedLine = line.trim()

                // 忽略注释和空行
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("--") || trimmedLine.startsWith("/*")) {
                    return@forEachLine
                }

                // 检查是否更改了分隔符
                if (trimmedLine.uppercase().startsWith("DELIMITER ")) {
                    delimiter = trimmedLine.substring(10).trim()
                    return@forEachLine
                }

                sb.append(line).append(" ")

                if (trimmedLine.endsWith(delimiter)) {
                    val sql = sb.toString().removeSuffix(delimiter).trim()
                    if (sql.isNotEmpty()) {
                        statements.add(sql)
                    }
                    sb.clear()
                }
            }
        }

        return statements
    }

    /**
     * 解压ZIP文件到当前目录
     * @param zipFile ZIP文件（MultipartFile）
     * @return 解压后的文件路径列表
     */
    fun unzipFile(zipFile: MultipartFile): List<String> {
        val extractedFiles = mutableListOf<String>()
        val currentDir = File(".")

        try {
            println("Starting unzip process from: ${zipFile.originalFilename}")

            ZipInputStream(zipFile.inputStream.buffered()).use { zipIn ->
                var entry = zipIn.nextEntry
                val buffer = ByteArray(8192)
                var totalFiles = 0

                while (entry != null) {
                    val filePath = File(currentDir, entry.name)

                    // 安全检查：确保解压路径不会超出当前目录
                    if (!filePath.canonicalPath.startsWith(currentDir.canonicalPath)) {
                        println("Skipping suspicious entry: ${entry.name}")
                        entry = zipIn.nextEntry
                        continue
                    }

                    if (entry.isDirectory) {
                        filePath.mkdirs()
                    } else {
                        // 确保父目录存在
                        filePath.parentFile?.mkdirs()

                        // 写入文件（如果文件存在会自动替换）
                        filePath.outputStream().buffered().use { output ->
                            var len: Int
                            while (zipIn.read(buffer).also { len = it } > 0) {
                                output.write(buffer, 0, len)
                            }
                        }
                        extractedFiles.add(filePath.absolutePath)
                        totalFiles++
                        println("Extracted: ${entry.name}")
                    }

                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }

                println("Unzip completed. Total files extracted: $totalFiles")
            }

            return extractedFiles

        } catch (e: Exception) {
            println("Error during unzip: ${e.message}")
            throw e
        }
    }
    /**
     * 创建临时目录
     */
    private fun createTempDirectory(): File {
        return Files.createTempDirectory("import_").toFile().apply {
            deleteOnExit()
        }
    }

//    /**
//     * 完整的导入过程：解压ZIP文件并导入SQL
//     * @param zipFile ZIP文件（MultipartFile）
//     */
//    fun importFromZip(zipFile: MultipartFile) {
//        var tempDir: File? = null
//        try {
//            println("Starting import process...")
//
//            // 创建临时目录
//            tempDir = createTempDirectory()
//
//            // 解压ZIP文件
//            val extractedFiles = unzipFile(zipFile)
//
//            // 查找SQL文件
//            val sqlFile = extractedFiles.find { it.lowercase().endsWith(".sql") }
//                ?: throw IllegalStateException("No SQL file found in the ZIP archive")
//
//            // 将SQL文件转换为MultipartFile
//            val sqlMultipartFile = object : MultipartFile {
//                private val file = File(sqlFile)
//
//                override fun getName() = file.name
//                override fun getOriginalFilename() = file.name
//                override fun getContentType() = "application/sql"
//                override fun isEmpty() = file.length() == 0L
//                override fun getSize() = file.length()
//                override fun getBytes() = Files.readAllBytes(file.toPath())
//                override fun getInputStream() = Files.newInputStream(file.toPath())
//                override fun transferTo(dest: File): Unit = Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING)
//            }
//
//            // 导入SQL文件
//            importSqlFile(sqlMultipartFile)
//            println("Import process completed successfully")
//
//        } catch (e: Exception) {
//            println("Error during import process: ${e.message}")
//            throw e
//        } finally {
//            // 清理临时文件
//            tempDir?.deleteRecursively()
//        }
//    }
}