#include "JniHelper.h"
#include <string.h>
#include "Utils.h"

#include <android/log.h>

#define  LOG_TAG  "JniHelper"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


#define UNITSDK_JAVAVM  JniHelper::getJavaVM()

using namespace std;
namespace gplay { namespace framework {

extern "C"{
    static bool _getEnv(JNIEnv **env) {

        bool bRet = false;

        do {
			if(!UNITSDK_JAVAVM) {
                LOGE("JavaVM is NULL");
                break;
            }
			
            if (UNITSDK_JAVAVM->GetEnv((void**)env, JNI_VERSION_1_4) != JNI_OK) {
                LOGE("Failed to get the environment using GetEnv()");
                break;
            }

            if (UNITSDK_JAVAVM->AttachCurrentThread(env, 0) < 0) {
                LOGE("Failed to get the environment using AttachCurrentThread()");
                break;
            }
            bRet = true;
        } while (0);

        return bRet;
    }

    static jclass _getClassID(const char *className, JNIEnv *env) {
        JNIEnv *pEnv = env;
        jclass ret = 0;

        do {
            if (! pEnv) {
                pEnv = JniHelper::getEnv();
                if (! pEnv) {
                    break;
                }
            }

            ret = pEnv->FindClass(className);
            if (! ret) {
                LOGE("Failed to find class of %s", className);
                break;
            }
        } while (0);

        return ret;
    }
}

JavaVM* JniHelper::_psJavaVM = NULL;

JavaVM* JniHelper::getJavaVM() {
    return _psJavaVM;
}

void JniHelper::setJavaVM(JavaVM *javaVM) {
    _psJavaVM = javaVM;
}

JNIEnv* JniHelper::getEnv() {
    JNIEnv* ret = NULL;
    bool bRet = _getEnv(&ret);

    if (! bRet)
        ret = NULL;

    return ret;
}

bool JniHelper::getStaticMethodInfo(JniMethodInfo &methodinfo, const char *className, const char *methodName, const char *paramCode) {
    if ((NULL == className) ||
        (NULL == methodName) ||
        (NULL == paramCode)) {
        return false;
    }

    JNIEnv *pEnv = JniHelper::getEnv();
    if (!pEnv) {
        LOGE("Failed to get JNIEnv");
        return false;
    }

    jclass classID = _getClassID(className, pEnv);
    if (! classID) {
        LOGE("Failed to find class %s", className);
        return false;
    }

    jmethodID methodID = pEnv->GetStaticMethodID(classID, methodName, paramCode);
    if (! methodID) {
        if(pEnv->ExceptionCheck()) {
            pEnv->ExceptionClear();
        }
        LOGE("Failed to find static method id of %s", methodName);
        return false;
    }

    methodinfo.classID = classID;
    methodinfo.env = pEnv;
    methodinfo.methodID = methodID;
    return true;
}

bool JniHelper::getMethodInfo(JniMethodInfo &methodinfo, const char *className, const char *methodName, const char *paramCode) {
    if ((NULL == className) ||
        (NULL == methodName) ||
        (NULL == paramCode)) {
        return false;
    }

    JNIEnv *pEnv = JniHelper::getEnv();
    if (!pEnv) {
        return false;
    }

    jclass classID = _getClassID(className, pEnv);
    if (! classID) {
        LOGE("Failed to find class %s", className);
        return false;
        return false;
    }

    jmethodID methodID = pEnv->GetMethodID(classID, methodName, paramCode);

    if (! methodID) {
        if(pEnv->ExceptionCheck()) {
            pEnv->ExceptionClear();
        }
        LOGE("Failed to find method id of %s", methodName);
        return false;
    }

    methodinfo.classID = classID;
    methodinfo.env = pEnv;
    methodinfo.methodID = methodID;

    return true;
}

bool JniHelper::jobject2bool(jobject jobj) {
    if (jobj == NULL) {
        return "";
    }

    JNIEnv *env = JniHelper::getEnv();
    if (NULL == env) {
        return "";
    }

    jclass classID = env->FindClass("java/lang/Boolean");
    jmethodID methodID = env->GetMethodID(classID, "booleanValue", "()Z");
    bool ret = env->CallBooleanMethod(jobj, methodID);
    env->DeleteLocalRef(classID);
    return ret;
}

int JniHelper::jobject2int(jobject jobj) {
    if (jobj == NULL) {
        return -1;
    }

    JNIEnv *env = JniHelper::getEnv();
    if (NULL == env) {
        return -1;
    }

    jclass classID = env->FindClass("java/lang/Integer");
    jmethodID methodID = env->GetMethodID(classID, "intValue", "()I");
    int ret = env->CallIntMethod(jobj, methodID);
    env->DeleteLocalRef(classID);
    return ret;
}

float JniHelper::jobject2float(jobject jobj) {
    if (jobj == NULL) {
        return -1.0;
    }

    JNIEnv *env = JniHelper::getEnv();
    if (NULL == env) {
        return -1.0;
    }

    jclass classID = env->FindClass("java/lang/float");
    jmethodID methodID = env->GetMethodID(classID, "floatValue", "()F");
    int ret = env->CallIntMethod(jobj, methodID);
    env->DeleteLocalRef(classID);
    return ret;
}

string JniHelper::jstring2string(jstring jstr) {
    if (jstr == NULL) {
        return "";
    }

    JNIEnv *env = JniHelper::getEnv();
    if (NULL == env) {
        return "";
    }

    jclass classID = env->FindClass("java/lang/String");
    jstring strEncode = env->NewStringUTF( "utf-8");
    jmethodID methodID = env->GetMethodID(classID, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray bArr= (jbyteArray)env->CallObjectMethod(jstr, methodID, strEncode);
    jsize arrLen = env->GetArrayLength(bArr);

    if (arrLen > 0) {
        jbyte* arrBuf = env->GetByteArrayElements(bArr, NULL);
        std::string ret((const char*)arrBuf, (size_t)arrLen);
        env->ReleaseByteArrayElements(bArr, arrBuf, 0);

        env->DeleteLocalRef(classID);
        env->DeleteLocalRef(strEncode);
        env->DeleteLocalRef(bArr);
        return ret;
    }

    env->DeleteLocalRef(classID);
    env->DeleteLocalRef(strEncode);
    env->DeleteLocalRef(bArr);
    return "";
}

jstring JniHelper::newStringUTF(JNIEnv* env, const std::string& utf8Str) {
    if (NULL == env) {
        return NULL;
    }

    jclass clazz = env->FindClass("java/lang/String");
    jmethodID id = env->GetMethodID(clazz, "<init>", "([BLjava/lang/String;)V");

    jstring encoding = env->NewStringUTF( "utf-8");
    size_t utf8StrLen = utf8Str.length();
    jbyteArray bytes = env->NewByteArray(utf8StrLen);
    env->SetByteArrayRegion(bytes, 0, utf8StrLen, (jbyte*)utf8Str.c_str());

    jstring ret = (jstring)env->NewObject(clazz, id , bytes, encoding);

    env->DeleteLocalRef(bytes);
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(encoding);

    return ret;
}


bool JniHelper::getMethodInfo_DefaultClassLoader(JniMethodInfo &methodinfo,
                                                     const char *className,
                                                     const char *methodName,
                                                     const char *paramCode)
{
    if ((NULL == className) ||
        (NULL == methodName) ||
        (NULL == paramCode)) {
        return false;
    }

    JNIEnv *pEnv = JniHelper::getEnv();
    if (!pEnv) {
        return false;
    }

    jclass classID = pEnv->FindClass(className);
    if (! classID) {
        LOGE("Failed to find class %s", className);
        return false;
    }

    jmethodID methodID = pEnv->GetMethodID(classID, methodName, paramCode);
    if (! methodID) {
        LOGE("Failed to find method id of %s", methodName);
        return false;
    }

    methodinfo.classID = classID;
    methodinfo.env = pEnv;
    methodinfo.methodID = methodID;
    pEnv->DeleteLocalRef(classID);

    return true;
}

}} // gplay::framework::
