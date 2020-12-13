import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("org.sonarqube") version "3.0"
    jacoco
}

sonarqube {
    properties {
        property("sonar.projectKey", "welldoer_growing-object-oriented-software-guided-by-tests-with-kotlin")
        property("sonar.organization", "welldoer-github")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

group = "net.blogjava.welldoer"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/central")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/gradle-plugin")
    }

    mavenCentral()
}

dependencies {
    implementation("org.igniterealtime.smack:smack:3.1.0")
    implementation("org.igniterealtime.smack:smackx:3.1.0")

    testImplementation("com.googlecode.windowlicker:windowlicker-swing:r268")

    testImplementation("org.jmock:jmock:2.11.0")
    testImplementation("org.jmock:jmock-junit5:2.11.0")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}