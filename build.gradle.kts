@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10"
    kotlin("plugin.jpa") version "1.4.10"
    kotlin("kapt") version "1.4.10"

    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

apply {
    plugin("kotlin")
}

group = "com.artemkaxboy"
version = "0.0.1-SNAPSHOT"

val local = Properties().apply {
    rootProject.file("local.properties")
        .takeIf { it.exists() }
        ?.inputStream()
        ?.use { this.load(it) }
}

val kotlinLoggingVersion by extra("1.7.10")
val swaggerVersion by extra("1.4.3")
val modelMapperVersion by extra("2.3.8")

sourceSets {
    test {
        java {
            srcDirs("src/test/kotlin", "src/test/kotlinIntegration")
        }
    }
}

repositories {
    mavenCentral()
    maven {
        name = "Github"
        url = uri("https://maven.pkg.github.com/artemkaxboy/CoreLib")

        credentials {
            username = local.getProperty("github.username") ?: System.getenv("GITHUB_ACTOR")
            password = local.getProperty("github.token") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("com.h2database:h2")

    testImplementation("io.mockk:mockk:1.10.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    // implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("com.artemkaxboy.kotlin:corelib:v0.1.1")

    // https://github.com/springdoc/springdoc-openapi
    // swagger
    implementation("org.springdoc:springdoc-openapi-webflux-ui:$swaggerVersion")

    // logging
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    // validation       https://www.baeldung.com/javax-validation
    implementation("org.hibernate.validator:hibernate-validator")
    // implementation("org.glassfish:javax.el:3.0.0")

    // entity dto mapper    https://habr.com/ru/post/438808/
    // implementation("org.modelmapper:modelmapper:$modelMapperVersion")
    // locally fixed https://github.com/modelmapper/modelmapper/issues/553
    implementation(files("libs/modelmapper-2.3.9-SNAPSHOT.jar"))

    // kapt
    kapt("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}