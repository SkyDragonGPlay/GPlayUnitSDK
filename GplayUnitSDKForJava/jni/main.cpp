#include <jni.h>
#include <android/log.h>
#include "JniHelper.h"

#define  LOG_TAG    "main"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

using namespace gplay::framework;
extern "C"
{

jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    JniHelper::setJavaVM(vm);  // for plugins
    return JNI_VERSION_1_4;
}

}
