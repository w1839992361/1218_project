package cn.backed.teriedu.utils
import cn.backed.teriedu.entity.UploadCoverAndVideoPath
import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.text.SimpleDateFormat

/**
 * 视频上传工具类
 *
 * @author Anaple@GPT4o
 */
@Component
class FileUploadUtils {
    // 从配置文件中读取存储路径
    @Value("\${upload.video.path}")
    lateinit var basePath: String

    @Value("\${upload.video.image}")
    lateinit var videoCoverPath: String

    @Value("\${upload.teri-tag.image}")
    lateinit var teriTagCoverPath: String

    @Value("\${upload.teri-tag.docs}")
    lateinit var teriTagDocsPath: String

    @Value("\${upload.avatar.image}")
    lateinit var userAvatarPath: String

    fun saveAvatar (avatarBytes: ByteArray, originalFilename: String): String {
        val fileName = UUID.randomUUID().toString() +"."+ originalFilename.substringAfterLast('.')
        val targetDirectory = File(userAvatarPath)
        // 如果文件夹不存在，则创建
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs()
        }

        val targetFile = generateUniqueFile(targetDirectory, fileName)
        targetFile.writeBytes(avatarBytes)
        return targetFile.path
    }


    fun saveTeriTagDocs (docsBytes: ByteArray, originalFilename: String,): String {
        val fileName = UUID.randomUUID().toString() +"."+ originalFilename.substringAfterLast('.')
        val targetDirectory = File(teriTagDocsPath)
        // 如果文件夹不存在，则创建
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs()
        }

        val targetFile = generateUniqueFile(targetDirectory, fileName)
        targetFile.writeBytes(docsBytes)
        return targetFile.path
    }
    fun saveTeriTagCover (coverBytes: ByteArray, originalFilename: String): String {
        val fileName = UUID.randomUUID().toString() +"."+ originalFilename.substringAfterLast('.')
        val targetDirectory = File(teriTagCoverPath)
        // 如果文件夹不存在，则创建
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs()
        }
        val targetFile = generateUniqueFile(targetDirectory, fileName)
        targetFile.writeBytes(coverBytes)
        return targetFile.path
    }





    /**
     * 按照 tag、class、date 存储视频
     * @param tag 标签名称
     * @param className 年级
     * @param date 日期
     * @param coverBytes 封面文件字节流
     * @param videoBytes 视频文件字节流
     * @param originalFilename 视频的原始文件名
     * @param coverOriginalFilename 封面的原始文件名
     * @return 存储的相对路径
     */
    fun saveVideo(tag: String, className: String, date: Date, videoBytes: ByteArray, originalFilename: String ,coverBytes: ByteArray,coverOriginalFilename: String ): UploadCoverAndVideoPath {
        // 格式化日期为 YYYY-MM-DD
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = dateFormat.format(date)

        // 构造存储路径
        val targetDirectory = File(basePath, "$className/$tag/$formattedDate")

        //构造封面存储路径
        val coverDirectory = File(videoCoverPath, "$className/$tag/$formattedDate")

        // 如果文件夹不存在，则创建
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs()
        }

        // 如果文件夹不存在，则创建
        if (!coverDirectory.exists()) {
            coverDirectory.mkdirs()
        }

        // 构造目标文件
        val targetFile = generateUniqueFile(targetDirectory, originalFilename)

        val coverFile = generateUniqueFile(coverDirectory, coverOriginalFilename)

        // 将视频字节流写入文件
        targetFile.writeBytes(videoBytes)

        coverFile.writeBytes(coverBytes)

        // 返回存储的相对路径
        return UploadCoverAndVideoPath(targetFile.path,coverFile.path)
    }

    /**
     * 检查存储路径是否有效
     */
    fun validateStoragePath(): Boolean {
        val baseDirectory = File(basePath)
        if (!baseDirectory.exists() || !baseDirectory.isDirectory) {
            return baseDirectory.mkdirs()
        }
        return true
    }

    /**
     * 检查路径是否存在重复文件，若有则生成唯一文件名
     * @param directory 存储的目标目录
     * @param originalFilename 原始文件名
     * @return 唯一文件对象
     */
    private fun generateUniqueFile(directory: File, originalFilename: String): File {
        var file = File(directory, originalFilename)
        if (!file.exists()) {
            return file
        }

        val baseName = originalFilename.substringBeforeLast('.')
        val extension = originalFilename.substringAfterLast('.', "")
        var counter = 1

        do {
            val newName = if (extension.isNotEmpty()) {
                "$baseName($counter).$extension"
            } else {
                "$baseName($counter)"
            }
            file = File(directory, newName)
            counter++
        } while (file.exists())

        return file
    }
}