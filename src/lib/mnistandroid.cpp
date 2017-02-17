#include <fstream>
#include <vector>
#include <string>
#include <memory>
#include <mutex>
#include <thread>

#include <android/log.h>
#include <jni.h>

#include "tensorflow/core/platform/env.h"
#include "tensorflow/core/platform/logging.h"
#include "tensorflow/core/public/session.h"
#include "tensorflow/core/util/stat_summarizer.h"

#define MNIST_ANDROID_NATIVE_METHOD(METHOD_NAME) Java_net_ornew_mnistandroid_JNI_##METHOD_NAME

namespace mnistandroid {
  static constexpr size_t mnist_input_size = 784;
  static constexpr size_t mnist_labels_size = 10;
  static char const* TAG = "MNISTANDROID";
}

using namespace tensorflow;
using namespace std;
using namespace mnistandroid;

namespace Log {
  template<typename... T>
  void print(android_LogPriority level, char const* tag, T... args){
    __android_log_print(level, tag, args...);
  }
  template<typename... T>
  void i(T... args){
    print(ANDROID_LOG_INFO, TAG, args...);
  }
}

unique_ptr<Session> global_session;

extern "C" JNIEXPORT void JNICALL MNIST_ANDROID_NATIVE_METHOD(initialize)(JNIEnv* env, jclass clazz, jstring j_model_path){
  const char* const model_path = env->GetStringUTFChars(j_model_path, NULL);
  Status status;

  SessionOptions options;
  ConfigProto& config = options.config;
  Log::i("Got config, %d devices.", config.device_count_size());

  // Initialize session
  global_session.reset(NewSession(options));
  Log::i("Session created.");

  // Load graph
  GraphDef graph;
  Log::i("Graph created.");
  Log::i("Reading file to proto: %s", model_path);
  status = ReadBinaryProto(Env::Default(), model_path, &graph);
  if (!status.ok()) {
    env->FatalError("Could not read model file.");
  }
  Log::i("Creating session with graph.");

  // Create session
  status = global_session->Create(graph);
  if (!status.ok()) {
    Log::i(status.error_message().c_str());
    env->FatalError("Could not create TensorFlow Graph");
  }

  // Clear graph used memory
  graph.Clear();
  Log::i("TensorFlow graph loaded from: %s", model_path);
}

extern "C" JNIEXPORT jfloatArray JNICALL MNIST_ANDROID_NATIVE_METHOD(inference)(JNIEnv* env, jclass clazz, jfloatArray j_x){
  jsize input_length = env->GetArrayLength(j_x);
  Log::i("Input length: %d", (int)input_length);

  if(input_length != mnist_input_size){}

  float inputs[mnist_input_size];
  env->GetFloatArrayRegion(j_x, 0, mnist_input_size, reinterpret_cast<jfloat*>(inputs));

  Status status;

  // Tensors
  Tensor x(DT_FLOAT, TensorShape({1, mnist_input_size}));
  Tensor keep_prob(DT_FLOAT, TensorShape());

  // Initialize Tensors
  auto _x = x.flat<float>();
  for(int i = 0; i < mnist_input_size; ++i){
    _x(i) = inputs[i];
  }
  keep_prob.scalar<float>()() = 1.0f;

  // Input and Output
  vector<pair<string, Tensor>> feed_dict({
    {"x", x},
    {"keep_prob", keep_prob}
  });
  vector<Tensor> outputs;

  // Run session
  status = global_session->Run(feed_dict, {"readout/y:0"}, {}, &outputs);
  if(!status.ok()){
    Log::i(status.error_message().c_str());
    env->FatalError("Failed to execute runnning to inference operation.");
  }

  // Get and return output
  float y[mnist_labels_size];
  auto output0 = outputs[0];
  auto _y = output0.flat<float>();
  for(int i = 0; i < mnist_labels_size; ++i){
    y[i] = _y(i);
  }
  jfloatArray j_y = env->NewFloatArray(mnist_labels_size);
  if(j_y == NULL){
    return NULL;
  }
  env->SetFloatArrayRegion(j_y, 0, mnist_labels_size, y);
  return j_y;
}
