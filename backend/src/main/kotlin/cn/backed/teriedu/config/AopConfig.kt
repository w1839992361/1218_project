import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        val javaTimeModule = JavaTimeModule()
        javaTimeModule.addSerializer(
            LocalDateTime::class.java,
            LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )

        return ObjectMapper()
            .registerModule(javaTimeModule)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}