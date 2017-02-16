#ifndef MNISTANDROID_H
#define MNISTANDROID_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif// __cplusplus

#define MNIST_ANDROID_NATIVE_METHOD(METHOD_NAME) Java_net_ornew_mnistandroid_JNI_##METHOD_NAME

JNIEXPORT void        JNICALL MNIST_ANDROID_NATIVE_METHOD(initialize)(JNIEnv* env, jclass clazz, jstring j_model_path);
JNIEXPORT jfloatArray JNICALL MNIST_ANDROID_NATIVE_METHOD(inference)(JNIEnv* env, jclass clazz, jfloatArray j_x);

#ifdef __cplusplus
}
#endif// __cplusplus

namespace mnistandroid {
  static constexpr size_t mnist_input_size = 784;
  static constexpr size_t mnist_labels_size = 10;
  static char const* TAG = "MNISTANDROID";
}

#endif// MNISTANDROID_H
