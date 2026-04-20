import org.gradle.authentication.http.BasicAuthentication

plugins {
	kotlin("jvm") version "2.3.0"
	kotlin("plugin.spring") version "2.3.0"
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
	`java-library`
	`maven-publish`
}

group = "no.novari"
version = project.findProperty("version") as String? ?: "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
	withSourcesJar()
}

repositories {
	mavenLocal()
	mavenCentral()
}

dependencies {
	api("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.14.3")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") }
}

tasks.test { useJUnitPlatform() }
tasks.bootJar { enabled = false }
tasks.jar { archiveClassifier.set("") }

publishing {
	repositories {
		maven {
			url = uri("https://repo.fintlabs.no/releases")
			credentials {
				username = System.getenv("REPOSILITE_USERNAME")
				password = System.getenv("REPOSILITE_PASSWORD")
			}
			authentication {
				create<BasicAuthentication>("basic")
			}
		}
	}
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
		}
	}
}
