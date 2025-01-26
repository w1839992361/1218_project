package cn.backed.teriedu.service

import cn.backed.teriedu.entity.Users
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService

interface UsersService : IService<Users> {
    fun register(user: Users): Boolean
    fun login(username: String, password: String): String?
    fun searchUsersByNicknameOrOrganization(
        keyword: String,
        page: Long,
        size: Long
    ): Page<Users>

    fun getUserInfoByUsername(username: String):Users?

    fun updateUserInfo(user: Users):Boolean

    fun managerUserInfo(keyword: String,
                        page: Long,
                        size: Long):Page<Users>
}
