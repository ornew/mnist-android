# MNIST for Android

Run the TensorFlow MNIST model in Android application.

## How to Build

You need to install the following tools in advance.

- Android SDK
- Android NDK
- Bazel

Fix WORKSPACE.

```:WORKSPACE
android_sdk_repository(
    name="androidsdk",
    path="</absolute/path/to/android-sdk>",
    api_level = <api level>,
)

android_ndk_repository(
    name = "androidndk",
    path = "</absolute/path/to/android-ndk>",
    api_level = 21,
)
```

Please install the following items.

- Specified version of SDK Platform Tools
- SDK Build Tools
- Google Support Library

Run build.

```bash
bazel build :app
```

`app.apk` is generated in `./bazel-bin`.

If you want to install the application:

```bash
bazel mobile-install :app
```

or

```bash
adb install -r bazel-bin/app.apk
```

## Contacts

Arata Furukawa \<info@ornew.net\>
