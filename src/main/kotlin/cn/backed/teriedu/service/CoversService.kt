package cn.backed.teriedu.service

import cn.backed.teriedu.entity.Covers
import com.baomidou.mybatisplus.extension.service.IService

interface CoversService : IService<Covers>{
    fun saveCover(cover:Covers ,coverBytes: ByteArray, originalFilename: String): String
    fun getCoverById(id: String): Covers
}