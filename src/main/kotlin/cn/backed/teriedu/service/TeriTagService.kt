package cn.backed.teriedu.service

import cn.backed.teriedu.entity.TeriTag
import com.baomidou.mybatisplus.extension.service.IService

/**
 * @Author: Bamboo
 * @Description: Service interface for TeriTag
 */
interface TeriTagService : IService<TeriTag> {

    /**
     * 查询某一层级的节点
     * @param level 节点层级
     * @return List<TeriTag>
     */
    fun getTagsByLevel(level: Int): List<TeriTag>

    /**
     * 查询某一层级的节点及该层级子节点
     * @param level 节点层级
     * @return List<TeriTag>
     */
    fun getTagsByLevelChildren(level: Int): List<TeriTag>

    /**
     * 根据父节点查询子节点
     * @param parentId 父节点ID
     * @return List<TeriTag>
     */
    fun getTagsByParentId(parentId: Long): List<TeriTag>

    /**
     * 新建节点
     * @param tag 节点实体
     */
    fun createTag(tag: TeriTag): String?

    /**
     * 修改节点
     * @param tag 节点实体
     */
    fun updateTag(tag: TeriTag)

    /**
     * 更新 videoUuid 和 coverUuid
     * @param tagId 节点ID
     * @param coverUuid 封面UUID
     */
    fun updateMedia(tagId: Long, coverUuid: String)
    /**
     * 根据节点ID获取节点树
     * @param id 节点ID
     * @return List<TeriTag>
     */

    fun getTreeById(id: Long): List<TeriTag>;

    /**
     * 根据节点ID更新点赞 收藏 观看
     * @param id 节点ID
     * @return  bool
     */
    fun updateCount(id: Long, viewCount: Int, likeCount: Int, collectCount: Int): Boolean

    /**
     * 根据节点资源UUID获取节点ID
     * @param resourceUUID 节点ID
     * @return id
     */

    fun getIdByResourceUUID(resourceUUID: String): Long?
}
