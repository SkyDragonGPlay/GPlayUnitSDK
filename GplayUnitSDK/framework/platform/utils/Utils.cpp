#include "Utils.h"
#include <map>

#define MAX_LOG_LEN			2048
#define LOG_TAG "FLQG_LOG"

namespace gplay { namespace framework {

#define JAVAVM    gplay::framework::JniHelper::getJavaVM()

JNIEnv* Utils::getEnv() {
    bool bRet = false;
    JNIEnv* env = NULL;
    do {
        if (JAVAVM->GetEnv((void**)&env, JNI_VERSION_1_4) != JNI_OK) {
        	outputLog(ANDROID_LOG_DEBUG,"PluginUtils", "Failed to get the environment using GetEnv()");
            break;
        }

        if (JAVAVM->AttachCurrentThread(&env, 0) < 0) {
            outputLog(ANDROID_LOG_DEBUG,"PluginUtils", "Failed to get the environment using AttachCurrentThread()");
            break;
        }

        bRet = true;
    } while (0);        

    if (!bRet) {
    	env = NULL; 
    }

    return env;
}

void Utils::outputLog(int type, const char* logTag, const char* pFormat, ...) {
	char buf[MAX_LOG_LEN + 1];

	va_list args;
	va_start(args, pFormat);
	vsnprintf(buf, MAX_LOG_LEN, pFormat, args);
	va_end(args);

	std::string ret;
	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/skydragon/gplay/unitsdk/framework/util/Util"
		, "logLevel"
		, "()Ljava/lang/String;"))
	{
		jstring strRet = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
		ret = JniHelper::jstring2string(strRet);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(strRet);
	}

	if(ret.compare("verbose") == 0 ) {
		__android_log_print(type, LOG_TAG, "%s: %s",logTag, buf);
		return ;
	}

	if(ret.compare("debug") == 0 && type != ANDROID_LOG_VERBOSE) {
		__android_log_print(type, LOG_TAG, "%s: %s",logTag, buf);
		return ;
	}

	if(ret.compare("info") == 0 && (type == ANDROID_LOG_INFO ||type == ANDROID_LOG_ERROR || type == ANDROID_LOG_WARN)) {
		__android_log_print(type, LOG_TAG, "%s: %s",logTag, buf);
		return ;
	}

	if(ret.compare("warning") == 0 && (type == ANDROID_LOG_ERROR || type == ANDROID_LOG_WARN)) {
		__android_log_print(type, LOG_TAG, "%s: %s",logTag, buf);
		return ;
	}

	if(ret.compare("error") == 0 && type == ANDROID_LOG_ERROR) {
		__android_log_print(type, LOG_TAG, "%s: %s",logTag, buf);
		return ;
	}
    
}

}} // namespace gplay::framework 
