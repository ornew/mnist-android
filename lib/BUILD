package(default_visibility = ["//visibility:public"])

android_library(
  name = "net.ornew.mnist",
  custom_package = "net.ornew.mnist",
  srcs = glob(["src/MNIST.java"]),
  deps = [":jni"],
)

cc_library(
  name = "jni",
  srcs = [":libmnist.so"],
)

cc_binary(
  name = "libmnist.so",
  srcs = glob(["src/mnist.cpp"]),
  copts = [
    "-std=gnu++11",
    "-fno-exceptions",
    "-DEIGEN_AVOID_STL_ARRAY",
    "-DSELECTIVE_REGISTRATION",
    "-mfpu=neon",
    "-DMIN_LOG_LEVEL=0",
    "-DTF_LEAN_BINARY",
    "-O2",
    "-fPIE",
  ],
  linkopts = [
    "-landroid",
    "-ljnigraphics",
    "-llog",
    "-lm",
    "-z defs",
    "-s",
    "-Wl,--icf=all",
    "-Wl,--exclude-libs,ALL",
  ],
  linkshared = 1,
  linkstatic = 1,
  tags = [
    "manual",
    "notap",
  ],
  deps = ["@org_tensorflow//tensorflow/core:android_tensorflow_lib"],
)
