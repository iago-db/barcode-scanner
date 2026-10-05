pluginManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }

//    resolutionStrategy {
//        eachPlugin {
//            if (requested.id.id == "org.jetbrains.kotlin.multiplatform") {
//                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
//            }
//        }
//    }
}

dependencyResolutionManagement {
    repositories {
//        maven {
//            url = uri("C:/Users.T11510/Documents/maven")
//
//            content {
//                includeGroup("org.jetbrains.kotlin")
//            }
//        }

//        maven {
//            url = uri("https://repobins.apps.prof.banrisul.com.br/repository/gradle-downloads/distributions/")
//        }
//
//        maven {
//            url = uri("https://repobins.apps.prof.banrisul.com.br/repository/maven-public/")
//        }

        google()
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

includeBuild("/Users/UDS/Documents/KitePDF") {
    dependencySubstitution {
        substitute(module("io.github.yuroyami:kitepdf"))
            .using(project(":kitepdf"))

        substitute(module("io.github.yuroyami:kitepdf-native-renderer"))
            .using(project(":kitepdf-native-renderer"))

        substitute(module("io.github.yuroyami:kitepdf-skia-renderer"))
            .using(project(":kitepdf-skia-renderer"))
    }
}

rootProject.name = "barcode-scanner"
include(":barcode-scanner")
