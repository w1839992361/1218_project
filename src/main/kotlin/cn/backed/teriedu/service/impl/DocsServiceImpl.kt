package cn.backed.teriedu.service.impl

import cn.backed.teriedu.entity.Docs
import cn.backed.teriedu.entity.Users
import cn.backed.teriedu.entity.enum.MediaType
import cn.backed.teriedu.entity.enum.ResourcesType
import cn.backed.teriedu.mapper.DocsMapper
import cn.backed.teriedu.service.DocsService
import cn.backed.teriedu.utils.FileUploadUtils
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class DocsServiceImpl : ServiceImpl<DocsMapper, Docs>(), DocsService {
    @Autowired
    lateinit var fileUploadUtils: FileUploadUtils

    @Autowired
    lateinit var docsMapper: DocsMapper

    @Autowired
    lateinit var fileResourcesServiceImpl: FileResourcesServiceImpl
    override fun saveDocs(
        docs: Docs,
        pptBytes: ByteArray,
        pptOriginalFilename: String,
        leanTaskBytes: ByteArray,
        leanTaskOriginalFilename: String,
        homeworkBytes: ByteArray,
        homeworkOriginalFilename: String,
        designClassBytes: ByteArray,
        designClassOriginalFilename: String,
        resourceUUID: String
    ): String {
        if (docs.id.isNullOrEmpty()) {
            docs.id = UUID.randomUUID().toString() // 生成UUID
        }
        val pptPath = fileUploadUtils.saveTeriTagDocs(pptBytes, pptOriginalFilename)
        val leanTaskPath = fileUploadUtils.saveTeriTagDocs(leanTaskBytes, leanTaskOriginalFilename)
        val homeworkPath = fileUploadUtils.saveTeriTagDocs(homeworkBytes, homeworkOriginalFilename)
        val designClassPath = fileUploadUtils.saveTeriTagDocs(designClassBytes, designClassOriginalFilename)


        fileResourcesServiceImpl.saveFileResources(pptOriginalFilename, pptPath, MediaType.Docs, resourceUUID, ResourcesType.PPTX)
        fileResourcesServiceImpl.saveFileResources(leanTaskOriginalFilename, leanTaskPath, MediaType.Docs, resourceUUID, ResourcesType.LearnGuide)
        fileResourcesServiceImpl.saveFileResources(homeworkOriginalFilename, homeworkPath, MediaType.Docs, resourceUUID, ResourcesType.Homework)
        fileResourcesServiceImpl.saveFileResources(designClassOriginalFilename, designClassPath, MediaType.Docs, resourceUUID, ResourcesType.ClassDesign)

        return docs.apply {
            pptUrl = pptPath
            leanTaskUrl = leanTaskPath
            homeworkUrl = homeworkPath
            designClassUrl = designClassPath
        }.let {
            save(it)
            it.id!!
        }
    }

    override fun getDocsById(id: String): Docs {
        return getById(id)
    }

    override fun searchDocsByKeyword(keyword: String, page: Long, size: Long): Page<Docs> {
        val queryWrapper = QueryWrapper<Docs>()
        queryWrapper.lambda()
            .like(Docs::title, keyword)
        // 分页查询
        return docsMapper.selectPage(Page(page, size), queryWrapper)
    }
}