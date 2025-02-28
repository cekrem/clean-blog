plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "io.github.cekrem"
version = "0.0.1"

application {
    mainClass.set("io.github.cekrem.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.mustache)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.caching.headers)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.intellij.markdown)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.jsoup)
    testImplementation(libs.mockk)
    testImplementation(libs.ktor.client.core)
    testImplementation(libs.ktor.client.cio)
    testImplementation(libs.ktor.client.content.negotiation)
}

tasks {
    test {
        useJUnitPlatform()
        description = "Runs all tests including unit and acceptance tests"
        group = "verification"
    }

    register<Test>("unitTest") {
        useJUnitPlatform {
            filter {
                excludeTestsMatching("*.acceptance.*")
            }
        }
        description = "Runs unit tests"
        group = "verification"
    }

    register<Test>("acceptanceTest") {
        useJUnitPlatform {
            filter {
                includeTestsMatching("*.acceptance.*")
            }
        }
        description = "Runs acceptance tests"
        group = "verification"

        mustRunAfter(test)
    }
}
