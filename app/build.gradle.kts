plugins {
    id("com.android.application")
}

android {
    namespace = "com.pengxh.androidx.lib"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pengxh.androidx.lib"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(project(":lite"))
    //Google官方授权库
    implementation("pub.devrel:easypermissions:3.0.0")
    implementation("io.reactivex:rxjava:1.3.8")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.code.gson:gson:2.10.1")
    //沉浸式状态栏。基础依赖包，必须要依赖
    implementation("com.gyf.immersionbar:immersionbar:3.0.0")
    //图片选择框架
    implementation("io.github.lucksiege:pictureselector:v3.0.4")
    //图片加载库
    implementation("com.github.bumptech.glide:glide:4.12.0")
}