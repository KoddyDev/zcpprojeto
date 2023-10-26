plugins {
    java
    kotlin("jvm") version "1.8.0"
    `maven-publish`
}

group = "me.koddydev"
version = "1.8"

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io/")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.mikael:mkUtilsLegacy:+")
    compileOnly(
        files(
            "C:\\Users\\Koddy\\Desktop\\IntelliJ Global Depends\\EduardAPI-1.0-all.jar"
        )
    )
}

tasks {
    jar {
        destinationDirectory
            .set(file("C:\\Users\\Koddy\\Desktop\\IntelliJ Global Depends\\Plugins\\"))
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
