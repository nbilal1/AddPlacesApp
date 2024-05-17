plugins {
    id("com.android.application")

    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.moutamid.addplacesapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.moutamid.addplaces"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        setProperty("archivesBaseName", "AddPlacesApp-$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.fxn769:stash:1.3.2")
    implementation("com.google.firebase:firebase-database:19.3.0")
    implementation("com.google.firebase:firebase-storage:19.1.1")
    implementation("com.google.android.gms:play-services-auth:21.1.1")
    implementation ("com.airbnb.android:lottie:6.4.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.github.ome450901:SimpleRatingBar:1.5.1")

    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
}