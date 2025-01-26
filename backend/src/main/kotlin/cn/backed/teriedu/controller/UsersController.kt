package cn.backed.teriedu.controller

import cn.backed.teriedu.annotation.AutoLog
import cn.backed.teriedu.entity.ApiResponse
import cn.backed.teriedu.entity.Users
import cn.backed.teriedu.service.UsersService
import cn.backed.teriedu.utils.JwtUtils

import org.apache.ibatis.javassist.compiler.ast.Keyword
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UsersController(@Autowired private val usersService: UsersService) {

    private  val logger = org.slf4j.LoggerFactory.getLogger(UsersController::class.java)

    @PostMapping("/register")
    fun register(@RequestBody user: Users): ApiResponse<String> {
        return if (usersService.register(user)) {
            ApiResponse(200, "Registration successful", null)
        } else {
            ApiResponse(400, "Registration failed", null)
        }
    }

    @PostMapping("/login")
    fun login(@RequestParam username: String, @RequestParam password: String): ApiResponse<String> {
        val token = usersService.login(username, password)
        return if (token != null) {
            ApiResponse(200, "Login successful", token)
        } else {
            ApiResponse(401, "Invalid username or password", null)
        }
    }
    @GetMapping("/info")
    fun getUserInfo(@RequestHeader("Authorization") token: String): ApiResponse<Users> {
        return try {
            // 1. 验证并解析token
            val username = JwtUtils.validateToken(token) ?: return ApiResponse(400,"Invalid token")

            // 2. 从数据库获取用户信息
            val user = usersService.getUserInfoByUsername(username)
                ?: return ApiResponse(400, "User not found")
            // 3. 处理敏感信息
//            user.password = ""  // 清除密码
            ApiResponse(200, "User info retrieved", user)
        } catch (e: Exception) {
            logger.error("Failed to get user info", e)
            ApiResponse(500,"Failed to get user info: ${e.message}")
        }
    }

    @PostMapping("/update")
    @AutoLog
    fun updateUserInfo(@RequestBody user: Users): ApiResponse<String> {
        return if (usersService.updateUserInfo(user)) {
            ApiResponse(200, "User info updated", null)
        } else {
            ApiResponse(400, "Failed to update user info", null)
        }
    }
    @GetMapping("/table")
    @AutoLog
    fun getUsersTable(@RequestParam keyword: String, @RequestParam page: Long, @RequestParam size: Long): ApiResponse<Any> {

        return try {
            val users = usersService.managerUserInfo(keyword, page, size)
            ApiResponse(200, "Users retrieved", users)
        } catch (e: Exception) {
            logger.error("Failed to get users", e)
            ApiResponse(500,"Failed to get users: ${e.message}")
        }
    }
}
