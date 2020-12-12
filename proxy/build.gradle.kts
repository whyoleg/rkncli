plugins {
    kotlin("multiplatform")
}

val ktorVersion: String by rootProject
val rSocketVersion: String by rootProject

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.rsocket.kotlin:rsocket-core:$rSocketVersion")
                implementation("io.rsocket.kotlin:rsocket-transport-ktor:$rSocketVersion")
                implementation("io.rsocket.kotlin:rsocket-transport-ktor-client:$rSocketVersion")
                implementation("io.rsocket.kotlin:rsocket-transport-ktor-server:$rSocketVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-server-cio:$ktorVersion")
            }
        }
    }
}
