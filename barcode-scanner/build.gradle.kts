plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kotlinCocoapods)
}

group = "com.iago"
version = "1.0.0"

kotlin {
    android {
        namespace = "com.iago.barcode.scanner"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    iosArm64()

    cocoapods {
        version = "1.0.0"
        summary = "KMP PDF POC"
        homepage = "localhost"
        ios.deploymentTarget = "14.0"

        framework {
            baseName = "BarcodeScanner"
            isStatic = true
        }

        pod("MLKitBarcodeScanning")
        pod("MLKitVision")
        pod("MLKitCommon")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.app.profiler)
            implementation(libs.kitepdf)
            implementation(libs.kitepdf.native.renderer)
            implementation(libs.kitepdf.skia.renderer)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.play.services)
            implementation(libs.app.profiler)
            implementation(libs.mlkit.android)
            implementation(libs.pdfium)
            implementation(libs.filekit)
        }

        iosMain.dependencies {
            implementation(libs.app.profiler)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    coordinates(group.toString(), "barcode-scanner", version.toString())

    pom {
        name = "BarcodeScanner"
        description = "A library."
        inceptionYear = "2024"
        url = "https://github.com/kotlin/multiplatform-library-template/"
        licenses {
            license {
                name = "XXX"
                url = "YYY"
                distribution = "ZZZ"
            }
        }
        developers {
            developer {
                id = "XXX"
                name = "YYY"
                url = "ZZZ"
            }
        }
        scm {
            url = "XXX"
            connection = "YYY"
            developerConnection = "ZZZ"
        }
    }
}
