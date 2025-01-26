package cn.backed.teriedu.service

import cn.backed.teriedu.entity.Docs
import cn.backed.teriedu.entity.Users
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService

interface DocsService : IService<Docs>{
    fun saveDocs(docs: Docs,
                 pptBytes: ByteArray, pptOriginalFilename: String,
                 leanTaskBytes: ByteArray, leanTaskOriginalFilename: String ,
                 homeworkBytes: ByteArray, homeworkOriginalFilename: String,
                 designClassBytes: ByteArray, designClassOriginalFilename: String,
                 resourceUUID: String
    ): String;
    fun getDocsById(id: String): Docs
    fun searchDocsByKeyword(
        keyword: String,
        page: Long,
        size: Long
    ): Page<Docs>
}