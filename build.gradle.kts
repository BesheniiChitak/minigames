plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

group = "me.beshenii"
version = "0.1"

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation("com.mojang:brigadier:1.0.18")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.jar {

    doFirst {
        from(configurations.runtimeClasspath.get().filter {
            it.path.contains("beshenii")
        }.map { if (it.isDirectory) it else zipTree(it) })
    }


    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.assemble {
    dependsOn("reobfJar")
}
