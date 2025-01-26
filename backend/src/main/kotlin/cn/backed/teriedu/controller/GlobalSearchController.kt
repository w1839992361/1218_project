package cn.backed.teriedu.controller

import cn.backed.teriedu.annotation.Anybody
import cn.backed.teriedu.annotation.AutoLog
import cn.backed.teriedu.entity.ApiResponse
import cn.backed.teriedu.entity.Users
import cn.backed.teriedu.entity.Video
import cn.backed.teriedu.service.DocsService
import cn.backed.teriedu.service.TeriTagService
import cn.backed.teriedu.service.UsersService
import cn.backed.teriedu.service.VideoService
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/globalSearch")
class GlobalSearchController {

    @Autowired
    private lateinit var videoService: VideoService
    @Autowired
    private lateinit var userService: UsersService
    @Autowired
    lateinit var teriTagService: TeriTagService
    @Autowired
    lateinit var docsService: DocsService

    /**
     * 根据标题或说明搜索视频（带分页）
     * @param titleOrDescription 标题或说明
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页搜索结果
     * @author Anaple
     */
    @Anybody
    @AutoLog
    @GetMapping("/searchVideo")
    fun searchVideos(
        @RequestParam titleOrDescription: String,
        @RequestParam page: Long,
        @RequestParam size: Long
    ): ApiResponse<Page<Video>> {
        val result = videoService.searchVideosByTitleOrDescriptionWithPagination(titleOrDescription, page, size)
        return ApiResponse(200, "success", result)
    }


    /**
     * 搜索用户接口
     * @param keyword 搜索关键字（昵称或机构名称）
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页搜索结果
     */
    @GetMapping("/searchUser")
    @AutoLog
    fun searchUsers(
        @RequestParam keyword: String,
        @RequestParam page: Long,
        @RequestParam size: Long
    ): ApiResponse<Page<Users>> {
        val result = userService.searchUsersByNicknameOrOrganization(keyword, page, size)
        return ApiResponse(200, "success", result)
    }
    /**
     * 搜索文档接口
     * @param keyword 搜索关键字（昵称或机构名称）
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页搜索结果
     */
    @GetMapping("/searchDocs")
    @AutoLog
    fun searchDocs(
        @RequestParam keyword: String,
        @RequestParam page: Long,
        @RequestParam size: Long
    ): ApiResponse<Page<Users>> {
        val result = userService.searchUsersByNicknameOrOrganization(keyword, page, size)
        return ApiResponse(200, "success", result)
    }
    @GetMapping("/searchTag")
    @AutoLog
    fun searchTag(
        @RequestParam keyword: String,
        @RequestParam page: Long,
        @RequestParam size: Long
    ): ApiResponse<Page<Users>> {
        val result = userService.searchUsersByNicknameOrOrganization(keyword, page, size)
        return ApiResponse(200, "success", result)
    }



}

