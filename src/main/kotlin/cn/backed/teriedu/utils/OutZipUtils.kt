package cn.backed.teriedu.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object  OutZipUtils {
    fun createZipFromPaths(paths: List<String>, outputZipPath: String) {
        try {
            ZipOutputStream(FileOutputStream(outputZipPath)).use { zipOut ->
                paths.forEach { path ->
                    val file = File(path.removePrefix(".\\"))
                    if (file.exists()) {
                        // 创建zip条目，保持原有的路径结构
                        val zipEntry = ZipEntry(path.removePrefix(".\\").replace("\\", "/"))
                        zipOut.putNextEntry(zipEntry)

                        // 写入文件内容
                        FileInputStream(file).use { fileIn ->
                            fileIn.copyTo(zipOut)
                        }
                        zipOut.closeEntry()
                        println("Added file: $path")
                    } else {
                        println("Skipped file (not found): $path")
                    }
                }
            }
            println("Zip file created successfully at: $outputZipPath")
        } catch (e: Exception) {
            println("Error creating zip file: ${e.message}")
            e.printStackTrace()
        }
    }
    fun createZipFromHashMaps(pathsNameMaps: List<Map<String, String>>): Result<File> {
        return try {
            // 创建临时zip文件
            val tempZipFile = File.createTempFile("temp_", ".zip")

            // 记录跳过的文件
            val skippedFiles = mutableListOf<String>()

            // 创建ZipOutputStream
            ZipOutputStream(FileOutputStream(tempZipFile)).use { zipOut ->
                pathsNameMaps.forEach { map ->
                    map.forEach { (fileName, filePath) ->
                        try {
                            val inputFile = File(filePath)
                            if (inputFile.exists()) {
                                // 文件存在则添加到zip
                                val entry = ZipEntry(fileName)
                                zipOut.putNextEntry(entry)

                                FileInputStream(inputFile).use { fileIn ->
                                    fileIn.copyTo(zipOut)
                                }
                                zipOut.closeEntry()
                            } else {
                                // 记录跳过的文件
                                skippedFiles.add("文件名: $fileName, 路径: $filePath")
                            }
                        }catch (e: Exception){
                            skippedFiles.add("文件名: $fileName, 路径: $filePath")
                            println("Error adding file to zip: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                }
            }

            // 如果所有文件都不存在，返回错误
            if (skippedFiles.size == pathsNameMaps.sumOf { it.size }) {
                tempZipFile.delete() // 删除空的临时文件
                Result.failure(IllegalArgumentException("所有文件都不存在，无法创建ZIP文件"))
            } else {
                // 如果有跳过的文件，打印日志
                if (skippedFiles.isNotEmpty()) {
                    println("以下文件不存在已被跳过:\n${skippedFiles.joinToString("\n")}")
                }
                Result.success(tempZipFile)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}