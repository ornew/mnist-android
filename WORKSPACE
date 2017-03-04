android_sdk_repository(
    name="androidsdk",
#    path="/abs/path/to/android-sdk",
#    api_level = 25,
)

android_ndk_repository(
    name = "androidndk",
#    path = "/abs/path/to/android-ndk",
#    api_level = 21,
)

git_repository(
    name = "org_tensorflow",
    remote = "https://github.com/tensorflow/tensorflow.git",
    init_submodules = 1,
    tag = "v1.0.0"
)

load("@org_tensorflow//tensorflow:workspace.bzl", "tf_workspace")
tf_workspace()
