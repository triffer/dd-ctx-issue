plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(23)
}

dependencies {
    api("io.opentelemetry:opentelemetry-extension-kotlin:1.60.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}
