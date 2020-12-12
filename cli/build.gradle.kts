import org.jetbrains.kotlin.konan.target.*

plugins {
    kotlin("multiplatform")
}

val kotlinxCliVersion: String by rootProject
val rSocketVersion: String by rootProject

kotlin {
    when {
        HostManager.hostIsLinux -> linuxX64("native")
        HostManager.hostIsMac   -> macosX64("native")
        else                    -> error("not supported")
    }.binaries {
        executable {
            entryPoint = "rkncli.main"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-cli:$kotlinxCliVersion")
                implementation("io.rsocket.kotlin:rsocket-core:$rSocketVersion")
                implementation("io.rsocket.kotlin:rsocket-transport-ktor:$rSocketVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}
