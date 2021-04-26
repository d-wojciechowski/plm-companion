import com.google.protobuf.gradle.*
import org.jetbrains.changelog.closure
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "pl.dwojciechowski"
version = "1.0.3"
val protobufVersion = "3.15.8"
val rSocketRpcVersion = "0.3.0"
val rSocketVersion = "1.1.0"
val coroutinesVersion = "1.4.3"
val fuelVersion = "2.3.1"
val rxJavaVersion = "3.0.12"

plugins {
    id("org.jetbrains.changelog") version "1.1.2"
    id("com.github.ben-manes.versions") version "0.38.0"
    id("org.jetbrains.intellij") version "0.7.3"
    id("com.google.protobuf") version "0.8.16"
    kotlin("jvm") version "1.4.32"
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

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("com.github.kittinunf.fuel:fuel:$fuelVersion")
    implementation("io.reactivex.rxjava3:rxjava:$rxJavaVersion")
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")

    implementation("io.rsocket:rsocket-core:$rSocketVersion") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("io.rsocket:rsocket-transport-local:$rSocketVersion") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("io.rsocket:rsocket-transport-netty:$rSocketVersion") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("io.rsocket.rpc:rsocket-rpc-core:$rSocketRpcVersion") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
}

sourceSets {
    main {
        java.srcDirs("src/main/kotlin", "src/generated/java", "src/generated/rsocketRpc")
        resources.srcDir("src/main/resources")
        proto.srcDir("src/main/proto")
    }
}

intellij {
    version = "2021.1"
    updateSinceUntilBuild = true
    pluginName = "PLM Companion"
}

tasks {

    runIde {
        systemProperty("idea.auto.reload.plugins", false)
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    clean {
        delete("src/generated")
        delete("out")
    }

    patchPluginXml {
        changeNotes(closure { changelog.getLatest().toHTML() })
        pluginDescription(htmlFixer("${project.projectDir}/src/main/resources/META-INF/description.html"))
        sinceBuild("200")
    }

}

changelog {
    version = "${project.version}"
    path = "${project.projectDir}/CHANGELOG.md"
    header = closure{"[{0}]"}
    headerParserRegex = """\d+\.\d+.\d+""".toRegex()
    itemPrefix = "-"
    unreleasedTerm = "[Unreleased]"
    groups = listOf("Added", "Changed", "Fixed")
}

idea {
    module {
        sourceDirs.add(file("src/main/kotlin"))

        generatedSourceDirs.add(file("src/generated/main/java"))
        generatedSourceDirs.add(file("src/generated/main/rsocketRpc"))
    }
}

protobuf {
    generatedFilesBaseDir = "${projectDir}/src/generated"

    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }

    plugins {
        id("rsocketRpc") {
            artifact = "io.rsocket.rpc:rsocket-rpc-protobuf:$rSocketRpcVersion"
        }

        generateProtoTasks {
            ofSourceSet("main").forEach { task ->
                task.plugins {
                    id("rsocketRpc") {}
                }
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
        return File(filename).readText().replace("(</?html>)".toRegex(), "")
    }
    return ""
}
