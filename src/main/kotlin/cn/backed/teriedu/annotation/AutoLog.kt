package cn.backed.teriedu.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoLog(
    val description: String = ""
)