import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "io.github.bytebeats"
version = "2.2.0"

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/google")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/central")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/jcenter")
    }
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    implementation("com.github.promeg:tinypinyin:2.0.3")
    intellijPlatform {
        create("IC", "2024.2")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "242"
            untilBuild = "251.*"
        }

        changeNotes = """
      v1.8.4 k-line chart of stock.<br>
      v2.0.0 giant upgrade to the project and bugfix.<br>
      v2.1.0 upgrade mns with Java 21 and Idea 2025.1.<br>
    """.trimIndent()
    }

    pluginVerification {
        ides {
            ide(IntelliJPlatformType.IntellijIdeaCommunity, "2024.3.6")
            local(file(project.localProperty("plugin.verifier.ide.location")))
            recommended()
            select {
                types = listOf(IntelliJPlatformType.IntellijIdeaCommunity, IntelliJPlatformType.AndroidStudio)
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = "242"
                untilBuild = "251.*"
            }
        }
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_21.toString()
        targetCompatibility = JavaVersion.VERSION_21.toString()
    }
    withType<KotlinCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    register<Copy>("MoveBuildArtifacts") {
        mustRunAfter("DeletePluginFiles")
        from(layout.buildDirectory.dir("distributions"))
        include("mns-$version.zip")
        into("plugins")
    }

    register<Delete>("DeletePluginFiles") {
        delete(files("plugins"))
    }
    named("signPlugin") {
        finalizedBy("MoveBuildArtifacts")
    }
}

fun Project.localProperty(key: String, from: String = "local.properties"): String {
    val properties = Properties()
    properties.load(FileInputStream(rootProject.file(from)))
    return properties.getProperty(key)
}