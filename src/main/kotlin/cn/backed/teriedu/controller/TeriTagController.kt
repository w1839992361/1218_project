package cn.backed.teriedu.controller

import cn.backed.teriedu.annotation.Anybody
import cn.backed.teriedu.annotation.AutoLog
import cn.backed.teriedu.entity.*
import cn.backed.teriedu.service.TeriTagService
import cn.backed.teriedu.service.UpdateService
import cn.backed.teriedu.utils.OutZipUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @Author: Bamboo
 * @Description: Controller for TeriTag
 */
@RestController
@RequestMapping("/api/teriTag")
class TeriTagController(@Autowired private val teriTagService: TeriTagService) {

    @Autowired
    lateinit var updateService: UpdateService

    /**
     * 查询某一层级的节点
     * @param level 节点层级
     * @return List<TeriTag>
     */
    @Anybody
    @GetMapping("/level/{level}")
    fun getTagsByLevel(@PathVariable level: Int): ApiResponse<List<TeriTag>> {
        return ApiResponse(200,"success",teriTagService.getTagsByLevel(level))
    }
    /**
     * 删除节点
     */

    @Anybody
    @AutoLog
    @DeleteMapping("/delete/{id}")
    fun deleteTag(@PathVariable id :Int):ApiResponse<Any>{
        return ApiResponse(200,"success",teriTagService.removeById(id));

    }


    /**
     * 根据父节点查询子节点
     * @param parentId 父节点ID
     * @return List<TeriTag>
     */
    @Anybody
    @GetMapping("/parent/{parentId}")
    fun getTagsByParentId(@PathVariable parentId: Long):ApiResponse<List<TeriTag>> {
        return ApiResponse(200,"success",teriTagService.getTagsByParentId(parentId))

    }

    /**
     * 新建节点
     * @param tag 节点实体
     */
    @Anybody
    @AutoLog
    @PostMapping
    fun createTag(@RequestBody tag: TeriTag):ApiResponse<Any> {
        val res  = teriTagService.createTag(tag)
        return ApiResponse(200,"success",res)
    }

//    /**
//     * 修改节点
//     * @param tag 节点实体
//     */
//    @PostMapping
//    fun updateTag(@RequestBody tag: TeriTag):ApiResponse<Any> {
//        teriTagService.updateTag(tag)
//        return ApiResponse(200,"success")
//    }

    /**
     * 根据节点ID查询节点
     * @param id
     * @return TeriTag
     */

    @Anybody
    @AutoLog
    @GetMapping("/info/{id}")
    fun getTagById(@PathVariable id: Long):ApiResponse<TeriTag> {
        return ApiResponse(200,"success",teriTagService.getById(id))
    }

    /**
     * 更新 videoUuid 和 coverUuid
     * @param tagId 节点ID
     * @param videoUuid 视频UUID
     * @param coverUuid 封面UUID
     */
    @Anybody
    @AutoLog
    @PostMapping("/update/{tagId}")
    fun updateMedia(
        @PathVariable tagId: Long,
        @RequestBody body: UpdateTagVideoAndCoverBody,
    ):ApiResponse<Any> {
        teriTagService.updateMedia(tagId,body.coverUuid)
        //body.videoPath?.let { body.coverPath?.let { it1 -> teriTagService.updateMedia(tagId, it, it1) } }
        return ApiResponse(200,"success")
    }

    /**
     * 按照树状关系返回所有节点
     * @return List<TeriTag>
     */
    @Anybody
    @AutoLog
    @GetMapping("/tree")
    fun getAllTagsAsTree():ApiResponse<List<TeriTag>> {
        return ApiResponse(200,"success",buildTree(teriTagService.list()))
    }
    /**
     * 按照树状关系返回所有节点 使用level查询小于等于该level的
     * @return List<TeriTag>
     */
    @Anybody
    @AutoLog
    @GetMapping("/tree/{level}")
    fun getAllTagsAsTree(@PathVariable level: Int):ApiResponse<List<TeriTag>> {
        val tags = teriTagService.getTagsByLevelChildren(level)

        return ApiResponse(200,"success",buildTree(tags))
    }
    /**
     * 按照树状关系返回节点 通过id检索
     * @return List<TeriTag>
     */
    @Anybody
    @AutoLog
    @GetMapping("/tree/id/{tagId}")
    fun getAllTagsAsTree(@PathVariable tagId: Long):ApiResponse<List<TeriTag>> {
        val tags = teriTagService.getTreeById(tagId)

        return ApiResponse(200,"success",buildTree(tags))
    }

    @Anybody
    @AutoLog
    @PostMapping("/updateStatistics")
    fun getTagsByLevelChildren(@RequestBody body: UpdateStatistics): ApiResponse<Any> {
        return if (teriTagService.updateCount(body.id!!,body.viewNum!!,body.subNum!!,body.favoriteNum!!)){
            ApiResponse(200, "success")
        }else{
            ApiResponse(400,"error")
        }

    }

    @Anybody
    @AutoLog
    @GetMapping("/getTagZip/{id}")
    fun getTagZip(@PathVariable id: Long): ResponseEntity<out ByteArray> {

        val tagsMap = updateService.getTagFiles(id);
        val file = OutZipUtils.createZipFromHashMaps(tagsMap)
        if (!file.isSuccess) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_OCTET_STREAM
            contentDisposition = ContentDisposition
                .builder("attachment")
                .filename("files_$id.zip", StandardCharsets.UTF_8)
                .build()
        }
        FileInputStream(file.getOrThrow()).use { inputStream ->
            return ResponseEntity(inputStream.readBytes(), headers, HttpStatus.OK)
        }

    }
    @Anybody
    @GetMapping("/deleteTagResource/{id}")
    fun deleteTagResource(@PathVariable id: Long): ApiResponse<Any> {
        return try {
            val fileMaps: List<HashMap<String, String>> = updateService.getTagFiles(id)
            for (fileMap in fileMaps) {
                for (file in fileMap.values) {
                    val path = Paths.get(file)
                    if (Files.exists(path)) {
                        Files.delete(path)
                    }
                }
            }
            ApiResponse(200, "success")
        } catch (e: Exception) {
            ApiResponse(400, "error")
        }
    }

    private fun buildTree(tags: List<TeriTag>): List<TeriTag> {
        val tagMap = tags.associateBy { it.id }
        val tree = mutableListOf<TeriTag>()

        tags.forEach { tag ->
            if (tag.parentId == null) {
                tree.add(tag)
            } else if (tagMap[tag.parentId] == null) {
                // 如果父节点不存在，则将当前节点添加到根节点列表中
                tree.add(tag)
            }
            else {
                val parent = tagMap[tag.parentId]
                if (parent != null) {
                    if (parent.children == null) {
                        parent.children = mutableListOf()
                    }
                    parent.children!!.add(tag)
                }
            }
        }
        return tree
    }
}
