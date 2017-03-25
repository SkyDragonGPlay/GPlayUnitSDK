LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := UnitSDKStatic

LOCAL_MODULE_FILENAME := libUnitSDKStatic

LOCAL_SRC_FILES :=\
$(addprefix ../framework/platform/android/, \
	UnitSDKObject.cpp \
) \
$(addprefix ../framework/platform/utils/, \
    md5.cpp \
    unzip.cpp \
    ioapi.cpp \
    base64.cpp \
    Encode.cpp \
    JniHelper.cpp \
    Utils.cpp \
) \


LOCAL_CFLAGS := -std=c++11 -Wno-psabi

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../framework/include $(LOCAL_PATH)/../framework/platform/android $(LOCAL_PATH)/../framework/platform
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../framework/include $(LOCAL_PATH)/../framework/platform/android $(LOCAL_PATH)/../framework/platform

LOCAL_LDLIBS := -landroid
LOCAL_LDLIBS += -llog 
LOCAL_LDLIBS += -lz  
LOCAL_STATIC_LIBRARIES := android_native_app_glue
# define the macro to compile through support/zip_support/ioapi.c
LOCAL_CFLAGS   :=  -DUSE_FILE32API
LOCAL_CPPFLAGS := -Wno-deprecated-declarations -Wno-extern-c-compat
LOCAL_EXPORT_CFLAGS   := -DUSE_FILE32API
LOCAL_EXPORT_CPPFLAGS := -Wno-deprecated-declarations -Wno-extern-c-compat
include $(BUILD_STATIC_LIBRARY)

$(call import-module,android/native_app_glue)
