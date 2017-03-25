#ifndef __PLUGIN_UTILS_H__
#define __PLUGIN_UTILS_H__

#include "JniHelper.h"
#include <map>
#include <list>
#include <vector>
#include "TypeParam.h"
#include <android/log.h>
//#include <android_native_app_glue.h>

namespace gplay { namespace framework {

class Utils
{
public:
//cjh  static void initPluginWrapper(android_app* app);
    static jobject createJavaMapObject(std::map<std::string, std::string>* paramMap);
    static jobject createJavaListObject(std::list<std::string>* paramList);
    static jobject createJavaListObject();
    static jobject createJavaListObject(std::vector<TypeParam*> paramList);
    static JNIEnv* getEnv();

    static jobject getJObjFromParam(TypeParam* param);


    static void outputLog(int type, const char* logTag, const char* pFormat, ...);
};

}}

#endif //__PLUGIN_UTILS_H__
