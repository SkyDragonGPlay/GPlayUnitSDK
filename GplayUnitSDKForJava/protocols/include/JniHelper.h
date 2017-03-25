#ifndef __JNI_HELPER_H__
#define __JNI_HELPER_H__

#include <jni.h>
#include <string>

namespace gplay {namespace framework{

typedef struct PluginJniMethodInfo_
{
    JNIEnv *    env;
    jclass      classID;
    jmethodID   methodID;
} JniMethodInfo;

class JniHelper
{
public:
    static JavaVM* getJavaVM();
    static void setJavaVM(JavaVM *javaVM);
    static JNIEnv* getEnv();

    static bool getStaticMethodInfo(JniMethodInfo &methodinfo, const char *className, const char *methodName, const char *paramCode);
    static bool getMethodInfo(JniMethodInfo &methodinfo, const char *className, const char *methodName, const char *paramCode);
    static std::string jstring2string(jstring jstr);
    static jstring newStringUTF(JNIEnv* env, const std::string& utf8Str);
    static bool jobject2bool(jobject obj);
    static int jobject2int(jobject obj);
    static float jobject2float(jobject obj);

private:
    static JavaVM *_psJavaVM;
    static bool getMethodInfo_DefaultClassLoader(JniMethodInfo &methodinfo,
                                                 const char *className,
                                                 const char *methodName,
                                                 const char *paramCode);
};

}}

#endif // __JNI_HELPER_H__
