LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := UnitSDK

LOCAL_MODULE_FILENAME := libUnitSDK

LOCAL_SRC_FILES := 	main.cpp \
$(addprefix ../android/, \
	UnitSDKForJava.cpp \
	UnitSDKForJavaUtils.cpp \
)	\
                  
LOCAL_C_INCLUDES := $(LOCAL_PATH)/../include 
LOCAL_LDLIBS := -landroid
LOCAL_LDLIBS += -llog
LOCAL_LDLIBS += -lz  
LOCAL_STATIC_LIBRARIES := android_native_app_glue
LOCAL_WHOLE_STATIC_LIBRARIES += UnitSDKStatic

include $(BUILD_SHARED_LIBRARY)

$(call import-add-path,$(LOCAL_PATH))
$(call import-module,android/native_app_glue)
$(call import-module,../protocols/android)
