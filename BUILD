package(default_visibility = ["//visibility:public"])

android_binary (
  name = "app",
  srcs = glob(["src/app/java/**/*.java"]),
  custom_package = "net.ornew.mnistandroid",
  manifest = "src/app/AndroidManifest.xml",
  resource_files = glob(["src/app/res/**"]),
  deps = [
    ":lib",
    "@androidsdk//com.android.support:appcompat-v7-25.0.0",
  ],
)

cc_library(
    name = "lib",
    srcs = [
      "src/lib/mnistandroid.cpp",
    ],
    hdrs = ["src/lib/mnistandroid.h"],
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
    linkstatic=0,
    tags = [
        "manual",
        "notap",
    ],
    deps = ["@org_tensorflow//tensorflow/core:android_tensorflow_lib"],
)
