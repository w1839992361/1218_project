package cn.backed.teriedu.service.impl

import cn.backed.teriedu.entity.enum.MediaType
import cn.backed.teriedu.entity.enum.ResourcesType
import java.time.LocalDateTime
import java.util.UUID

// FileResourcesServiceImpl.kt

import cn.backed.teriedu.entity.FileResources
import cn.backed.teriedu.mapper.FileResourcesMapper
import cn.backed.teriedu.service.FileResourcesService
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FileResourcesServiceImpl : ServiceImpl<FileResourcesMapper, FileResources>(), FileResourcesService {

    @Autowired
    lateinit var fileResourcesMapper: FileResourcesMapper

    // 可以在这里添加自定义的业务方法
     override fun saveFileResources(fileName: String, filePath: String, mediaType: MediaType,resourceUUID: String,resourcesType: ResourcesType): Boolean {
        val code = countSimilarFiles(resourceUUID, resourcesType, mediaType)+1
        val fileCode =  if(code < 10){
            "000$code"
        } else if(code in 10..99){
            "00$code"
        }else if(code in 100 .. 999 ){
            "0$code"
        } else {
            "$code"
        }
        val mediaCode = mediaType.getCode();
        val resourcesTypeCode = resourcesType.getCode()
        val fullResourceCode = "$"+"0$resourcesTypeCode"+"0$mediaCode$fileCode"
        val fileResources = FileResources(
            id = UUID.randomUUID().toString(),
            fileCode = fileCode,
            fileName = fileName,
            filePath = filePath,
            fullResourceCode = fullResourceCode,
            mediaType = mediaType,
            resourceUuid = resourceUUID,
            resourceType = resourcesType,
            createTime = LocalDateTime.now(),
            updateTime = LocalDateTime.now()
        )
        return save(fileResources)
    }

    override fun getResourcesByResourceUUIDAndMediaTypeAndResourceType(resourceUUID: String ,mediaType: MediaType,resourceType: ResourcesType): List<FileResources> {
        val queryWrapper = QueryWrapper<FileResources>()

        queryWrapper.eq("resource_uuid", resourceUUID)
            .eq("resource_type", resourceType)
            .eq("media_type", mediaType)
        return fileResourcesMapper.selectList(queryWrapper)
    }
    // 例如：根据资源UUID查询文件
    override fun getByResourceUUID(resourceUUID: String):List<FileResources> {
        val queryWrapper = QueryWrapper<FileResources>()

        queryWrapper.eq("resource_uuid", resourceUUID)

        return fileResourcesMapper.selectList(queryWrapper)
    }

    override fun getByResourceUUID(resourceUUID: String , mediaType: MediaType):List<FileResources> {
        val queryWrapper = QueryWrapper<FileResources>()

        queryWrapper.eq("resource_uuid", resourceUUID)
            .eq("media_type", mediaType)
        return fileResourcesMapper.selectList(queryWrapper)
    }

    override fun getByID(id: String): FileResources {
        val queryWrapper = QueryWrapper<FileResources>()
        return fileResourcesMapper.selectOne(queryWrapper.eq("id", id))
    }

    // 根据文件编号查询
    fun getByFileCode(fileCode: String): FileResources? {
        return lambdaQuery()
            .eq(FileResources::fileCode, fileCode)
            .one()
    }

    // 根据完整编号查询
    fun getByFullResourceCode(fullResourceCode: String): FileResources? {
        return lambdaQuery()
            .eq(FileResources::fullResourceCode, fullResourceCode)
            .one()
    }

    fun countSimilarFiles(resourceUUID: String, resourcesType: ResourcesType, mediaType: MediaType): Long {
        val queryWrapper = QueryWrapper<FileResources>()

        queryWrapper.eq("resource_uuid", resourceUUID)
            .eq("resource_type", resourcesType)
            .eq("media_type", mediaType)


        return fileResourcesMapper.selectCount(queryWrapper)


    }

    override fun queryLocalVideo(id: String): FileResources? {
        val queryWrapper = QueryWrapper<FileResources>()
        queryWrapper.eq("file_path", id)
        return fileResourcesMapper.selectOne(queryWrapper)
    }
}