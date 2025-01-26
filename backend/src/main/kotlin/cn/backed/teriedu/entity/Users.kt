package cn.backed.teriedu.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@TableName("users")
data class Users (
    var id: String? = null, //uuid


    @TableField(value = "username")
    @get:JvmName("get_username")
    var username: String? = null,  // 改名为_username

    @get:JvmName("get_password")
    @TableField(value = "password")
    var password: String? = null,

    var role: String? = null, //角色 t 教师 s学生 a管理员 c家长 前端
    var avatar: String? = null, //头像  前端 调用 /api/avatar/upload 上传后获取地址
    var nickname: String? = null, //昵称 前端
    var email: String? = null, //邮箱 前端
    var phone: String? = null, //电话 前端

    var createTime: LocalDateTime? = null, //创建时间
    var organization : String? = null, //机构 前端
    var isDelete: Boolean? = false,  //是否删除
    var isDisable: Boolean? = false,  //是否禁用
    var isFirstLogin: Boolean? = true, //是否首次登录

    var lastLoginTime: LocalDateTime? = null // 上次登录时间
): UserDetails {
    // 提供username的getter和setter
//    var username: String?
//        get() = _username
//        set(value) {
//            _username = value
//        }
//    var password: String?
//        get() = _password
//        set(value) {
//            _password = value
//        }


    override fun getAuthorities(): Collection<GrantedAuthority> {
        // 将角色转换为Spring Security的权限
        val authorities = mutableListOf<GrantedAuthority>()

        // 添加基础角色
        role?.let {
            authorities.add(SimpleGrantedAuthority("ROLE_${it.uppercase()}"))

            // 根据角色添加具体权限
            when (it.lowercase()) {
                "a" -> {
                    authorities.add(SimpleGrantedAuthority("ADMIN_ACCESS"))
                    authorities.add(SimpleGrantedAuthority("TEACHER_ACCESS"))
                    authorities.add(SimpleGrantedAuthority("STUDENT_ACCESS"))
                    authorities.add(SimpleGrantedAuthority("PARENT_ACCESS"))
                    authorities.add(SimpleGrantedAuthority("USER_ACCESS"))
                }
                "t" -> {
                    authorities.add(SimpleGrantedAuthority("TEACHER_ACCESS"))
                    authorities.add(SimpleGrantedAuthority("PARENT_ACCESS"))
                    authorities.add(SimpleGrantedAuthority("STUDENT_ACCESS"))
                    authorities.add(SimpleGrantedAuthority("USER_ACCESS"))
                }
                "s" -> {
                    authorities.add(SimpleGrantedAuthority("STUDENT_ACCESS"))
                    authorities.add(SimpleGrantedAuthority("USER_ACCESS"))
                }
                "c" -> {
                    authorities.add(SimpleGrantedAuthority("PARENT_ACCESS"))
                    authorities.add(SimpleGrantedAuthority("STUDENT_ACCESS"))
                    authorities.add(SimpleGrantedAuthority("USER_ACCESS"))
                }

                else -> {
                    authorities.add(SimpleGrantedAuthority("USER_ACCESS"))

                }
            }
        }

        return authorities
    }

    override fun getPassword(): String {
        return password ?: ""
    }

    override fun getUsername(): String {
        return username ?: ""
    }

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = !(isDisable ?: false)

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = !(isDelete ?: false)
}
