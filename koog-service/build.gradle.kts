import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("org.flywaydb.flyway") version "11.9.1"
    id("org.graalvm.buildtools.native") version "0.10.6"
    id("jacoco")
}

group = "com.learnwords"
version = "0.0.1-SNAPSHOT"
description = "koog-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Observability wypięta (low-RAM native): tracing-brave/zipkin/logstash/prometheus usunięte.
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.slf4j:slf4j-simple") // logowanie wyłączone (no-op provider)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("ai.koog:koog-agents:0.6.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")


    testImplementation("org.springframework.boot:spring-boot-micrometer-tracing-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
    testImplementation("org.springframework.boot:spring-boot-starter-kafka-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-zipkin-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-kafka")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Logowanie wyłączone (low-RAM native): wyklucz logback z całego classpath; slf4j-nop = provider.
// Bez logbacka netty/AI nie łapią Loggera → brak kaskady image-heap w native.
configurations.all {
    exclude(group = "ch.qos.logback")
}

kotlin {
    compilerOptions {
        // Kotlin 2.2 max JVM target = 24; toolchain JDK stays 25 so GraalVM native-image
        // reports Java 25 (required by Spring Boot 4 AOT). Bytecode target 24 is forward-compatible.
        jvmTarget.set(JvmTarget.JVM_24)
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

// Keep Java bytecode target aligned with Kotlin (24) to avoid the JVM-target mismatch,
// while the toolchain JDK remains 25.
tasks.withType<JavaCompile>().configureEach {
    options.release.set(24)
}

// slf4j-simple's SimpleLogger is instantiated during Spring AOT and lands in the image heap,
// so it must be initialized at build time or native-image aborts (UnsupportedFeatureException).
graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("--initialize-at-build-time=org.slf4j.simple")
        }
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

flyway {
    url = System.getenv("FLYWAY_URL") ?: "jdbc:postgresql://localhost:5432/koog"
    user = System.getenv("FLYWAY_USER") ?: "admin"
    password = System.getenv("FLYWAY_PASSWORD") ?: ""
    schemas = arrayOf(System.getenv("FLYWAY_SCHEMAS") ?: "public")
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}
