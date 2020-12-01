import com.google.protobuf.gradle.*
import org.jetbrains.changelog.closure
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "pl.dwojciechowski"
version = "1.0.1"
val protobufVersion = "3.12.4"
val rSocketRpcVersion = "0.2.18"
val rSocketVersion = "1.0.0-RC7"
val coroutinesVersion = "1.4.2"
val fuelVersion = "2.3.0"
val rxJavaVersion = "3.0.7"

plugins {
    id("org.jetbrains.changelog") version "0.6.2"
    id("com.github.ben-manes.versions") version "0.36.0"
    id("org.jetbrains.intellij") version "0.6.5"
    id("com.google.protobuf") version "0.8.14"
    kotlin("jvm") version "1.4.10"
    java
    idea
}

apply(plugin = "org.jetbrains.intellij")

repositories {
    jcenter()
    maven("https://dl.bintray.com/kittinunf/maven")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("com.github.kittinunf.fuel", "fuel", fuelVersion)
    implementation("io.reactivex.rxjava3:rxjava:$rxJavaVersion")

    implementation("io.rsocket:rsocket-core:$rSocketVersion")
    implementation("io.rsocket:rsocket-transport-local:$rSocketVersion")
    implementation("io.rsocket:rsocket-transport-netty:$rSocketVersion")
    implementation("io.rsocket.rpc:rsocket-rpc-core:$rSocketRpcVersion")
    //Do not use implementation here, compile is needed :
    // https://github.com/JetBrains/gradle-intellij-plugin/issues/239
    // https://github.com/JetBrains/gradle-intellij-plugin/issues/456
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
}

sourceSets {
    main {
        java.srcDirs("src/main/kotlin", "src/generated/java", "src/generated/rsocketRpc")
        resources.srcDir("src/main/resources")
        proto.srcDir("src/main/proto")
    }
}

intellij {
    version = "2020.3"
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
    }

    patchPluginXml {
        changeNotes(closure { changelog.getLatest().toHTML() })
        pluginDescription(htmlFixer("src/main/resources/META-INF/description.html"))
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
