package cn.backed.teriedu.utils

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.security.SignatureException
import java.util.*
import javax.crypto.SecretKey

object JwtUtils {

        // JWT 过期时间，这里设置为24小时
        private const val EXPIRATION_TIME = 24 * 60 * 60 * 1000L

        // 密钥，建议使用至少256位（32字节）的密钥
        private const val SECRET = "your-secret-key-should-be-very-long-and-secure"

        // 使用 HMAC-SHA256 算法创建密钥
        private val SECRET_KEY: Key = Keys.hmacShaKeyFor(SECRET.toByteArray())

        private val logger = org.slf4j.LoggerFactory.getLogger(JwtUtils::class.java)

        /**
         * 生成JWT Token
         * @param username 用户名
         * @return Token
         */
        fun generateToken(username: String): String {
            return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date()) // 设置签发时间
                .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 明确指定签名算法
                .compact()
        }

        /**
         * 验证JWT Token并解析用户名
         * @param token Token
         * @return 用户名
         */
        fun validateToken(token: String): String? {
            return try {
                // 1. 清理token
                val cleanedToken = if (token.startsWith("Bearer ", ignoreCase = true)) {
                    token.substring(7).trim()
                } else {
                    token.trim()
                }

                logger.info("Cleaned token: $cleanedToken")

                // 2. 解析token
                val claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(cleanedToken)

                // 3. 打印解析信息
                logger.info("Token Header: ${claims.header}")
                logger.info("Token Body: ${claims.body}")
                logger.info("Subject: ${claims.body.subject}")
                logger.info("Issued At: ${Date(claims.body.issuedAt.time)}")
                logger.info("Expiration: ${Date(claims.body.expiration.time)}")

                // 4. 验证是否过期
                val now = Date()
                if (claims.body.expiration.before(now)) {
                    logger.warn("Token has expired")
                    return null
                }

                claims.body.subject
            } catch (e: ExpiredJwtException) {
                logger.error("Token expired: ${e.message}")
                null
            } catch (e: SignatureException) {
                logger.error("Invalid signature: ${e.message}")
                null
            } catch (e: MalformedJwtException) {
                logger.error("Malformed JWT: ${e.message}")
                null
            } catch (e: Exception) {
                logger.error("Token validation failed: ${e.message}")
                e.printStackTrace()
                null
            }
        }


        /**
         * 从请求头中提取Token
         * @param token 包含Bearer的完整token字符串
         * @return 提取后的token
         */
        fun extractToken(token: String): String? {
            return if (token.startsWith("Bearer ")) {
                token.substring(7)
            } else {
                null
            }
        }

}
