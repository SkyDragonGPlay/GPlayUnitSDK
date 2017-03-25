#include "UnitSDKForJava.h"
#include "UnitSDKForJavaUtils.h"
#include <jni.h>
#include <android/log.h>
#include "JniHelper.h"
#include "Utils.h"

#include <stdlib.h>

#define LOG_TAG "UnitSDK"
#define EMPTY_PARAM (vector<TypeParam*>())

using namespace std;
namespace gplay{ namespace framework{

void AsyncActionResultListener::onActionResult(UnitSDK* pUnitSDK, ActionResultCode code, const char* msg, map<string, string> infoMap) {
    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t,
                                       "com/skydragon/gplay/unitsdk/framework/java/GplayUnitSDK",
                                       "onAsyncFuncResult",
                                       "(ILjava/lang/String;Ljava/lang/String;)V"))
    {
        jint jCode = static_cast<int>(code);
        jstring jMsg = t.env->NewStringUTF(msg);
        std::string sCallbackId = infoMap["callbackId"];
        jstring jCallbackId = t.env->NewStringUTF(sCallbackId.c_str());

        t.env->CallStaticVoidMethod(t.classID, t.methodID, jCode, jMsg, jCallbackId);
        t.env->DeleteLocalRef( jMsg );
        t.env->DeleteLocalRef( jCallbackId );
    }
}

