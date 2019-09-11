import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    kotlin("jvm") version "1.3.50"
}

apply(plugin = "org.jetbrains.intellij")

group = "pl.dominikw"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1")
}

sourceSets {
    main {
        java.srcDir("src/main/kotlin")
        resources.srcDir("src/main/resources")
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.2.2"
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
}

configure<org.jetbrains.intellij.IntelliJPluginExtension> {
    version = "LATEST-EAP-SNAPSHOT"
    updateSinceUntilBuild = true
    pluginName = "Windchill-Plugin"
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}