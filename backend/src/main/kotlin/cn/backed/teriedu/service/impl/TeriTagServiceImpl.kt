package cn.backed.teriedu.service.impl

import cn.backed.teriedu.entity.TeriTag
import cn.backed.teriedu.mapper.TeriTagMapper
import cn.backed.teriedu.service.TeriTagService
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl

import org.springframework.stereotype.Service
import java.util.UUID

/**
 * @Author: Bamboo
 * @Description: Implementation of TeriTagService
 */
@Service
class TeriTagServiceImpl(
    private val teriTagMapper: TeriTagMapper
) : ServiceImpl<TeriTagMapper, TeriTag>(), TeriTagService {
    override fun getTagsByLevelChildren(level: Int): List<TeriTag> {
        val queryWrapper = QueryWrapper<TeriTag>()
        queryWrapper
            .ge("level", level)
            .or()
            .eq("level", level)

        return  teriTagMapper.selectList(queryWrapper);
    }

    override fun getTagsByLevel(level: Int): List<TeriTag> {
        val queryWrapper = QueryWrapper<TeriTag>()
        queryWrapper
            .eq("level", level)
//            .or()
//            .eq("level", level)

        return  teriTagMapper.selectList(queryWrapper);
    }

    override fun getTagsByParentId(parentId: Long): List<TeriTag> {
        return teriTagMapper.selectByParentId(parentId)
    }

    override fun createTag(tag: TeriTag):String? {
        if (tag.parentId != null) {
            val queryWrapper = QueryWrapper<TeriTag>()
            queryWrapper
                .eq("parent_id", tag.parentId)
            val log = teriTagMapper.selectCount(queryWrapper)
            tag.sequence = log.toInt()
        }
        tag.resourceUuid = UUID.randomUUID().toString()
        this.save(tag)
        return tag.resourceUuid

    }

    override fun updateTag(tag: TeriTag) {
        this.updateById(tag)
    }

    override fun updateMedia(tagId: Long,  coverUuid: String) {
        val tag = this.getById(tagId)
        if (tag != null) {
            tag.coverUuid = coverUuid

            this.updateById(tag)
        }
    }

    override fun getTreeById(id: Long): List<TeriTag> {
        return teriTagMapper.getTreeById(id);
    }

    override fun updateCount(id: Long, viewCount: Int, likeCount: Int, collectCount: Int): Boolean {
        val queryWrapper = QueryWrapper<TeriTag>()
        queryWrapper
            .eq("id", id)
        val teriTag = teriTagMapper.selectOne(queryWrapper)
        if (teriTag != null) {
            teriTag.viewNum = viewCount.toLong()
            teriTag.favoriteNum = likeCount.toLong()
            teriTag.subNum = collectCount.toLong()
            return teriTagMapper.updateById(teriTag) > 0
        }
        return false
    }

    override fun getIdByResourceUUID(resourceUUID: String): Long? {
        val queryWrapper = QueryWrapper<TeriTag>()
        queryWrapper
            .eq("resource_uuid", resourceUUID)
        return teriTagMapper.selectOne(queryWrapper)?.id
    }

}
