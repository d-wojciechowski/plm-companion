import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "pl.dwojciechowski"
version = "0.7.0"
val protobufVersion = "3.11.4"
val rsocketRpcVersion = "0.2.18"
val rsocketVersion = "1.0.0-RC7"
val coroutinesVersion = "1.3.5"
val fuelVersion = "2.2.2"
val rxJavaVersion = "3.0.3"

plugins {
    id("com.github.ben-manes.versions") version "0.28.0"
    id("org.jetbrains.intellij") version "0.4.21"
    id("com.google.protobuf") version "0.8.12"
    kotlin("jvm") version "1.3.72"
    java
    idea
}

apply(plugin = "org.jetbrains.intellij")

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kittinunf/maven")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("com.github.kittinunf.fuel", "fuel", fuelVersion)
    implementation("io.reactivex.rxjava3:rxjava:$rxJavaVersion")

    implementation("io.rsocket:rsocket-core:$rsocketVersion")
    implementation("io.rsocket:rsocket-transport-local:$rsocketVersion")
    implementation("io.rsocket:rsocket-transport-netty:$rsocketVersion")
    implementation("io.rsocket.rpc:rsocket-rpc-core:$rsocketRpcVersion")
    //Do not use implementation here, compile is needed :
    // https://github.com/JetBrains/gradle-intellij-plugin/issues/239
    // https://github.com/JetBrains/gradle-intellij-plugin/issues/456
    compile("com.google.protobuf:protobuf-java:$protobufVersion")
}

sourceSets {
    main {
        java.srcDirs("src/main/kotlin", "src/generated/java", "src/generated/rsocketRpc")
        resources.srcDir("src/main/resources")
        proto.srcDir("src/main/proto")
    }
}

intellij {
    version = "2020.1"
    updateSinceUntilBuild = true
    pluginName = "PLM Companion"
}

tasks {

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    runIde {
        systemProperty("idea.auto.reload.plugins", false)
    }

    register("myClean", Delete::class) {
        delete("src/generated")
    }

    clean {
        dependsOn("myClean")
    }

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
            artifact = "io.rsocket.rpc:rsocket-rpc-protobuf:$rsocketRpcVersion"
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
