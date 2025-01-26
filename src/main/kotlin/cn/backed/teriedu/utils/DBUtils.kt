package cn.backed.teriedu.utils
import org.springframework.core.io.ClassPathResource
import org.yaml.snakeyaml.Yaml
import java.io.BufferedWriter
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStreamReader
import java.sql.DriverManager
import java.sql.Types

object DBUtils {


    fun exportDatabaseToSql(outputFilePath: String) {
        try {
            // 读取yaml配置文件
//            val dbConfig = loadDatabaseConfig()

            // 构建JDBC URL
//            val jdbcUrl = buildJdbcUrl(dbConfig)
            val jdbcUrl = "jdbc:mysql://109.176.255.210:9929/teriedu?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC"

            // 建立数据库连接
            val connection = DriverManager.getConnection(
                jdbcUrl,
                "test1",
                "passwd@1"
            )

            val metaData = connection.metaData

            // 创建输出文件
            val writer = FileWriter(outputFilePath)
            val buffer = BufferedWriter(writer)

            buffer.write("-- Create database if not exists\n")
            buffer.write("CREATE DATABASE IF NOT EXISTS `teriedu`;\n")
            buffer.write("USE `teriedu`;\n\n")

            // 获取所有表名
            val tables = mutableListOf<String>()
            val rs = metaData.getTables("teriedu", null, "%", arrayOf("TABLE"))
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"))
            }

            // 遍历每个表
            tables.forEach { tableName ->
                // 添加表结构
                buffer.write("-- Table structure for $tableName\n")
                buffer.write("DROP TABLE IF EXISTS `$tableName`;\n")

                // 获取建表语句
                val statement = connection.createStatement()
                val showCreateTableRs = statement.executeQuery("SHOW CREATE TABLE $tableName")
                if (showCreateTableRs.next()) {
                    buffer.write("${showCreateTableRs.getString(2)};\n\n")
                }

                // 获取表数据
                buffer.write("-- Data for table $tableName\n")
                val dataRs = statement.executeQuery("SELECT * FROM $tableName")
                val columnCount = dataRs.metaData.columnCount

                // 遍历数据行
                while (dataRs.next()) {
                    val values = mutableListOf<String>()
                    for (i in 1..columnCount) {
                        val value = dataRs.getString(i)
                        values.add(when {
                            value == null -> "NULL"
                            dataRs.metaData.getColumnType(i) in listOf(
                                Types.VARCHAR, Types.CHAR, Types.TIMESTAMP, Types.DATE,
                                Types.LONGVARCHAR, Types.CLOB, Types.NVARCHAR, Types.NCHAR
                            ) -> "'${escapeString(value)}'"
                            else -> value
                        })
                    }

                    buffer.write("INSERT INTO `$tableName` VALUES (${values.joinToString(",")});\n")
                }

                buffer.write("\n")

                statement.close()
            }

            // 关闭资源
            buffer.close()
            writer.close()
            connection.close()

        } catch (e: Exception) {
            throw RuntimeException("Database export failed: ${e.message}", e)
        }
    }
    // 转义字符串
    private fun escapeString(str: String): String {
        return str.replace("\\", "\\\\")  // 把 \ 替换成 \\
            .replace("'", "\\'")     // 把 ' 替换成 \'
            .replace("\r", "\\r")    // 处理回车
            .replace("\n", "\\n")    // 处理换行
            .replace("\t", "\\t")    // 处理制表符
    }


    // 加载yaml配置文件
    private fun loadDatabaseConfig(): Map<String, Any> {
        val yaml = Yaml()
        val resource = ClassPathResource("application.yml")
        val inputStream = resource.inputStream
        val reader = InputStreamReader(inputStream)

        @Suppress("UNCHECKED_CAST")
        val config = yaml.load(reader) as Map<String, Any>
        val spring = config["spring"] as Map<String, Any>
        val datasource = spring["datasource"] as Map<String, Any>

        return datasource
    }

    // 构建JDBC URL
    private fun buildJdbcUrl(dbConfig: Map<String, Any>): String {
        val url = dbConfig["url"] as String
        // 如果URL中已经包含了完整的JDBC URL，直接返回
        if (url.startsWith("jdbc:")) {
            return url
        }

        // 否则构建JDBC URL
        val host = dbConfig["host"] as? String ?: "localhost"
        val port = dbConfig["port"] as? String ?: "3306"
        val database = dbConfig["database"] as String

        return "jdbc:mysql://$host:$port/$database?useSSL=false&serverTimezone=UTC"
    }
}