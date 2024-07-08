import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED

plugins {
  id("org.springframework.boot") version "3.3.1" apply false
  id("io.spring.dependency-management") version "1.1.6"
  id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
  id("org.jetbrains.kotlin.jvm") version "2.0.0"
  id("org.jetbrains.kotlin.plugin.spring") version "2.0.0"
  id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "com.rogervinas"
version = "0.0.1-SNAPSHOT"

val springCloudVersion = "2023.0.2"

allprojects {
  repositories {
    mavenCentral()
    maven { url = uri("https://packages.confluent.io/maven") }
  }
}

subprojects {
  apply(plugin = "org.springframework.boot")
  apply(plugin = "io.spring.dependency-management")
  apply(plugin = "com.github.davidmc24.gradle.plugin.avro")
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jetbrains.kotlin.plugin.spring")

  dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")

    implementation("io.confluent:kafka-avro-serializer:7.6.1")
    implementation("org.apache.avro:avro:1.11.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:1.19.8")
  }

  dependencyManagement {
    imports {
      mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
  }

  java {
    toolchain {
      languageVersion = JavaLanguageVersion.of(21)
    }
  }

  kotlin {
    compilerOptions {
      freeCompilerArgs.addAll("-Xjsr305=strict")
    }
  }

  tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
      events(PASSED, SKIPPED, FAILED)
      exceptionFormat = FULL
      showExceptions = true
      showCauses = true
      showStackTraces = true
    }
  }
}
