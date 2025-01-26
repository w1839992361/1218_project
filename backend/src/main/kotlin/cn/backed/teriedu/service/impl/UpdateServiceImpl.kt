package cn.backed.teriedu.service.impl

import cn.backed.teriedu.entity.Docs
import cn.backed.teriedu.entity.UpdateData
import cn.backed.teriedu.entity.enum.MediaType
import cn.backed.teriedu.mapper.DocsMapper
import cn.backed.teriedu.mapper.TeriTagMapper
import cn.backed.teriedu.mapper.UpdateMapper
import cn.backed.teriedu.service.*
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UpdateServiceImpl:  ServiceImpl<UpdateMapper, UpdateData>(), UpdateService {
    @Autowired lateinit var updateMapper: UpdateMapper
    @Autowired lateinit var teriTagService: TeriTagService
    @Autowired lateinit var fileResourcesService: FileResourcesService
    @Autowired lateinit var videoService: VideoService

    fun isValidHttpUrl(input: String): Boolean {
        return input.startsWith("http://") || input.startsWith("https://")
    }

    override fun getAllUpdateFiles(tagId: Long): List<String> {
        // 查询最后两次的时间
        val wrapper: QueryWrapper<UpdateData> = Wrappers.query<UpdateData>()
            .orderByDesc("update_time")
            .last("LIMIT 2")
        val updateDataList: List<UpdateData> = updateMapper.selectList(wrapper)
        val dateLast = updateDataList[0].updateTime
        val dateLast2 = updateDataList[1].updateTime
        val resourcesIds = ArrayList<String>();
        val resourcePath = ArrayList<String>();

        teriTagService.getTreeById(tagId).forEach {
            tag ->
            tag.resourceUuid?.let { resourcesIds.add(it) }
        }
        resourcesIds.forEach { id->
            fileResourcesService.getByResourceUUID(id).forEach {

                    fileResources ->
                println(fileResources.id)
                if (!isValidHttpUrl(fileResources.filePath!!)) {
                    if (fileResources.mediaType == MediaType.VIDEO) {
                        fileResources.filePath?.let {
                            val video = videoService.getVideoById(it)
                            if (video != null) {
                                if (isTimeBetween(video.uploadTime!!, dateLast2!!, dateLast!!)) {
                                    resourcePath.add(video.coverUrl!!)

                                    resourcePath.add(video.url!!)
                                }
                            }
                        }
                    } else {
                        if (isTimeBetween(fileResources.createTime!!, dateLast2!!, dateLast!!)) {
                            resourcePath.add(fileResources.filePath!!)
                        } else {
                            println("${fileResources.createTime} ${fileResources.filePath}")
                        }
                    }
                }
            }

        }
        return resourcePath
    }
    /**
     * 检查 targetTime 是否在 startTime 和 endTime 之间（包含边界）
     *
     * @param targetTime 要检查的时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 如果 targetTime 在 startTime 和 endTime 之间（包含边界），返回 true；否则返回 false
     */
    fun isTimeBetween(targetTime: LocalDateTime, startTime: LocalDateTime, endTime: LocalDateTime): Boolean {
        return targetTime.isAfter(startTime)
    }
    override fun getUpdateVersion(): String {
        val updateDataList = updateMapper.selectList(null)
        return updateDataList[updateDataList.size-1].version!!
    }
    /**
     *  获取TAG下所有文件 包含子TAG
     */
    override fun getTagFiles(tagId: Long): ArrayList<HashMap<String,String>> {
        val tags = teriTagService.getTreeById(id = tagId)
        val resourcesIds = ArrayList<String>();
        val resourcePath = ArrayList<String>();
        val fileNames = ArrayList<String>();
        val filePath = ArrayList<HashMap<String,String>>();

        tags.forEach { tag ->
            tag.resourceUuid?.let { resourcesIds.add(it) }
        }
        resourcesIds.forEach { id->
            fileResourcesService.getByResourceUUID(id).forEach { fileResources ->
                println(fileResources.id)
                if (!isValidHttpUrl(fileResources.filePath!!)) {
                    if (fileResources.mediaType == MediaType.VIDEO) {

                        fileResources.filePath?.let {
                            val video = videoService.getVideoById(it)
                            if (video != null) {
//                      resourcePath.add(video.coverUrl!!)
                                resourcePath.add(video.url!!)
                                fileNames.add(fileResources.fileName!!)
                            }
                        }
                    } else {
                        resourcePath.add(fileResources.filePath!!)
                        fileNames.add(fileResources.fileName!!)
                    }
                }
            }
        }
        for(i in 0 until fileNames.size){
            filePath.add(HashMap<String,String>().apply {
//          put("fileName",fileIds[i])
                put(fileNames[i],resourcePath[i])
            })
        }
        return filePath
    }
}