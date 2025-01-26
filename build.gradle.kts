plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.6"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.graalvm.buildtools.native") version "0.10.3"
}

dependencyManagement {
	imports {
		mavenBom("com.baomidou:mybatis-plus-bom:3.5.9")
	}
}


group = "cn.backed.teriedu"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

//configurations.all {
//	exclude(group = "com.fasterxml.jackson.core")
//	exclude(group = "com.fasterxml.jackson.datatype")
//	exclude(group = "com.fasterxml.jackson.module")
//	exclude(module = "spring-boot-starter-json")
//}




dependencies {

	implementation("org.springframework.boot:spring-boot-starter-jdbc")
//	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-web") {

	}

	implementation("com.alibaba.fastjson2:fastjson2-kotlin:2.0.45")
	implementation("com.alibaba.fastjson2:fastjson2-extension-spring6:2.0.45")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.baomidou:mybatis-plus-jsqlparser")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
//	implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.3'


	implementation("org.jetbrains.kotlin:kotlin-reflect")
	//implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.4")
	implementation ("com.baomidou:mybatis-plus-spring-boot3-starter:3.5.9")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:3.0.4")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
