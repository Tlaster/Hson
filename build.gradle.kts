import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    `maven-publish`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.jsoup:jsoup:1.13.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "moe.tlaster"
            artifactId = "hson"
            version = "0.1.1"

            from(components["java"])
        }
    }
}
