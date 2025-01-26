package cn.backed.teriedu

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["cn.backed.teriedu"])
class TeriEduApplication

fun main(args: Array<String>) {
	runApplication<TeriEduApplication>(*args)
}
