plugins {
    kotlin("jvm") version "2.2.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.example.ApplicationKt")
}

val agent by configurations.creating

dependencies {
    implementation(platform("io.ktor:ktor-bom:3.3.1"))
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:1.5.20")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.13")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("io.github.microutils:kotlin-logging:2.0.11")
    implementation("io.opentelemetry:opentelemetry-api:1.62.0")
    implementation(project(":tracing-context"))
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.27.0")
    agent("io.opentelemetry.javaagent:opentelemetry-javaagent:2.27.0")
    agent("com.datadoghq:dd-java-agent:1.62.0")
}


distributions {
    main {
        contents {
            into("java-agents") {
                from(agent)
            }
        }
    }
}

tasks.register<JavaExec>("runWithDatadog") {
    dependsOn("installDist")
    description = "Run application with Datadog agent"

    classpath = tasks.named<JavaExec>("run").get().classpath
    mainClass = tasks.named<JavaExec>("run").get().mainClass

    val agentPath = layout.buildDirectory.dir("install/dd-ctx-issue/java-agents").get()
        .asFileTree.matching { include("dd-java-agent-*.jar") }
        .singleFile.absolutePath

    jvmArgs(
        "-javaagent:$agentPath",
        "-Ddd.trace.otel.enabled=true",
        "-Ddd.writer.type=LoggingWriter"
    )
}

tasks.register<JavaExec>("runWithOtel") {
    dependsOn("installDist")
    description = "Run application with OpenTelemetry agent"

    classpath = tasks.named<JavaExec>("run").get().classpath
    mainClass = tasks.named<JavaExec>("run").get().mainClass

    val agentPath = layout.buildDirectory.dir("install/dd-ctx-issue/java-agents").get()
        .asFileTree.matching { include("opentelemetry-javaagent-*.jar") }
        .singleFile.absolutePath

    jvmArgs(
        "-javaagent:$agentPath",
        "-Dotel.traces.exporter=logging",
        "-Dotel.metrics.exporter=none",
        "-Dotel.logs.exporter=none"
    )
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}