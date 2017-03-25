APP_PLATFORM := android-10
APP_STL := gnustl_static
APP_CPPFLAGS += -frtti
NDK_TOOLCHAIN_VERSION=clang
APP_MODULES := UnitSDKStatic
APP_ABI :=armeabi armeabi-v7a x86 arm64-v8a
APP_CPPFLAGS += -Wno-error=format-security

