package cn.backed.teriedu.service.impl

import cn.backed.teriedu.entity.Covers
import cn.backed.teriedu.mapper.CoversMapper
import cn.backed.teriedu.service.CoversService
import cn.backed.teriedu.utils.FileUploadUtils
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class CoversServiceImpl : ServiceImpl<CoversMapper, Covers>(), CoversService {

    @Autowired
    lateinit var fileUploadUtils: FileUploadUtils
    override fun saveCover(cover: Covers, coverBytes: ByteArray, originalFilename: String): String {
        if (cover.id.isNullOrEmpty()) {
            cover.id = UUID.randomUUID().toString() // 生成UUID
        }
        var coverUrl = fileUploadUtils.saveTeriTagCover(coverBytes, originalFilename);
        cover.coverUrl = coverUrl
        cover.createTime = LocalDateTime.now()
        cover.updateTime = LocalDateTime.now()
        save(cover)
        return cover.id!!
    }

    override fun getCoverById(id: String): Covers {
        val cover = getById(id)
//        if (cover != null) {
//
//
//        }
        return cover

    }

}