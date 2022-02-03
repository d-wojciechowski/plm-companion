import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "pl.dwojciechowski"
version = "1.0.6"
val protobufVersion = "3.17.3"
val rSocketRpcVersion = "0.3.0"
val rSocketVersion = "1.1.1"
val coroutinesVersion = "1.5.1-native-mt"
val fuelVersion = "2.3.1"
val rxJavaVersion = "3.0.13"

plugins {
    id("org.jetbrains.changelog") version "1.3.1"
    id("com.github.ben-manes.versions") version "0.41.0"
    id("org.jetbrains.intellij") version "1.3.1"
    id("com.google.protobuf") version "0.8.16"
    kotlin("jvm") version "1.4.32"
    java
    idea
}

apply(plugin = "org.jetbrains.intellij")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.21")

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
    version.set("2021.3.2")
    updateSinceUntilBuild.set(true)
    pluginName.set("PLM Companion")
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
        changeNotes.set(provider{ changelog.getLatest().toHTML() })
        pluginDescription.set(htmlFixer("${project.projectDir}/src/main/resources/META-INF/description.html"))
        sinceBuild.set("210")
    }

}

changelog {
    version.set("${project.version}")
    path.set("${project.projectDir}/CHANGELOG.md")
    header.set(provider{"[{0}]"})
    headerParserRegex.set("""\d+\.\d+.\d+""".toRegex())
    itemPrefix.set("-")
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Fixed"))
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
