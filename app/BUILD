package(default_visibility = ["//visibility:public"])

filegroup(
  name = "srcs",
  srcs = glob(["app/**"]),
  visibility = ["//examples:__pkg__"],
)

android_binary (
  name = "net.ornew.mnist.app",
  srcs = glob(["java/**/*.java"]),
  custom_package = "net.ornew.mnist.app",
  manifest = "AndroidManifest.xml",
  resource_files = glob(["res/**"]),
  deps = [
    "//lib:net.ornew.mnist",
    "@androidsdk//com.android.support:appcompat-v7-25.0.0",
  ],
)
