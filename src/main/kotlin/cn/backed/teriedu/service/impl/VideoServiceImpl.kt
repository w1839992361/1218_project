package cn.backed.teriedu.service.impl

import cn.backed.teriedu.entity.Video
import cn.backed.teriedu.entity.enum.MediaType
import cn.backed.teriedu.entity.enum.ResourcesType
import cn.backed.teriedu.mapper.VideoMapper
import cn.backed.teriedu.service.FileResourcesService
import cn.backed.teriedu.service.TeriTagService
import cn.backed.teriedu.service.VideoService
import cn.backed.teriedu.utils.FileUploadUtils
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

/**
 * 视频Service实现类
 * 作者：Anaple@GPT4o
 */
@Service
class VideoServiceImpl : ServiceImpl<VideoMapper, Video>(), VideoService {

    @Autowired
    private lateinit var fileUploadUtils: FileUploadUtils

    @Autowired
    private lateinit var videoMapper: VideoMapper

    @Autowired
    lateinit var fileResourcesService: FileResourcesService

    @Autowired
    lateinit var teriTagService: TeriTagService

    /**
     * 保存视频信息到数据库
     * @param video 视频信息
     * @param tag 标签名称
     * @param className 年级
     * @param date 日期
     * @param videoBytes 视频文件字节流
     * @param originalFilename 视频的原始文件名
     * @return 是否保存成功
     */
    override fun saveVideo(video: Video, tag: String, className: String, date: Date, videoBytes: ByteArray, originalFilename: String, coverBytes: ByteArray,coverOriginalFilename: String,resourceUUID: String): String? {
        if (video.id.isNullOrEmpty()) {
            video.id = UUID.randomUUID().toString() // 生成UUID
        }

        // 调用工具类保存视频文件
        val relativePath = fileUploadUtils.saveVideo(tag, className, date, videoBytes, originalFilename, coverBytes,coverOriginalFilename)
        video.url = relativePath.videoPath // 设置视频的存储路径
        video.coverUrl = relativePath.coverPath // 设置封面的存储路径


        return if (save(video)){
            fileResourcesService.saveFileResources(fileName = originalFilename, filePath = video.id!!, mediaType = MediaType.VIDEO, resourceUUID = resourceUUID, resourcesType = ResourcesType.PremiumCourse)
            resourceUUID
        }else null

//        return save(video) // 保存视频信息到数据库
    }

    /**
     * 根据ID查询视频
     * @param id 视频ID
     * @return 视频信息
     */
    override fun getVideoById(id: String): Video? {
        return getById(id)
    }


    /**
     * 根据标题或说明搜索视频
     * @param titleOrDescription 标题或说明
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页搜索结果
     * @author Anaple
     */
    override fun searchVideosByTitleOrDescriptionWithPagination(
        titleOrDescription: String,
        page: Long,
        size: Long
    ): Page<Video> {
        val queryWrapper = QueryWrapper<Video>()
        queryWrapper.like("title", titleOrDescription)
            .or()
            .like("description", titleOrDescription)

        val videos = videoMapper.selectPage(Page(page, size), queryWrapper)
        for (video in videos.records) {
            video.url = fileResourcesService.queryLocalVideo(video.id!!)?.resourceUuid?.let {
                teriTagService.getIdByResourceUUID(
                    it
                ).toString()
            }
        }
        // 分页查询
        return videos
    }

}