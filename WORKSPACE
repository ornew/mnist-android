android_sdk_repository(
    name="androidsdk",
    path="/home/ornew/lib/android-sdk-25.2.3",
    api_level = 25,
    build_tools_version="25.0.2"
)

android_ndk_repository(
    name = "androidndk",
    path = "/home/ornew/lib/android-ndk-r13b",
    api_level = 21)

git_repository(
    name = "org_tensorflow",
    remote = "https://github.com/tensorflow/tensorflow.git",
    init_submodules = 1,
    tag = "v0.12.0"
)

load("@org_tensorflow//tensorflow:workspace.bzl", "tf_workspace")
tf_workspace()
