package cn.backed.teriedu.service.impl

import cn.backed.teriedu.entity.Log
import cn.backed.teriedu.entity.Users
import cn.backed.teriedu.mapper.UsersMapper
import cn.backed.teriedu.service.UsersService
import cn.backed.teriedu.utils.JwtUtils
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsersServiceImpl(private val usersMapper: UsersMapper) : ServiceImpl<UsersMapper, Users>(), UsersService {

    private val passwordEncoder = BCryptPasswordEncoder()

    @Autowired
    private lateinit var userMapper: UsersMapper

    override fun register(user: Users): Boolean {
        user.id = java.util.UUID.randomUUID().toString()
        user.password = passwordEncoder.encode(user.password)
        user.createTime = java.time.LocalDateTime.now()
        user.isDelete = false

        return save(user)
    }

    override fun login(username: String, password: String): String? {
        val queryWrapper = QueryWrapper<Users>()
        queryWrapper.eq("username", username).and {
            it.eq("is_delete", false)
                .eq("is_disable", false)
        }
        val user = userMapper.selectOne(queryWrapper)
        return if (user != null && passwordEncoder.matches(password, user.password)) {
            if (user.isFirstLogin!!) {
                user.isFirstLogin = false
            }
            user.lastLoginTime = java.time.LocalDateTime.now()
            userMapper.updateById(user)
            JwtUtils.generateToken(username)
        } else {
            null
        }
    }
    /**
     * 根据昵称或机构名称搜索用户（带分页）
     * @param keyword 搜索关键字
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页搜索结果
     * @author Anaple
     */

    override fun searchUsersByNicknameOrOrganization(
        keyword: String,
        page: Long,
        size: Long
    ): Page<Users> {
        val queryWrapper = QueryWrapper<Users>()
        queryWrapper.lambda()
            .like(Users::nickname, keyword)
            .or()
            .like(Users::organization, keyword)
            .and {
                it.eq(Users::isDelete, false)
                .eq(Users::isDisable, false)
                .eq(Users::isFirstLogin, false)
                .eq(Users::role, "t")
            }
        val usersPage: Page<Users> = userMapper.selectPage(Page(page, size), queryWrapper)
        for (user in usersPage.records) {
            user.id = null
            user.createTime = null
            user.lastLoginTime = null
            user.isDelete = null
            user.isDisable = null
            user.password = null
        }
        // 分页查询
        return usersPage
    }

    override fun getUserInfoByUsername(username: String): Users? {
        usersMapper.selectOne(QueryWrapper<Users>().eq("username", username).eq("is_delete", false).eq("is_disable", false))?.let {
            it.id = null
            it.createTime = null
            it.lastLoginTime = null
            it.isDelete = null
            it.isDisable = null
            it.password = null
            return it
        }
        return null
    }

    override fun updateUserInfo(user: Users): Boolean {
        usersMapper.selectOne(QueryWrapper<Users>().eq("id", user.id))?.let {
            it.nickname = user.nickname
            it.organization = user.organization
            it.avatar = user.avatar
            it.email = user.email
            it.phone = user.phone
            it.password = passwordEncoder.encode(user.password)
            it.isDelete = user.isDelete
            it.isDisable = user.isDisable
            return usersMapper.updateById(it) > 0
        }
        return false
    }

    override fun managerUserInfo(keyword: String, page: Long, size: Long): Page<Users> {
        val queryWrapper = QueryWrapper<Users>()
        // 检查是否都为空
        if (keyword.isBlank()) {
            // 如果都为空，返回所有记录
            return usersMapper.selectPage(Page(page, size), queryWrapper)
        }
        // 使用动态条件构建
        queryWrapper.apply {
            if (keyword.isNotBlank()) {
                    like("nickname", keyword)
                    .or().like("organization", keyword)
                    .or().like("email", keyword)
                    .or().like("phone", keyword)
                    .or().like("username", keyword)
            }

        }
        return usersMapper.selectPage(Page(page, size), queryWrapper)
    }
}
