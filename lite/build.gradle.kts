plugins {
    id("com.android.library")
}

android {
    namespace = "com.pengxh.androidx.lite"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.squareup.retrofit2:adapter-rxjava:2.8.1")
    //返回值转换器
    implementation("com.squareup.retrofit2:converter-gson:2.8.1")
    implementation("com.squareup.retrofit2:converter-scalars:2.3.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    //网络请求和接口封装
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //okhttp3日志拦截器
    implementation("com.squareup.okhttp3:logging-interceptor:4.6.0")
    //图片加载
    implementation("com.github.bumptech.glide:glide:4.12.0")
    //官方Json解析库
    implementation("com.google.code.gson:gson:2.10.1")
    //Kotlin协程
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    //MVVM+LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    //Socket
    implementation("io.netty:netty-all:4.1.23.Final")
    //汉字转拼音（多音字无法完美转换）
    implementation("com.belerweb:pinyin4j:2.5.0")
    //CameraX
    //CameraX Camera2 extensions
    implementation("androidx.camera:camera-camera2:1.2.3")
}