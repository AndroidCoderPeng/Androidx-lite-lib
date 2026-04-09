//
// Created by Administrator on 2026/4/7.
//

#include <jni.h>
#include <android/bitmap.h>

#include "logger.hpp"

static std::unique_ptr<Logger> logger_ptr;

extern "C"
JNIEXPORT jint
JNI_OnLoad(JavaVM *vm, void *reserved) {
    logger_ptr = std::make_unique<Logger>("JNI-DevCore");
    logger_ptr->i("JNI_OnLoad");
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void
JNI_OnUnload(JavaVM *vm, void *reserved) {
    logger_ptr->i("JNI_OnUnload");
    if (logger_ptr) {
        logger_ptr.reset();
    }
}