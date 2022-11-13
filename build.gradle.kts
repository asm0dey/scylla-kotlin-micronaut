plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
    id("org.jetbrains.kotlin.kapt") version "1.7.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.6.3"
}

version = "0.1"
group = "com.github.asm0dey"

val kotlinVersion = project.properties["kotlinVersion"]
repositories {
    mavenCentral()
}

dependencies {
    kapt("com.scylladb:java-driver-mapper-processor:4.14.1.0")
    kapt("info.picocli:picocli:4.7.0")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut:micronaut-http-validation")
    implementation("com.scylladb:java-driver-core:4.14.1.0")
    implementation("com.scylladb:java-driver-mapper-runtime:4.14.1.0")
    implementation("info.picocli:picocli:4.7.0")
    implementation("io.micronaut.beanvalidation:micronaut-hibernate-validator")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.rxjava3:micronaut-rxjava3")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

}


application {
    mainClass.set("com.carepet.server.AppKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}
graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("kotest")
    processing {
        incremental(true)
        annotations("com.github.asm0dey.*")
    }
}



