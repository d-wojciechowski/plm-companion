import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "pl.dwojciechowski"
version = "0.4.0"
val protobufVersion = "3.11.1"
val grpcVersion = "1.26.0"

plugins {
    id("org.jetbrains.intellij") version "0.4.15"
    id("com.google.protobuf") version "0.8.11"
    kotlin("jvm") version "1.3.61"
    java
    idea
}

apply(plugin = "org.jetbrains.intellij")

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kittinunf/maven")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("com.github.kittinunf.fuel", "fuel", "2.2.1")
    implementation("io.reactivex.rxjava3:rxjava:3.0.0-RC7")

    compile("com.google.protobuf:protobuf-java:$protobufVersion")
    compile("io.grpc:grpc-stub:$grpcVersion")
    compile("io.grpc:grpc-protobuf:$grpcVersion")
    runtime("io.grpc:grpc-netty:$grpcVersion")
    if (JavaVersion.current().isJava9Compatible) {
        compile("javax.annotation:javax.annotation-api:1.3.2")
    }
}

sourceSets {
    main {
        java.srcDir("src/main/kotlin")
        resources.srcDir("src/main/resources")
        proto.srcDir("src/main/proto")
    }
}

intellij {
    version = "2019.3"
    updateSinceUntilBuild = true
    pluginName = "PLM Companion"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

/**
 * KeyPromoterX function, visit its github: https://github.com/halirutan/IntelliJ-Key-Promoter-X
 */
fun htmlFixer(filename: String): String {
    if (!File(filename).exists()) {
        logger.error("File $filename not found.")
    } else {
        return File(filename).readText().replace("<html>", "").replace("</html>", "")
    }
    return ""
}

tasks {
    named<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
        changeNotes(htmlFixer("src/main/resources/META-INF/change-notes.html"))
        pluginDescription(htmlFixer("src/main/resources/META-INF/description.html"))
        sinceBuild("192")
    }
}
