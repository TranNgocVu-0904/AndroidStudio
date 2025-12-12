plugins {

    alias(libs.plugins.android.application)

    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp")

    id("org.jetbrains.kotlin.plugin.serialization")

}



android {

    namespace = "com.example.menuannam"

    compileSdk = 36



    defaultConfig {

        applicationId = "com.example.menuannam"

        minSdk = 26

        targetSdk = 36

        versionCode = 27

        versionName = "1.0"

        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"



    }

    // For Kotlin projects using KSP:

    ksp {

        arg("room.schemaLocation", "$projectDir/schemas")

    }




    buildTypes {

        release {

            isMinifyEnabled = false

            isDebuggable = false

            proguardFiles(

                getDefaultProguardFile("proguard-android-optimize.txt"),

                "proguard-rules.pro"

            )

        }

    }



    //compileOptions {

    //    sourceCompatibility = JavaVersion.VERSION_17 // Or your desired version

    //    targetCompatibility = JavaVersion.VERSION_17 // Or your desired version

    //}

    buildFeatures {

        compose = true

    }

    androidResources {

        generateLocaleConfig = true

    }

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_21 // Or your desired version

        targetCompatibility = JavaVersion.VERSION_21 // Or your desired version

    }

    testOptions {

        unitTests {

            isIncludeAndroidResources = true

        }

    }



}



configurations {

    create("cleanedAnnotations")

    implementation {

//        //exclude(group = "org.jetbrains", module = "annotations")

        exclude(group = "com.intellij", module = "annotations")

    }

}




dependencies {

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.material3)

    implementation(libs.androidx.navigation.runtime.ktx)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.navigation.testing)

    implementation(libs.core.ktx)

    implementation(libs.androidx.compose.ui.test.junit4)

    implementation(libs.androidx.room.compiler)

    implementation(libs.androidx.room.runtime)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.runtime)

    testImplementation(libs.junit)

    testImplementation(libs.robolectric)

    // Needed for createComposeRule(), but not for createAndroidComposeRule<YourActivity>():

    debugImplementation(libs.androidx.compose.ui.test.manifest)



    // androidTestImplementation(libs.androidx.junit)

    // androidTestImplementation(libs.androidx.espresso.core)

    // androidTestImplementation(platform(libs.androidx.compose.bom))

    // Test rules and transitive dependencies:

    // androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)

    // See Add the KSP plugin to your project

    ksp(libs.androidx.room.compiler)



    // optional - Kotlin Extensions and Coroutines support for Room

    implementation(libs.androidx.room.ktx)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0") // Or the latest version

    implementation("androidx.navigation:navigation-compose:2.9.6") // Or a later version


    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")




    // optional - Test helpers

    // testImplementation(libs.androidx.room.testing)

    // testImplementation(kotlin("test"))



}