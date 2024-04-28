plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "io.github.bytebeats"
version = "2.0.0"

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
}

dependencies {
    implementation("com.github.promeg:tinypinyin:2.0.3")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.2.5")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("230")
        untilBuild.set("242.*")

        changeNotes.set(
            """
      v1.8.4 k-line chart of stock.<br>
      v2.0.0 giant upgrade to the project and bugfix.<br>
      """
        )
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
        println("Moving Build Artifacts!")
        from(layout.buildDirectory.dir("distributions"))
        include("mns-**.zip")
        into("plugins")
    }

    register<Delete>("DeletePluginFiles") {
        delete(files("plugins"))
    }
    named("build") {
        finalizedBy("MoveBuildArtifacts")
    }
}