package cn.backed.teriedu.mapper

import cn.backed.teriedu.entity.TeriTag
import com.baomidou.mybatisplus.core.mapper.BaseMapper

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

/**
 * @Author: Bamboo
 * @Description: Mapper interface for TeriTag
 */
@Mapper
interface TeriTagMapper : BaseMapper<TeriTag> {

    /**
     * 查询某一层级的节点
     * @param level 节点层级
     * @return List<TeriTag>
     */
    @Select("SELECT * FROM teri_tag WHERE level = #{level}")
    fun selectByLevel(@Param("level") level: Int): List<TeriTag>

    /**
     * 根据父节点查询子节点
     * @param parentId 父节点ID
     * @return List<TeriTag>
     */
    @Select("SELECT * FROM teri_tag WHERE parent_id = #{parentId}")
    fun selectByParentId(@Param("parentId") parentId: Long): List<TeriTag>

    @Select("""
        WITH RECURSIVE tag_tree AS (
            -- 初始查询
            SELECT * FROM teri_tag WHERE id = #{id}
            UNION ALL
            -- 递归查询
            SELECT t.* 
            FROM teri_tag t
            INNER JOIN tag_tree tt ON t.parent_id = tt.id
        )
        SELECT * FROM tag_tree
    """)
    fun getTreeById(@Param("id") id: Long): List<TeriTag>
}