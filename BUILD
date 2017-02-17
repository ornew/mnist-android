package(default_visibility = ["//visibility:public"])

filegroup(
    name = "srcs",
    srcs = glob(["src/**"]),
    visibility = ["//examples:__pkg__"],
)

android_binary (
  name = "app",
  srcs = glob(["src/app/java/**/*.java"]),
  custom_package = "net.ornew.mnistandroid",
  manifest = "src/app/AndroidManifest.xml",
  resource_files = glob(["src/app/res/**"]),
  deps = [
    ":jni",
    "@androidsdk//com.android.support:appcompat-v7-25.0.0",
  ],
)

cc_library(
  name = "jni",
  srcs = [":libmnistandroid.so"],
)

cc_binary(
  name = "libmnistandroid.so",
  srcs = ["src/lib/mnistandroid.cpp"],
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