extern "C" {

    void Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeInit( JNIEnv* env, jobject thiz, jstring appkey, jstring appSecret, jstring privateKey) {
        string sAppKey = JniHelper::jstring2string(appkey);
        string sAppSecret = JniHelper::jstring2string(appSecret);
        string sPrivateKey = JniHelper::jstring2string(privateKey);
        UnitSDKForJava::getInstance()->init( sAppKey, sAppSecret, sPrivateKey );
    }

    jstring Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeGetChannelId( JNIEnv* env, jobject thiz ) {
        return env->NewStringUTF(UnitSDKForJava::getInstance()->getChannelId().c_str());
    }

    void Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeLogin( JNIEnv* env, jobject thiz, jstring callbackId) {
        string strCallbackId = JniHelper::jstring2string(callbackId);
        UnitSDKForJava::getInstance()->login(strCallbackId);
    }

    void Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativePayForProduct( JNIEnv* env, jobject thiz, jstring callbackId, jobject jParams) {
        string strCallbackId = JniHelper::jstring2string(callbackId);
        map<string, string> info = UnitSDKForJavaUtils::getInstance()->jobject2Map(env, jParams);

        UnitSDKForJava::getInstance()->payForProduct(strCallbackId, info);
    }

    void Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeShare( JNIEnv* env, jobject thiz, jstring callbackId, jobject jParams) {
        string strCallbackId = JniHelper::jstring2string(callbackId);
        map<string, string> info = UnitSDKForJavaUtils::getInstance()->jobject2Map(env, jParams);

        UnitSDKForJava::getInstance()->share(strCallbackId, info);
    }

    void Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeCreateShortcut( JNIEnv* env, jobject thiz, jstring callbackId, jobject jParams) {
        string strCallbackId = JniHelper::jstring2string(callbackId);
        map<string, string> info = UnitSDKForJavaUtils::getInstance()->jobject2Map(env, jParams);

        UnitSDKForJava::getInstance()->createShortcut(strCallbackId, info);
    }

    jstring Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeGetUserId( JNIEnv* env, jobject thiz ) {

        string userID = UnitSDKForJava::getInstance()->getUserId();
        return env->NewStringUTF(userID.c_str());
    }

    jboolean Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeIsLogined( JNIEnv* env, jobject thiz ) {
        return (jboolean) UnitSDKForJava::getInstance()->isLogined();
    }

    jboolean Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeIsFunctionSupported( JNIEnv* env, jobject thiz, jstring functionName) {
        string strFunctionName = JniHelper::jstring2string(functionName);
        return UnitSDKForJava::getInstance()->isFunctionSupported( strFunctionName );
    }

    void Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeSetDebugMode( JNIEnv* env, jobject thiz, jboolean bDebug) {
        UnitSDKForJava::getInstance()->setDebugMode((bool)bDebug);
    }


    // native call xxx function

    void Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeCallSyncFunction( JNIEnv* env, jobject thiz, jstring functionName, jobject jParams) {
        string strFunctionName = JniHelper::jstring2string(functionName);

        if( UnitSDKForJava::getInstance()->isFunctionSupported( strFunctionName ) ) {
            if (NULL == jParams) {
                UnitSDKForJava::getInstance()->callSyncFunc(strFunctionName.c_str(), EMPTY_PARAM);
            } else {
                vector<TypeParam> data = UnitSDKForJavaUtils::getInstance()->jobject2TypeParam(env, jParams);
                vector<TypeParam*> params;
                for(int i = 0; i < data.size(); i++ ) {
                    params.push_back(&data[i]);
                }
                UnitSDKForJava::getInstance()->callSyncFunc(strFunctionName.c_str(), params);
            }
        }
    }

    jboolean Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeCallSyncBoolFunction( JNIEnv* env, jobject thiz, jstring functionName, jobject jParams) {
        string strFunctionName = JniHelper::jstring2string(functionName);

        if( UnitSDKForJava::getInstance()->isFunctionSupported( strFunctionName ) ) {
            if (NULL == jParams) {
                return (jboolean)UnitSDKForJava::getInstance()->callSyncBoolFunc(strFunctionName.c_str(), EMPTY_PARAM);
            } else {
                vector<TypeParam> data = UnitSDKForJavaUtils::getInstance()->jobject2TypeParam(env, jParams);
                vector<TypeParam*> params;
                for(int i = 0; i < data.size(); i++ ) {
                    params.push_back(&data[i]);
                }
                return (jboolean)UnitSDKForJava::getInstance()->callSyncBoolFunc(strFunctionName.c_str(), params);
            }
        }
    }

    jint Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeCallSyncIntFunction( JNIEnv* env, jobject thiz, jstring functionName, jobject jParams) {
        string strFunctionName = JniHelper::jstring2string(functionName);

        if( UnitSDKForJava::getInstance()->isFunctionSupported( strFunctionName ) ) {
            if (NULL == jParams) {
                return (jint)UnitSDKForJava::getInstance()->callSyncIntFunc(strFunctionName.c_str(), EMPTY_PARAM);
            } else {
                vector<TypeParam> data = UnitSDKForJavaUtils::getInstance()->jobject2TypeParam(env, jParams);
                vector<TypeParam*> params;
                for(int i = 0; i < data.size(); i++ ) {
                    params.push_back(&data[i]);
                }
                return (jint)UnitSDKForJava::getInstance()->callSyncIntFunc(strFunctionName.c_str(), params);
            }
        }
    }

    jfloat Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeCallSyncFloatFunction( JNIEnv* env, jobject thiz, jstring functionName, jobject jParams) {
        string strFunctionName = JniHelper::jstring2string(functionName);

        if( UnitSDKForJava::getInstance()->isFunctionSupported( strFunctionName ) ) {
            if (NULL == jParams) {
                return (jfloat)UnitSDKForJava::getInstance()->callSyncFloatFunc(strFunctionName.c_str(), EMPTY_PARAM);
            } else {
                vector<TypeParam> data = UnitSDKForJavaUtils::getInstance()->jobject2TypeParam(env, jParams);
                vector<TypeParam*> params;
                for(int i = 0; i < data.size(); i++ ) {
                    params.push_back(&data[i]);
                }
                return (jfloat)UnitSDKForJava::getInstance()->callSyncFloatFunc(strFunctionName.c_str(), params);
            }
        }
    }

    jstring Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeCallSyncStringFunction( JNIEnv* env, jobject thiz, jstring functionName, jobject jParams){
        string strFunctionName = JniHelper::jstring2string(functionName);
        if( UnitSDKForJava::getInstance()->isFunctionSupported( strFunctionName ) ) {
            if (NULL == jParams) {
                string res = UnitSDKForJava::getInstance()->callSyncStringFunc(strFunctionName.c_str(), EMPTY_PARAM);
                return env->NewStringUTF(res.c_str());
            } else {
                vector<TypeParam> data = UnitSDKForJavaUtils::getInstance()->jobject2TypeParam(env, jParams);
                vector<TypeParam*> params;
                for(int i = 0; i < data.size(); i++ ) {
                    params.push_back(&data[i]);
                }
                string res = UnitSDKForJava::getInstance()->callSyncStringFunc(strFunctionName.c_str(), params);
                return env->NewStringUTF(res.c_str());
            }
        }
    }

    void Java_com_skydragon_gplay_unitsdk_framework_java_GplayUnitSDK_nativeCallAsyncFunction( JNIEnv* env, jobject thiz, jstring callbackId,  jstring functionName, jobject jParams){
        string strCallbackId = JniHelper::jstring2string(callbackId);
        string strFunctionName = JniHelper::jstring2string(functionName);

        if( UnitSDKForJava::getInstance()->isFunctionSupported( strFunctionName ) ) {
            if (NULL == jParams) {
                UnitSDKForJava::getInstance()->callAsyncFunc(strCallbackId.c_str(), strFunctionName.c_str(), EMPTY_PARAM);
            } else {
                vector<TypeParam> data = UnitSDKForJavaUtils::getInstance()->jobject2TypeParam(env, jParams);
                vector<TypeParam*> params;
                for(int i = 0; i < data.size(); i++ ) {
                    params.push_back(&data[i]);
                }
                UnitSDKForJava::getInstance()->callAsyncFunc(strCallbackId.c_str(), strFunctionName.c_str(), params);
            }
        }
    }

} // extern "C"

