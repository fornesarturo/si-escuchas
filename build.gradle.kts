import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.6.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.71"
	idea
	kotlin("plugin.spring") version "1.3.71"
}

group = "com"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
	runtimeClasspath {
		extendsFrom(developmentOnly)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

	// Spring Webflux
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// Mustache
	implementation("org.springframework.boot:spring-boot-starter-mustache")

	// Jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// Kotlin core dependencies
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// Kotlin coroutines dependencies
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	// Security
	//implementation("org.springframework.boot:spring-boot-starter-security")

	// Reactive MongoDB
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}

	// Test Implementations
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	// - Reactor
	testImplementation("io.projectreactor:reactor-test")
	// - Security
	//testImplementation("org.springframework.security:spring-security-test")

	// Dev implementations
	// developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
