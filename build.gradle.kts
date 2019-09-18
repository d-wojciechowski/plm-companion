import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "pl.dominikw"
version = "0.1.2"
val protobufVersion = "3.9.1"
val grpcVersion = "1.23.0"

buildscript {
    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://dl.bintray.com/jetbrains/intellij-plugin-service")
    }
    dependencies {
        classpath("org.jetbrains.intellij.plugins:gradle-intellij-plugin:0.5.0-SNAPSHOT")
    }
}

plugins {
    id("org.jetbrains.intellij") version "0.4.10"
    id("com.google.protobuf") version "0.8.10"
    kotlin("jvm") version "1.3.50"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1")
    implementation("com.github.kittinunf.fuel", "fuel", "2.2.0")

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

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.2"
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
        """
      Add change notes here.<br>
      <em>most HTML tags may be used</em>"""
    )
}

configure<org.jetbrains.intellij.IntelliJPluginExtension> {
    version = "2019.2"
    updateSinceUntilBuild = true
    pluginName = "Windchill-Plugin"
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

protobuf {
    protoc {
        generatedFilesBaseDir = "$projectDir/src"
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
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