AsyncActionResultListener* UnitSDKForJava::_pAsyncActionResultListener = new AsyncActionResultListener();

UnitSDKForJava* UnitSDKForJava::_pUnitSdkForJavaInstance = NULL;

UnitSDKForJava::UnitSDKForJava() {
    _pUnitSdkInstance = UnitSDK::getInstance();
}

UnitSDKForJava::~UnitSDKForJava() {
    delete _pUnitSdkInstance;
    delete _pUnitSdkForJavaInstance;
}

UnitSDKForJava* UnitSDKForJava::getInstance() {
    if (_pUnitSdkForJavaInstance == NULL) {
        _pUnitSdkForJavaInstance = new UnitSDKForJava();
    }
    return _pUnitSdkForJavaInstance;
}

void UnitSDKForJava::init( string appkey, string appSecret, string privateKey ) {
    if(!_pUnitSdkInstance) return;
    _pUnitSdkInstance->init( appkey, appSecret, privateKey );
    _pUnitSdkInstance->setAsyncActionResultListener(_pAsyncActionResultListener);
}

string UnitSDKForJava::getChannelId() {
    if(!_pUnitSdkInstance) return "";
    return _pUnitSdkInstance->getChannelId();
}

void UnitSDKForJava::login(string callbackId) {
    if(!_pUnitSdkInstance) return;
    _pUnitSdkInstance->login(callbackId);
}

bool UnitSDKForJava::isLogined() {
    if(!_pUnitSdkInstance) return false;
    return _pUnitSdkInstance->isLogined();
}

string UnitSDKForJava::getUserId() {
    if(!_pUnitSdkInstance) return "";
    return _pUnitSdkInstance->getUserID();
}

string UnitSDKForJava::getOrderId() {
    if(!_pUnitSdkInstance) return "";
    return _pUnitSdkInstance->getOrderId();
}

void UnitSDKForJava::payForProduct(string callbackId, map<string, string> info) {
    if(!_pUnitSdkInstance) return;
    return _pUnitSdkInstance->payForProduct(callbackId, info);
}

void UnitSDKForJava::resetPayState() {
    _pUnitSdkInstance->resetPayState();
}

void UnitSDKForJava::share(string callbackId, map<string, string> info) {
    if(!_pUnitSdkInstance) return ;
    _pUnitSdkInstance->share(callbackId, info);
}

void UnitSDKForJava::createShortcut(string callbackId, map<string, string> info) {
    if(!_pUnitSdkInstance) return ;
    _pUnitSdkInstance->createShortCut(callbackId, info);
}

//check 支持某函数
bool UnitSDKForJava::isFunctionSupported( string functionName ) {
    return _pUnitSdkInstance->isFunctionSupported( functionName );
}

void UnitSDKForJava::setDebugMode(bool bDebug) {
    isDebug = bDebug;
}

// 调用 UnitSDK 的扩展接口

void UnitSDKForJava::callSyncFunc(char const* funcName, vector<TypeParam*> params) {
    _pUnitSdkInstance->callSyncFunc(funcName, params);
}

string UnitSDKForJava::callSyncStringFunc(const char* funcName, vector<TypeParam*> params) {
    return _pUnitSdkInstance->callSyncStringFunc(funcName, params);
}

int UnitSDKForJava::callSyncIntFunc(const char* funcName, vector<TypeParam*> params) {
    return _pUnitSdkInstance->callSyncIntFunc(funcName, params);
}

bool UnitSDKForJava::callSyncBoolFunc(const char* funcName, vector<TypeParam*> params) {
    return _pUnitSdkInstance->callSyncBoolFunc(funcName, params);
}

float UnitSDKForJava::callSyncFloatFunc(const char* funcName, vector<TypeParam*> params) {
    return _pUnitSdkInstance->callSyncFloatFunc(funcName, params);
}

void UnitSDKForJava::callAsyncFunc(char const* callbackId, char const* functionName, vector<TypeParam*> params) {
    _pUnitSdkInstance->callAsyncFunc(callbackId, functionName, params);
}

}} //namespace gplay::framework::
